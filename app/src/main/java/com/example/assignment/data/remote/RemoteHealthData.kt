package com.example.assignment.data.remote

import com.example.assignment.data.UserAccount
import com.example.assignment.data.UserHealthGoals
import com.example.assignment.data.UserNotificationSettings
import com.example.assignment.model.MealEntry
import com.example.assignment.model.SleepRecord
import com.example.assignment.model.WaterRecord
import com.example.assignment.model.WorkoutItem
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The remote (Supabase) data source. Every method is the remote twin of a
 * method in [com.example.assignment.data.UserHealthData] / UserData.
 *
 * All calls follow the same shape as Practical 9:
 *   supabase.from("table").select { filter { eq("col", value) } }.decodeList<Dto>()
 *   supabase.from("table").insert(dto)
 *   supabase.from("table").update(dto) { filter { ... } }
 *   supabase.from("table").delete { filter { ... } }
 *
 * When Supabase is not configured yet (see [SupabaseModule.isConfigured]) every
 * method quietly does nothing, so the app still runs on Room alone.
 */
object RemoteHealthData {

    private val db get() = SupabaseModule.client
    private val enabled get() = SupabaseModule.isConfigured

    // Table names (must match the SQL in ROOM_SUPABASE_GUIDE.md)
    private const val PROFILES = "profiles"
    private const val MEALS = "meals"
    private const val WORKOUTS = "workouts"
    private const val SLEEP = "sleep"
    private const val WATER = "water"
    private const val GOALS = "goals"
    private const val NOTIFICATION_SETTINGS = "notification_settings"

    // ---------- profiles (account) ----------

    suspend fun upsertProfile(user: UserAccount) = io {
        db.from(PROFILES).upsert(ProfileDto.fromModel(user))
    }

    suspend fun fetchProfileByEmail(email: String): UserAccount? = ioResult {
        db.from(PROFILES).select {
            filter { eq("email", email) }
        }.decodeList<ProfileDto>().firstOrNull()?.toModel()
    }

    // ---------- meals ----------

    /** Returns null if the server could not be reached (so the caller keeps local data). */
    suspend fun fetchMeals(userId: Int): List<MealEntry>? = ioResult {
        db.from(MEALS).select { filter { eq("user_id", userId) } }
            .decodeList<MealDto>().map { it.toModel() }
    }

    suspend fun insertMeal(userId: Int, meal: MealEntry) = io {
        db.from(MEALS).insert(MealDto.fromModel(userId, meal))
    }

    suspend fun updateMeal(userId: Int, meal: MealEntry) = io {
        db.from(MEALS).update(MealDto.fromModel(userId, meal)) {
            filter { eq("user_id", userId); eq("id", meal.id) }
        }
    }

    suspend fun deleteMeal(userId: Int, mealId: Int) = io {
        db.from(MEALS).delete { filter { eq("user_id", userId); eq("id", mealId) } }
    }

    // ---------- workouts ----------

    suspend fun fetchWorkouts(userId: Int): List<WorkoutItem>? = ioResult {
        db.from(WORKOUTS).select { filter { eq("user_id", userId) } }
            .decodeList<WorkoutDto>().map { it.toModel() }
    }

    suspend fun insertWorkout(userId: Int, workout: WorkoutItem) = io {
        db.from(WORKOUTS).insert(WorkoutDto.fromModel(userId, workout))
    }

    suspend fun updateWorkout(userId: Int, workout: WorkoutItem) = io {
        db.from(WORKOUTS).update(WorkoutDto.fromModel(userId, workout)) {
            filter { eq("user_id", userId); eq("id", workout.id) }
        }
    }

    suspend fun deleteWorkout(userId: Int, workoutId: String) = io {
        db.from(WORKOUTS).delete { filter { eq("user_id", userId); eq("id", workoutId) } }
    }

    // ---------- sleep ----------

    suspend fun fetchSleep(userId: Int): List<SleepRecord>? = ioResult {
        db.from(SLEEP).select { filter { eq("user_id", userId) } }
            .decodeList<SleepDto>().map { it.toModel() }
    }

    suspend fun insertSleep(userId: Int, record: SleepRecord) = io {
        db.from(SLEEP).insert(SleepDto.fromModel(userId, record))
    }

    suspend fun updateSleep(userId: Int, record: SleepRecord) = io {
        db.from(SLEEP).update(SleepDto.fromModel(userId, record)) {
            filter { eq("user_id", userId); eq("id", record.id) }
        }
    }

    suspend fun deleteSleep(userId: Int, sleepId: Int) = io {
        db.from(SLEEP).delete { filter { eq("user_id", userId); eq("id", sleepId) } }
    }

    // ---------- water ----------

    suspend fun fetchWater(userId: Int): List<WaterRecord>? = ioResult {
        db.from(WATER).select { filter { eq("user_id", userId) } }
            .decodeList<WaterDto>().map { it.toModel() }
    }

    suspend fun insertWater(userId: Int, record: WaterRecord) = io {
        db.from(WATER).insert(WaterDto.fromModel(userId, record))
    }

    suspend fun updateWater(userId: Int, record: WaterRecord) = io {
        db.from(WATER).update(WaterDto.fromModel(userId, record)) {
            filter { eq("user_id", userId); eq("water_id", record.waterId) }
        }
    }

    suspend fun deleteWater(userId: Int, waterId: Int) = io {
        db.from(WATER).delete { filter { eq("user_id", userId); eq("water_id", waterId) } }
    }

    // ---------- goals ----------

    suspend fun fetchGoals(userId: Int): UserHealthGoals? = ioResult {
        db.from(GOALS).select {
            filter { eq("user_id", userId) }
        }.decodeList<GoalsDto>().firstOrNull()?.toModel()
    }

    suspend fun upsertGoals(userId: Int, goals: UserHealthGoals) = io {
        db.from(GOALS).upsert(GoalsDto.fromModel(userId, goals))
    }

    // ---------- notification settings ----------

    suspend fun fetchNotificationSettings(userId: Int): UserNotificationSettings? = ioResult {
        db.from(NOTIFICATION_SETTINGS).select {
            filter { eq("user_id", userId) }
        }.decodeList<NotificationSettingsDto>().firstOrNull()?.toModel()
    }

    suspend fun upsertNotificationSettings(userId: Int, settings: UserNotificationSettings) = io {
        db.from(NOTIFICATION_SETTINGS).upsert(NotificationSettingsDto.fromModel(userId, settings))
    }

    // ---------- helpers ----------

    /** Run a fire-and-forget write on the IO dispatcher; never throw. */
    private suspend inline fun io(crossinline block: suspend () -> Unit) {
        if (!enabled) return
        try {
            withContext(Dispatchers.IO) { block() }
        } catch (e: Exception) {
            android.util.Log.w("RemoteHealthData", "remote write failed: ${e.message}")
        }
    }

    /** Run a read on the IO dispatcher; return null on any failure. */
    private suspend inline fun <T> ioResult(crossinline block: suspend () -> T): T? {
        if (!enabled) return null
        return try {
            withContext(Dispatchers.IO) { block() }
        } catch (e: Exception) {
            android.util.Log.w("RemoteHealthData", "remote read failed: ${e.message}")
            null
        }
    }
}
