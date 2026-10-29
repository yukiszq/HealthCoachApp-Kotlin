package com.example.assignment.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.assignment.data.local.AppDatabase
import com.example.assignment.data.local.entity.UserEntity
import com.example.assignment.data.remote.RemoteHealthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class UserAccount(
    val id: Int,
    val fullName: String,
    val email: String,
    val password: String,
    val age: Int = 0,
    val gender: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val profileImageUri: String = "",
    val profileSetupCompleted: Boolean = false
)
enum class ResetPasswordResult {
    SUCCESS,
    ACCOUNT_NOT_FOUND,
    SAME_PASSWORD
}
object UserData {


    var currentUser by mutableStateOf<UserAccount?>(null)
        private set

    private var sessionManager: SessionManager? = null

    suspend fun initialize(context: Context) {
        if (sessionManager == null) {
            sessionManager = SessionManager(context)
        }

        // The app always starts at the login screen: do NOT restore a previous
        // session, and clear any saved one so a stale login can't linger.
        currentUser = null
        sessionManager?.clearSession()
    }

    /**
     * A stable, positive id derived from the email, so the same account gets the
     * same id on every device. This is what keeps Supabase data in sync across
     * devices and stops two people who register on fresh installs from colliding.
     */
    private fun stableId(email: String): Int {
        val h = email.trim().lowercase().hashCode() and 0x7FFFFFFF
        return if (h == 0) 1 else h
    }

    suspend fun registerUser(
        context: Context,
        fullName: String,
        email: String,
        password: String
    ): Boolean = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)
        val cleanEmail = email.trim()

        // Already registered on this device, or already registered on Supabase
        // from another device? Then this should be a login, not a register.
        if (db.userDao().getUserByEmail(cleanEmail) != null) return@withContext false
        val remoteExisting =
            runCatching { RemoteHealthData.fetchProfileByEmail(cleanEmail) }.getOrNull()
        if (remoteExisting != null) return@withContext false

        val newUser = UserEntity(
            id = stableId(cleanEmail),
            fullName = fullName.trim(),
            email = cleanEmail,
            passwordHash = password // Note: In a real app, hash the password!
        )

        // 1. Local (Room)
        db.userDao().upsertUser(newUser)

        // 2. Remote (Supabase) — best effort, never blocks registration
        runCatching {
            RemoteHealthData.upsertProfile(newUser.toUserAccount())
        }
        return@withContext true
    }

    suspend fun loginUser(
        context: Context,
        email: String,
        password: String
    ): Boolean = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)

        // Local account first (works offline).
        var user = db.userDao().getUserByEmail(email.trim())?.toUserAccount()

        // Not on this device? Try to pull it from Supabase (e.g. new phone).
        if (user == null) {
            val remote = runCatching { RemoteHealthData.fetchProfileByEmail(email.trim()) }.getOrNull()
            if (remote != null) {
                db.userDao().upsertUser(remote.toUserEntity())
                user = remote
            }
        }

        val account = user
        if (account != null && account.password == password) {
            withContext(Dispatchers.Main) { currentUser = account }
            if (sessionManager == null) sessionManager = SessionManager(context)
            sessionManager?.saveSession(account.id)
            return@withContext true
        }
        return@withContext false
    }

    suspend fun updateProfile(
        context: Context,
        fullName: String,
        email: String,
        age: Int,
        gender: String,
        height: Double,
        weight: Double,
        profileImageUri: String? = null,
        completeSetup: Boolean? = null
    ) = withContext(Dispatchers.IO) {
        val user = currentUser ?: return@withContext
        val updatedUser = user.copy(
            fullName = fullName.trim(),
            email = email.trim(),
            age = age,
            gender = gender,
            height = height,
            weight = weight,
            profileImageUri = profileImageUri ?: user.profileImageUri,
            profileSetupCompleted = completeSetup ?: user.profileSetupCompleted
        )

        val db = AppDatabase.getDatabase(context)
        db.userDao().updateUser(updatedUser.toUserEntity())
        runCatching { RemoteHealthData.upsertProfile(updatedUser) }

        withContext(Dispatchers.Main) {
            currentUser = updatedUser
        }
    }

    suspend fun completeProfileSetup(context: Context) {
        val user = currentUser ?: return
        updateProfile(
            context = context,
            fullName = user.fullName,
            email = user.email,
            age = user.age,
            gender = user.gender,
            height = user.height,
            weight = user.weight,
            completeSetup = true
        )
    }

    fun needsProfileSetup(): Boolean {
        return currentUser?.profileSetupCompleted?.not() ?: false
    }
    suspend fun changePassword(
        context: Context,
        currentPassword: String,
        newPassword: String
    ): Boolean = withContext(Dispatchers.IO) {
        val user = currentUser ?: return@withContext false

        // check old password first
        if (user.password != currentPassword) return@withContext false

        val updatedUser = user.copy(password = newPassword)
        val db = AppDatabase.getDatabase(context)

        // save local
        db.userDao().updateUser(updatedUser.toUserEntity())

        // save supabase
        runCatching {
            RemoteHealthData.upsertProfile(updatedUser)
        }

        withContext(Dispatchers.Main) {
            currentUser = updatedUser
        }

        true
    }

    suspend fun resetPassword(
        context: Context,
        email: String,
        newPassword: String
    ): ResetPasswordResult = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)
        val cleanEmail = email.trim()

        // try local first
        var user = db.userDao().getUserByEmail(cleanEmail)?.toUserAccount()

        // not found then try supabase
        if (user == null) {
            user = runCatching {
                RemoteHealthData.fetchProfileByEmail(cleanEmail)
            }.getOrNull()
        }

        if (user == null) return@withContext ResetPasswordResult.ACCOUNT_NOT_FOUND

        // cannot use back the old password
        if (user.password == newPassword) {
            return@withContext ResetPasswordResult.SAME_PASSWORD
        }

        val updatedUser = user.copy(password = newPassword)

        // save both sides
        db.userDao().upsertUser(updatedUser.toUserEntity())

        runCatching {
            RemoteHealthData.upsertProfile(updatedUser)
        }

        ResetPasswordResult.SUCCESS
    }

    fun logout() {
        currentUser = null
        sessionManager?.clearSession()
    }

    private fun UserEntity.toUserAccount() = UserAccount(
        id = id,
        fullName = fullName,
        email = email,
        password = passwordHash,
        age = age,
        gender = gender,
        height = height,
        weight = weight,
        profileImageUri = profileImageUri,
        profileSetupCompleted = profileSetupCompleted
    )

    private fun UserAccount.toUserEntity() = UserEntity(
        id = id,
        fullName = fullName,
        email = email,
        passwordHash = password,
        age = age,
        gender = gender,
        height = height,
        weight = weight,
        profileImageUri = profileImageUri,
        profileSetupCompleted = profileSetupCompleted
    )
}
