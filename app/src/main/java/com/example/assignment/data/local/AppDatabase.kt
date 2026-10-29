package com.example.assignment.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.assignment.data.local.dao.GoalsDao
import com.example.assignment.data.local.dao.MealDao
import com.example.assignment.data.local.dao.NotificationSettingsDao
import com.example.assignment.data.local.dao.SleepDao
import com.example.assignment.data.local.dao.UserDao
import com.example.assignment.data.local.dao.WaterDao
import com.example.assignment.data.local.dao.WorkoutDao
import com.example.assignment.data.local.entity.GoalsEntity
import com.example.assignment.data.local.entity.MealEntity
import com.example.assignment.data.local.entity.NotificationSettingsEntity
import com.example.assignment.data.local.entity.SleepEntity
import com.example.assignment.data.local.entity.UserEntity
import com.example.assignment.data.local.entity.WaterEntity
import com.example.assignment.data.local.entity.WorkoutEntity

/**
 * The single Room database for the whole app (local data storage).
 * Follows the singleton pattern taught in RoomDbTest (Practical 7).
 *
 * version 2: added meal / workout / sleep / water / goals / notification-settings.
 * version 3: users.id is no longer auto-generated (it is derived from the email).
 */
@Database(
    entities = [
        UserEntity::class,
        MealEntity::class,
        WorkoutEntity::class,
        SleepEntity::class,
        WaterEntity::class,
        GoalsEntity::class,
        NotificationSettingsEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun mealDao(): MealDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun sleepDao(): SleepDao
    abstract fun waterDao(): WaterDao
    abstract fun goalsDao(): GoalsDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "health_coach_database"
                )
                    // This is a student project with no shipped users, so on a
                    // schema change we just recreate the tables instead of
                    // writing migrations.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
