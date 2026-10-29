package com.example.assignment.data

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.assignment.data.local.AppDatabase
import com.example.assignment.data.local.entity.GoalsEntity
import com.example.assignment.data.local.entity.MealEntity
import com.example.assignment.data.local.entity.NotificationSettingsEntity
import com.example.assignment.data.local.entity.SleepEntity
import com.example.assignment.data.local.entity.WaterEntity
import com.example.assignment.data.local.entity.WorkoutEntity
import com.example.assignment.data.remote.RemoteHealthData
import com.example.assignment.model.AchievementItem
import com.example.assignment.model.MealEntry
import com.example.assignment.model.SleepRecord
import com.example.assignment.model.WaterRecord
import com.example.assignment.model.WorkoutItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class UserHealthGoals(
    val waterGoal: Int = 2000,
    val mealGoal: Int = 2200,
    val exerciseGoal: Int = 60,
    val sleepGoal: Int = 480
)

data class UserNotificationSettings(
    val waterReminder: Boolean = false,
    val waterTime: String = "10:00",
    val mealReminder: Boolean = false,
    val mealTime: String = "12:30",
    val exerciseReminder: Boolean = false,
    val exerciseTime: String = "18:00",
    val sleepReminder: Boolean = false,
    val sleepTime: String = "22:30",
    val achievementNotification: Boolean = false
)

/**
 * Single in-memory cache for every user's tracker data, kept exactly the same
 * shape as before so no screen has to change.
 *
 * What is new: the cache is now backed by storage.
 *   - Every change is written through to **Room** (local) and **Supabase**
 *     (remote) in the background — see [persist].
 *   - [loadForUser] pulls the data back (Supabase -> Room -> memory) after login
 *     and on app start, so the data survives an app restart and can follow the
 *     user to another device.
 */
object UserHealthData {

    private val mealsByUser =
        mutableStateMapOf<Int, SnapshotStateList<MealEntry>>()

    private val workoutsByUser =
        mutableStateMapOf<Int, SnapshotStateList<WorkoutItem>>()

    private val sleepByUser =
        mutableStateMapOf<Int, SnapshotStateList<SleepRecord>>()

    private val waterByUser =
        mutableStateMapOf<Int, SnapshotStateList<WaterRecord>>()

    private val goalsByUser =
        mutableStateMapOf<Int, UserHealthGoals>()

    private val notificationsByUser =
        mutableStateMapOf<Int, UserNotificationSettings>()

    // ---------------------------------------------------------------------
    // Storage plumbing
    // ---------------------------------------------------------------------

    private var appContext: Context? = null
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Call once from MainActivity before the UI is shown. */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private fun database(): AppDatabase? =
        appContext?.let { AppDatabase.getDatabase(it) }

    /** Run a background write to Room + Supabase; never crash the UI. */
    private fun persist(block: suspend () -> Unit) {
        ioScope.launch {
            try {
                block()
            } catch (e: Exception) {
                Log.w("UserHealthData", "persist failed: ${e.message}")
            }
        }
    }

    /**
     * Load everything for [userId]: Supabase -> Room -> memory.
     * Safe to call again (it replaces the cached lists).
     *
     * If the server is reachable, Room is made an exact mirror of the server
     * (so records deleted on another device also disappear here). If the server
     * cannot be reached, the local Room data is left untouched (offline-first).
     */
    suspend fun loadForUser(userId: Int) = coroutineScope {
        val db = database() ?: return@coroutineScope

        // 1. Pull from the server — all six requests in parallel, not one by one.
        val meals = async { RemoteHealthData.fetchMeals(userId) }
        val workouts = async { RemoteHealthData.fetchWorkouts(userId) }
        val sleep = async { RemoteHealthData.fetchSleep(userId) }
        val water = async { RemoteHealthData.fetchWater(userId) }
        val goals = async { RemoteHealthData.fetchGoals(userId) }
        val settings = async { RemoteHealthData.fetchNotificationSettings(userId) }

        // 2. Mirror into Room. `null` = server unreachable -> keep local as-is.
        meals.await()?.let { remote ->
            db.mealDao().clearForUser(userId)
            remote.forEach { db.mealDao().insert(MealEntity.fromModel(userId, it)) }
        }
        workouts.await()?.let { remote ->
            db.workoutDao().clearForUser(userId)
            remote.forEach { db.workoutDao().insert(WorkoutEntity.fromModel(userId, it)) }
        }
        sleep.await()?.let { remote ->
            db.sleepDao().clearForUser(userId)
            remote.forEach { db.sleepDao().insert(SleepEntity.fromModel(userId, it)) }
        }
        water.await()?.let { remote ->
            db.waterDao().clearForUser(userId)
            remote.forEach { db.waterDao().insert(WaterEntity.fromModel(userId, it)) }
        }
        goals.await()?.let { db.goalsDao().upsert(GoalsEntity.fromModel(userId, it)) }
        settings.await()?.let {
            db.notificationSettingsDao().upsert(NotificationSettingsEntity.fromModel(userId, it))
        }

        // 3. Read Room (the local source of truth) into the in-memory cache.
        loadFromRoom(userId)
    }

    /**
     * Fill the in-memory cache straight from Room, without touching the network.
     * Used by background components (e.g. the reminder receiver) that may run in
     * a fresh process where the cache is empty.
     */
    suspend fun loadFromRoom(userId: Int) {
        val db = database() ?: return

        val mealRows = db.mealDao().getMealsForUser(userId).map { it.toModel() }
        val workoutRows = db.workoutDao().getWorkoutsForUser(userId).map { it.toModel() }
        val sleepRows = db.sleepDao().getSleepForUser(userId).map { it.toModel() }
        val waterRows = db.waterDao().getWaterForUser(userId).map { it.toModel() }
        val goalRow = db.goalsDao().getGoalsForUser(userId)?.toModel() ?: UserHealthGoals()
        val settingsRow = db.notificationSettingsDao().getSettingsForUser(userId)?.toModel()
            ?: UserNotificationSettings()

        meals(userId).apply { clear(); addAll(mealRows) }
        // addWorkout() puts the newest workout first, so show newest first here too.
        // (workout ids are time-based, so a larger id = more recent.)
        workouts(userId).apply {
            clear()
            addAll(workoutRows.sortedByDescending { it.id.toLongOrNull() ?: 0L })
        }
        sleepRecords(userId).apply { clear(); addAll(sleepRows) }
        waterRecords(userId).apply { clear(); addAll(waterRows) }
        goalsByUser[userId] = goalRow
        notificationsByUser[userId] = settingsRow
    }

    // ---------------------------------------------------------------------
    // Reads (unchanged)
    // ---------------------------------------------------------------------

    fun meals(userId: Int): SnapshotStateList<MealEntry> {
        return mealsByUser.getOrPut(userId) {
            mutableStateListOf()
        }
    }

    fun workouts(userId: Int): SnapshotStateList<WorkoutItem> {
        return workoutsByUser.getOrPut(userId) {
            mutableStateListOf()
        }
    }

    fun sleepRecords(userId: Int): SnapshotStateList<SleepRecord> {
        return sleepByUser.getOrPut(userId) {
            mutableStateListOf()
        }
    }

    fun waterRecords(userId: Int): SnapshotStateList<WaterRecord> {
        return waterByUser.getOrPut(userId) {
            mutableStateListOf()
        }
    }

    fun goals(userId: Int): UserHealthGoals {
        return goalsByUser.getOrPut(userId) {
            UserHealthGoals()
        }
    }

    fun notificationSettings(userId: Int): UserNotificationSettings {
        return notificationsByUser.getOrPut(userId) {
            UserNotificationSettings()
        }
    }

    // ---------------------------------------------------------------------
    // Writes (in-memory first, then written through to Room + Supabase)
    // ---------------------------------------------------------------------

    fun updateGoals(
        userId: Int,
        water: Int,
        meal: Int,
        exercise: Int,
        sleep: Int
    ) {
        val goals = UserHealthGoals(
            waterGoal = water,
            mealGoal = meal,
            exerciseGoal = exercise,
            sleepGoal = sleep
        )
        goalsByUser[userId] = goals
        persist {
            database()?.goalsDao()?.upsert(GoalsEntity.fromModel(userId, goals))
            RemoteHealthData.upsertGoals(userId, goals)
        }
    }

    fun updateNotifications(
        userId: Int,
        waterReminder: Boolean,
        waterTime: String,
        mealReminder: Boolean,
        mealTime: String,
        exerciseReminder: Boolean,
        exerciseTime: String,
        sleepReminder: Boolean,
        sleepTime: String,
        achievementNotification: Boolean
    ) {
        val settings = UserNotificationSettings(
            waterReminder = waterReminder,
            waterTime = waterTime,
            mealReminder = mealReminder,
            mealTime = mealTime,
            exerciseReminder = exerciseReminder,
            exerciseTime = exerciseTime,
            sleepReminder = sleepReminder,
            sleepTime = sleepTime,
            achievementNotification = achievementNotification
        )
        notificationsByUser[userId] = settings
        persist {
            database()?.notificationSettingsDao()
                ?.upsert(NotificationSettingsEntity.fromModel(userId, settings))
            RemoteHealthData.upsertNotificationSettings(userId, settings)
        }
    }

    fun addMeal(userId: Int, meal: MealEntry) {
        meals(userId).add(meal)
        persist {
            database()?.mealDao()?.insert(MealEntity.fromModel(userId, meal))
            RemoteHealthData.insertMeal(userId, meal)
        }
    }

    fun updateMeal(userId: Int, updatedMeal: MealEntry) {
        val list = meals(userId)
        val index = list.indexOfFirst { it.id == updatedMeal.id }
        if (index != -1) {
            list[index] = updatedMeal
        }
        persist {
            database()?.mealDao()?.update(MealEntity.fromModel(userId, updatedMeal))
            RemoteHealthData.updateMeal(userId, updatedMeal)
        }
    }

    fun deleteMeal(userId: Int, mealId: Int) {
        meals(userId).removeAll { it.id == mealId }
        persist {
            database()?.mealDao()?.deleteById(userId, mealId)
            RemoteHealthData.deleteMeal(userId, mealId)
        }
    }

    fun addWorkout(userId: Int, workout: WorkoutItem) {
        workouts(userId).add(0, workout)
        persist {
            database()?.workoutDao()?.insert(WorkoutEntity.fromModel(userId, workout))
            RemoteHealthData.insertWorkout(userId, workout)
        }
    }

    fun updateWorkout(userId: Int, workout: WorkoutItem) {
        val list = workouts(userId)
        val index = list.indexOfFirst { it.id == workout.id }
        if (index != -1) {
            list[index] = workout
        }
        persist {
            database()?.workoutDao()?.update(WorkoutEntity.fromModel(userId, workout))
            RemoteHealthData.updateWorkout(userId, workout)
        }
    }

    fun deleteWorkout(userId: Int, workoutId: String) {
        workouts(userId).removeAll { it.id == workoutId }
        persist {
            database()?.workoutDao()?.deleteById(userId, workoutId)
            RemoteHealthData.deleteWorkout(userId, workoutId)
        }
    }

    fun addSleep(userId: Int, record: SleepRecord) {
        sleepRecords(userId).add(record)
        persist {
            database()?.sleepDao()?.insert(SleepEntity.fromModel(userId, record))
            RemoteHealthData.insertSleep(userId, record)
        }
    }

    fun updateSleep(userId: Int, updatedRecord: SleepRecord) {
        val list = sleepRecords(userId)
        val index = list.indexOfFirst { it.id == updatedRecord.id }
        if (index != -1) {
            list[index] = updatedRecord
        }
        persist {
            database()?.sleepDao()?.update(SleepEntity.fromModel(userId, updatedRecord))
            RemoteHealthData.updateSleep(userId, updatedRecord)
        }
    }

    fun deleteSleep(userId: Int, sleepId: Int) {
        sleepRecords(userId).removeAll { it.id == sleepId }
        persist {
            database()?.sleepDao()?.deleteById(userId, sleepId)
            RemoteHealthData.deleteSleep(userId, sleepId)
        }
    }

    fun addWater(userId: Int, record: WaterRecord) {
        val stored = record.copy(userId = userId)
        waterRecords(userId).add(stored)
        persist {
            database()?.waterDao()?.insert(WaterEntity.fromModel(userId, stored))
            RemoteHealthData.insertWater(userId, stored)
        }
    }

    fun updateWater(userId: Int, updatedRecord: WaterRecord) {
        val stored = updatedRecord.copy(userId = userId)
        val list = waterRecords(userId)
        val index = list.indexOfFirst { it.waterId == stored.waterId }
        if (index != -1) {
            list[index] = stored
        }
        persist {
            database()?.waterDao()?.update(WaterEntity.fromModel(userId, stored))
            RemoteHealthData.updateWater(userId, stored)
        }
    }

    fun deleteWater(userId: Int, waterId: Int) {
        waterRecords(userId).removeAll { it.waterId == waterId }
        persist {
            database()?.waterDao()?.deleteById(userId, waterId)
            RemoteHealthData.deleteWater(userId, waterId)
        }
    }

    // ---------------------------------------------------------------------
    // Derived reads (unchanged)
    // ---------------------------------------------------------------------

    fun waterForDate(
        userId: Int,
        date: String
    ): List<WaterRecord> {
        return waterRecords(userId).filter {
            getWaterDate(it.loggedAt) == date
        }
    }

    fun mealsForDate(
        userId: Int,
        date: String
    ): List<MealEntry> {
        return meals(userId).filter {
            it.date == date
        }
    }

    fun workoutsForDate(
        userId: Int,
        date: String
    ): List<WorkoutItem> {
        return workouts(userId).filter {
            it.date == date
        }
    }

    fun sleepForDate(
        userId: Int,
        date: String
    ): List<SleepRecord> {
        return sleepRecords(userId).filter {
            it.date == date
        }
    }

    fun waterTotal(userId: Int, date: String): Int {
        return waterForDate(userId, date).sumOf {
            it.intakeAmount
        }
    }

    fun mealTotal(userId: Int, date: String): Int {
        return mealsForDate(userId, date).sumOf {
            it.kcal
        }
    }

    fun exerciseMinutes(userId: Int, date: String): Double {
        val totalSecs = workoutsForDate(userId, date).sumOf {
            getDurationInSeconds(it.duration)
        }
        return totalSecs / 60.0
    }

    fun exerciseCalories(userId: Int, date: String): Int {
        return workoutsForDate(userId, date).sumOf {
            it.calories
        }
    }

    fun sleepMinutes(userId: Int, date: String): Int {
        return sleepForDate(userId, date).sumOf {
            it.durationMinutes
        }
    }

    fun achievements(userId: Int): List<AchievementItem> {
        val goals = goals(userId)

        val waterDays =
            waterRecords(userId)
                .groupBy {
                    getWaterDate(it.loggedAt)
                }
                .count { (_, records) ->
                    records.sumOf {
                        it.intakeAmount
                    } >= goals.waterGoal
                }

        val sleepDays =
            sleepRecords(userId)
                .groupBy {
                    it.date
                }
                .count { (_, records) ->
                    records.sumOf {
                        it.durationMinutes
                    } >= goals.sleepGoal
                }

        val exerciseDays =
            workouts(userId)
                .filter {
                    it.date.isNotBlank()
                }
                .groupBy {
                    it.date
                }
                .count { (_, records) ->
                    val totalMinutes =
                        records.sumOf {
                            getDurationInSeconds(it.duration)
                        } / 60.0

                    totalMinutes >= goals.exerciseGoal
                }

        val mealDays =
            meals(userId)
                .filter {
                    it.date.isNotBlank()
                }
                .groupBy {
                    it.date
                }
                .count { (_, records) ->
                    records.sumOf {
                        it.kcal
                    } >= goals.mealGoal
                }

        val allDates =
            (
                    meals(userId).map {
                        it.date
                    } +
                            workouts(userId).map {
                                it.date
                            } +
                            sleepRecords(userId).map {
                                it.date
                            } +
                            waterRecords(userId).map {
                                getWaterDate(it.loggedAt)
                            }
                    )
                .filter {
                    it.isNotBlank()
                }
                .distinct()

        val healthyLifestyleDays =
            allDates.count { date ->
                waterTotal(userId, date) >= goals.waterGoal &&
                        mealTotal(userId, date) >= goals.mealGoal &&
                        exerciseMinutes(userId, date) >= goals.exerciseGoal &&
                        sleepMinutes(userId, date) >= goals.sleepGoal
            }

        return listOf(
            AchievementItem(
                id = 1,
                title = "Hydration Hero",
                description = "Reach your water goal for 14 days.",
                progress = waterDays.coerceAtMost(14),
                target = 14,
                unlocked = waterDays >= 14,
                icon = Icons.Default.WaterDrop
            ),
            AchievementItem(
                id = 2,
                title = "Sleep Champion",
                description = "Reach your sleep goal for 10 days.",
                progress = sleepDays.coerceAtMost(10),
                target = 10,
                unlocked = sleepDays >= 10,
                icon = Icons.Default.NightsStay
            ),
            AchievementItem(
                id = 3,
                title = "Active Week",
                description = "Reach your exercise goal for 7 days.",
                progress = exerciseDays.coerceAtMost(7),
                target = 7,
                unlocked = exerciseDays >= 7,
                icon = Icons.Default.FitnessCenter
            ),
            AchievementItem(
                id = 4,
                title = "Meal Tracker",
                description = "Reach your calorie goal for 10 days.",
                progress = mealDays.coerceAtMost(10),
                target = 10,
                unlocked = mealDays >= 10,
                icon = Icons.Default.Restaurant
            ),
            AchievementItem(
                id = 5,
                title = "Healthy Lifestyle",
                description = "Complete all four health goals for 3 days.",
                progress = healthyLifestyleDays.coerceAtMost(3),
                target = 3,
                unlocked = healthyLifestyleDays >= 3,
                icon = Icons.Default.EmojiEvents
            )
        )
    }

    fun hasAnyData(userId: Int): Boolean {
        return meals(userId).isNotEmpty() ||
                workouts(userId).isNotEmpty() ||
                sleepRecords(userId).isNotEmpty() ||
                waterRecords(userId).isNotEmpty()
    }
    fun seedDemoDataIfNeeded(userId: Int) {

        // Do not add demo data again if this account already has tracker records
        if (hasAnyData(userId)) {
            return
        }

        val goals = goals(userId)

        // Previous 13 days
        val previousDays = (1..13).map {
            getDemoDate(it)
        }

        // -------------------------------------------------
        // WATER
        // 13 completed days = Hydration Hero 13/14
        // -------------------------------------------------
        previousDays.forEachIndexed { index, date ->

            addWater(
                userId,
                WaterRecord(
                    waterId = 700001 + index,
                    userId = userId,
                    intakeAmount = goals.waterGoal,
                    unit = "ml",
                    loggedAt = "$date 08:00 AM"
                )
            )
        }

        // Today is almost complete.
        // Demo: add another 250ml -> Hydration Hero unlocks.
        addWater(
            userId,
            WaterRecord(
                waterId = 700100,
                userId = userId,
                intakeAmount = (goals.waterGoal - 250).coerceAtLeast(0),
                unit = "ml",
                loggedAt = "${getDemoDate(0)} 08:00 AM"
            )
        )

        // -------------------------------------------------
        // SLEEP
        // 9 completed days = Sleep Champion 9/10
        // -------------------------------------------------
        previousDays.take(9).forEachIndexed { index, date ->

            addSleep(
                userId,
                SleepRecord(
                    id = 710001 + index,
                    date = date,
                    sleepType = "Night Sleep",
                    bedtime = "11:00 PM",
                    wakeUpTime = "07:00 AM",
                    durationMinutes = goals.sleepGoal,
                    quality = 4,
                    notes = "Demo record"
                )
            )
        }

        // -------------------------------------------------
        // EXERCISE
        // 7 completed days = Active Week 7/7
        // -------------------------------------------------
        val exerciseDemoDays =
            listOf(1, 2, 10, 11, 12, 13, 14)

        exerciseDemoDays.forEachIndexed { index, daysAgo ->

            addWorkout(
                userId,
                WorkoutItem(
                    id = (720001 + index).toString(),
                    title = "Demo Workout",
                    time = "06:00 PM",
                    duration = "${goals.exerciseGoal} mins",
                    calories = 250,
                    distanceKm = null,
                    date = getDemoDate(daysAgo)
                )
            )
        }

        // Today's exercise goal is already completed for demo
        addWorkout(
            userId,
            WorkoutItem(
                id = "720100",
                title = "Demo Workout",
                time = "06:00 PM",
                duration = "${goals.exerciseGoal} mins",
                calories = 250,
                distanceKm = null,
                date = getDemoDate(0)
            )
        )

        // -------------------------------------------------
        // MEAL
        // 9 completed days = Meal Tracker 9/10
        // -------------------------------------------------
        val foods = listOf(
            "Chicken Rice",
            "Sandwich",
            "Fried Rice",
            "Noodles",
            "Chicken Wrap",
            "Pasta"
        )

        previousDays.take(9).forEachIndexed { index, date ->

            addMeal(
                userId,
                MealEntry(
                    id = 730001 + index,
                    mealType = "Lunch",
                    foodName = foods[index % foods.size],
                    kcal = goals.mealGoal,
                    protein = 25,
                    carbs = 60,
                    fat = 15,
                    fiber = 5,
                    time = "12:30 PM",
                    date = date,
                    notes = "Demo record",
                    imageUri = ""
                )
            )
        }
        // Today is almost complete.
        // Demo: log another 250 kcal meal -> Meal Tracker unlocks.
        addMeal(
            userId,
            MealEntry(
                id = 730100,
                mealType = "Breakfast",
                foodName = "Demo Meal Progress",
                kcal = (goals.mealGoal - 250).coerceAtLeast(0),
                protein = 25,
                carbs = 60,
                fat = 15,
                fiber = 5,
                time = "08:00 AM",
                date = getDemoDate(0),
                notes = "Demo record",
                imageUri = ""
            )
        )
    }

    private fun getDurationInSeconds(duration: String): Double {
        val trimmed = duration.trim().lowercase()
        val minsMatch = Regex("""(\d+)\s*(?:min|mins|m)""").find(trimmed)
        val secsMatch = Regex("""(\d+)\s*(?:sec|secs|s)""").find(trimmed)

        var totalSeconds = 0.0
        if (minsMatch != null) {
            totalSeconds += (minsMatch.groupValues[1].toDoubleOrNull() ?: 0.0) * 60.0
        }
        if (secsMatch != null) {
            totalSeconds += secsMatch.groupValues[1].toDoubleOrNull() ?: 0.0
        }

        if (minsMatch == null && secsMatch == null) {
            totalSeconds = trimmed.toDoubleOrNull() ?: 0.0
        }
        return totalSeconds
    }

    private fun getWaterDate(loggedAt: String): String {
        return if (loggedAt.length >= 10) {
            loggedAt.take(10)
        } else {
            ""
        }
    }

    private fun getDemoDate(daysAgo: Int): String {

        val calendar = Calendar.getInstance()

        calendar.add(
            Calendar.DAY_OF_YEAR,
            -daysAgo
        )

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(calendar.time)
    }

}
