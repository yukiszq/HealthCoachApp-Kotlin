package com.example.assignment.nav

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment.AboutScreen
import com.example.assignment.AchievementsScreen
import com.example.assignment.HealthGoalsScreen
import com.example.assignment.BmiCalculatorScreen
import com.example.assignment.HelpSupportScreen
import com.example.assignment.InsightScreen
import com.example.assignment.auth.LoginScreen
import com.example.assignment.auth.RegisterScreen
import com.example.assignment.data.UserData
import com.example.assignment.data.UserHealthData
import com.example.assignment.exercisescreen.CalculatingSessionScreen
import com.example.assignment.exercisescreen.CustomActivityScreen
import com.example.assignment.mealscreen.DetectResultScreen
import com.example.assignment.mealscreen.EditMealScreen
import com.example.assignment.exercisescreen.ExerciseTrackerMainScreen
import com.example.assignment.exercisescreen.HomeScreen
import com.example.assignment.exercisescreen.SuccessPillSnackbar
import com.example.assignment.mealscreen.LogMealScreen
import com.example.assignment.mealscreen.MealDetailScreen
import com.example.assignment.exercisescreen.SelectActivityScreen
import com.example.assignment.mealscreen.TrackerScreen
import com.example.assignment.exercisescreen.WorkoutDetailScreen
import com.example.assignment.exercisescreen.WorkoutHistoryScreen
import com.example.assignment.model.WorkoutItem
import com.example.assignment.notification.AchievementNotifier
import com.example.assignment.notification.NotificationTester
import com.example.assignment.notification.NotificationsScreen
import com.example.assignment.notification.ReminderScheduler
import com.example.assignment.profilescreen.EditProfileScreen
import com.example.assignment.profilescreen.ProfileScreen
import com.example.assignment.profilescreen.ProfileSetupScreen
import com.example.assignment.sleeptrackerscreen.AddSleepRecordScreen
import com.example.assignment.sleeptrackerscreen.SleepDetail
import com.example.assignment.sleeptrackerscreen.SleepTrackerScreen
import com.example.assignment.util.getCurrentDate
import com.example.assignment.util.getCurrentTime
import com.example.assignment.waterscreen.AddWaterRecord
import com.example.assignment.waterscreen.WaterTrackerScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.assignment.auth.ForgotPasswordScreen
import com.example.assignment.profilescreen.ChangePasswordScreen
@Composable
fun AppNavGraph(
    navController: NavHostController =
        rememberNavController()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTrackerDate by remember {
        mutableStateOf(getCurrentDate())
    }

    // One "added successfully" message shared by every tracker (shown as a
    // pill at the bottom, auto-hides after a couple of seconds).
    var successMessage by remember {
        mutableStateOf<String?>(null)
    }

    // Remembers which tracker screen the user was last on, so returning from
    // Insight goes back to that tracker instead of always the exercise one.
    var lastTracker by remember {
        mutableStateOf("water_tracker")
    }

    // Whenever a user becomes logged in, load their tracker data
    // (Supabase -> Room -> memory). Runs after login and after re-login.
    val loggedInUserId = UserData.currentUser?.id

    LaunchedEffect(loggedInUserId) {

        loggedInUserId?.let { userId ->

            // Load existing records first
            UserHealthData.loadForUser(userId)

            val currentEmail =
                UserData.currentUser
                    ?.email
                    ?.trim()
                    ?.lowercase()

            if (currentEmail == "demo@gmail.com") {

                UserHealthData.seedDemoDataIfNeeded(
                    userId
                )
            }
        }
    }

    val startDest = when {
        UserData.currentUser == null -> "login"
        UserData.needsProfileSetup() -> "profile_setup"
        else -> "home"
    }

    Box(modifier = Modifier.fillMaxSize()) {

    NavHost(
        navController = navController,
        startDestination = startDest,
        modifier = Modifier.fillMaxSize()
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    selectedTrackerDate =
                        getCurrentDate()

                    val destination =
                        if (
                            UserData.needsProfileSetup()
                        ) {
                            "profile_setup"
                        } else {
                            "home"
                        }

                    navController.navigate(
                        destination
                    ) {
                        popUpTo("login") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                },
                onForgotPasswordClick = {
                    navController.navigate("forgot_password")
                }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onBack = {
                    navController.popBackStack()
                },
                onResetSuccess = {
                    // back to login after reset
                    navController.navigate("login") {
                        popUpTo("forgot_password") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }


        composable("profile_setup") {
            val user =
                UserData.currentUser

            if (user != null) {
                ProfileSetupScreen(
                    fullName =
                        user.fullName,
                    email =
                        user.email,
                    currentProfileImageUri =
                        user.profileImageUri,
                    onSave = {
                            name,
                            email,
                            age,
                            gender,
                            height,
                            weight,
                            imageUri ->

                        scope.launch {
                            UserData.updateProfile(
                                context = context,
                                fullName = name,
                                email = email,
                                age = age,
                                gender = gender,
                                height = height,
                                weight = weight,
                                profileImageUri =
                                    imageUri,
                                completeSetup =
                                    true
                            )

                            navController.navigate(
                                "home"
                            ) {
                                popUpTo(
                                    "profile_setup"
                                ) {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }
                        }
                    },
                    onSkip = {
                        scope.launch {
                            UserData
                                .completeProfileSetup(context)

                            navController.navigate(
                                "home"
                            ) {
                                popUpTo(
                                    "profile_setup"
                                ) {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        composable("home") {
            val user =
                UserData.currentUser

            val userId =
                user?.id ?: 0

            val today =
                getCurrentDate()

            val goals =
                UserHealthData.goals(userId)

            HomeScreen(
                userName =
                    user?.fullName ?: "User",
                profileImageUri =
                    user?.profileImageUri ?: "",
                waterIntake =
                    UserHealthData.waterTotal(
                        userId,
                        today
                    ),
                waterGoal =
                    goals.waterGoal,
                mealCalories =
                    UserHealthData.mealTotal(
                        userId,
                        today
                    ),
                mealGoal =
                    goals.mealGoal,
                exerciseMinutes =
                    UserHealthData.exerciseMinutes(
                        userId,
                        today
                    ),
                exerciseGoal =
                    goals.exerciseGoal,
                sleepMinutes =
                    UserHealthData.sleepMinutes(
                        userId,
                        today
                    ),
                sleepGoal =
                    goals.sleepGoal,
                onNavigateToEditGoals = {
                    navController.navigate(
                        "health_goals"
                    )
                },
                onNavigateToExerciseTracker = {
                    selectedTrackerDate =
                        getCurrentDate()

                    navController.navigate(
                        "select_activity"
                    )
                },
                onNavigateToMealTracker = {
                    selectedTrackerDate =
                        getCurrentDate()

                    navController.navigate(
                        "logMeal"
                    )
                },
                onNavigateToWaterTracker = {
                    selectedTrackerDate =
                        getCurrentDate()

                    navController.navigate(
                        "add_water"
                    )
                },
                onNavigateToSleepTracker = {
                    selectedTrackerDate =
                        getCurrentDate()

                    navController.navigate(
                        "add_sleep"
                    )
                },
                onProfileClick = {
                    navController.navigate(
                        "profile"
                    )
                },
                onAchievementClick = {
                    navController.navigate(
                        "achievements"
                    )
                },
                onHealthGoalsClick = {
                    navController.navigate(
                        "health_goals"
                    )
                },
                onNotificationClick = {
                    navController.navigate(
                        "notifications"
                    )
                },
                onBmiClick = {
                    navController.navigate(
                        "bmi_calculator"
                    )
                },
                onHelpClick = {
                    navController.navigate(
                        "help_support"
                    )
                },
                onAboutClick = {
                    navController.navigate(
                        "about"
                    )
                },
                onSignOutClick = {
                    UserData.logout()

                    navController.navigate("login") {
                        popUpTo("home") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
                onInsightClick = {
                    navController.navigate(
                        "insight"
                    )
                },
                onTrackerClick = {
                    selectedTrackerDate = getCurrentDate()
                    navController.navigate(lastTracker)
                }
            )
        }

        composable("profile") {
            val user =
                UserData.currentUser

            if (user != null) {
                ProfileScreen(
                    fullName =
                        user.fullName,
                    email =
                        user.email,
                    age =
                        user.age,
                    gender =
                        user.gender,
                    height =
                        user.height,
                    weight =
                        user.weight,
                    profileImageUri =
                        user.profileImageUri,
                    onBack = {
                        navController.popBackStack()
                    },
                    onEditProfile = {
                        navController.navigate(
                            "edit_profile"
                        )
                    },
                    onChangePassword = {
                        navController.navigate("change_password")
                    }
                )
            }
        }
        composable("change_password") {
            ChangePasswordScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPasswordChanged = {
                    // done then return profile
                    navController.popBackStack()
                }
            )
        }

        composable("edit_profile") {
            val user =
                UserData.currentUser

            if (user != null) {
                EditProfileScreen(
                    currentName =
                        user.fullName,
                    currentEmail =
                        user.email,
                    currentAge =
                        user.age,
                    currentGender =
                        user.gender,
                    currentHeight =
                        user.height,
                    currentWeight =
                        user.weight,
                    currentProfileImageUri =
                        user.profileImageUri,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                            name,
                            email,
                            age,
                            gender,
                            height,
                            weight,
                            imageUri ->

                        scope.launch {
                            UserData.updateProfile(
                                context = context,
                                fullName = name,
                                email = email,
                                age = age,
                                gender = gender,
                                height = height,
                                weight = weight,
                                profileImageUri =
                                    imageUri
                            )

                            navController.popBackStack()
                        }
                    }
                )
            }
        }

        composable("achievements") {
            val userId =
                UserData.currentUser?.id ?: 0

            AchievementsScreen(
                achievements =
                    UserHealthData.achievements(
                        userId
                    ),
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("health_goals") {
            val userId =
                UserData.currentUser?.id ?: 0

            val goals =
                UserHealthData.goals(userId)

            HealthGoalsScreen(
                currentWaterGoal =
                    goals.waterGoal,
                currentMealGoal =
                    goals.mealGoal,
                currentExerciseGoal =
                    goals.exerciseGoal,
                currentSleepGoal =
                    goals.sleepGoal,
                onBack = {
                    navController.popBackStack()
                },
                onSave = {
                        waterGoal,
                        mealGoal,
                        exerciseGoal,
                        sleepGoal ->

                    UserHealthData.updateGoals(
                        userId = userId,
                        water = waterGoal,
                        meal = mealGoal,
                        exercise = exerciseGoal,
                        sleep = sleepGoal
                    )

                    navController.popBackStack()
                }
            )
        }

        composable("notifications") {
            val userId =
                UserData.currentUser?.id ?: 0

            val settings =
                UserHealthData.notificationSettings(
                    userId
                )

            val permissionLauncher =
                rememberLauncherForActivityResult(
                    contract =
                        ActivityResultContracts.RequestPermission()
                ) {}

            LaunchedEffect(Unit) {
                if (
                    Build.VERSION.SDK_INT >= 33 &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }

            NotificationsScreen(
                currentWaterReminder =
                    settings.waterReminder,
                currentWaterTime =
                    settings.waterTime,
                currentMealReminder =
                    settings.mealReminder,
                currentMealTime =
                    settings.mealTime,
                currentExerciseReminder =
                    settings.exerciseReminder,
                currentExerciseTime =
                    settings.exerciseTime,
                currentSleepReminder =
                    settings.sleepReminder,
                currentSleepTime =
                    settings.sleepTime,
                currentAchievementNotification =
                    settings.achievementNotification,
                onBack = {
                    navController.popBackStack()
                },
                onSave = {
                        waterReminder,
                        waterTime,
                        mealReminder,
                        mealTime,
                        exerciseReminder,
                        exerciseTime,
                        sleepReminder,
                        sleepTime,
                        achievementNotification ->

                    UserHealthData.updateNotifications(
                        userId = userId,
                        waterReminder = waterReminder,
                        waterTime = waterTime,
                        mealReminder = mealReminder,
                        mealTime = mealTime,
                        exerciseReminder = exerciseReminder,
                        exerciseTime = exerciseTime,
                        sleepReminder = sleepReminder,
                        sleepTime = sleepTime,
                        achievementNotification =
                            achievementNotification
                    )

                    ReminderScheduler.applySettings(
                        context = context,
                        userId = userId,
                        settings =
                            UserHealthData
                                .notificationSettings(
                                    userId
                                )
                    )

                    navController.popBackStack()
                },
                onTest = {
                    NotificationTester.sendTest(context)
                }
            )
        }

        composable("bmi_calculator") {
            val user =
                UserData.currentUser

            BmiCalculatorScreen(
                profileHeight =
                    user?.height ?: 0.0,
                profileWeight =
                    user?.weight ?: 0.0,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("help_support") {
            HelpSupportScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("about") {
            AboutScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("water_tracker") {
            val userId =
                UserData.currentUser?.id ?: 0

            val goals =
                UserHealthData.goals(userId)

            WaterTrackerScreen(
                selectedDate =
                    selectedTrackerDate,
                records =
                    UserHealthData.waterForDate(
                        userId,
                        selectedTrackerDate
                    ),
                goalAmount =
                    goals.waterGoal,
                onPreviousDate = {
                    selectedTrackerDate =
                        changeDate(
                            selectedTrackerDate,
                            -1
                        )
                },
                onNextDate = {
                    selectedTrackerDate =
                        nextDateNotFuture(
                            selectedTrackerDate
                        )
                },
                onAddClick = {
                    selectedTrackerDate =
                        getCurrentDate()

                    navController.navigate(
                        "add_water"
                    )
                },
                onRecordClick = {
                    navController.navigate(
                        "edit_water/${it.waterId}"
                    )
                },
                onMealClick = {
                    navController.navigate(
                        "tracker"
                    )
                },
                onExerciseClick = {
                    navController.navigate(
                        "main_tracker"
                    )
                },
                onSleepClick = {
                    navController.navigate(
                        "sleep_tracker"
                    )
                },
                onHomeClick = {
                    navController.navigate(
                        "home"
                    ) {
                        launchSingleTop = true
                    }
                },
                onInsightClick = {
                    lastTracker = "water_tracker"
                    navController.navigate(
                        "insight"
                    )
                }
            )
        }

        composable("add_water") {
            val userId =
                UserData.currentUser?.id ?: 0

            val today =
                getCurrentDate()

            val goals =
                UserHealthData.goals(userId)

            AddWaterRecord(
                currentIntake =
                    UserHealthData.waterTotal(
                        userId,
                        today
                    ),
                goalAmount =
                    goals.waterGoal,
                userId = userId,
                onBack = {
                    navController.popBackStack()
                },
                onSave = {
                    UserHealthData.addWater(
                        userId,
                        it
                    )

                    AchievementNotifier.checkAndNotify(
                        context,
                        userId
                    )

                    selectedTrackerDate =
                        today

                    successMessage = "Water logged successfully"

                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "edit_water/{waterId}",
            arguments = listOf(
                navArgument("waterId") {
                    type = NavType.IntType
                }
            )
        ) { entry ->

            val userId =
                UserData.currentUser?.id ?: 0

            val waterId =
                entry.arguments
                    ?.getInt("waterId")
                    ?: -1

            val record =
                UserHealthData.waterRecords(
                    userId
                ).find {
                    it.waterId == waterId
                }

            if (record != null) {

                val date =
                    record.loggedAt.take(10)

                AddWaterRecord(
                    currentIntake =
                        UserHealthData.waterTotal(
                            userId,
                            date
                        ),

                    goalAmount =
                        UserHealthData.goals(
                            userId
                        ).waterGoal,

                    userId = userId,
                    existingRecord = record,

                    onBack = {
                        navController.popBackStack()
                    },

                    onSave = {

                        UserHealthData.updateWater(
                            userId,
                            it
                        )

                        AchievementNotifier.checkAndNotify(
                            context,
                            userId
                        )

                        selectedTrackerDate =
                            date

                        navController.popBackStack()
                    },

                    onDelete = {

                        UserHealthData.deleteWater(
                            userId,
                            waterId
                        )

                        selectedTrackerDate =
                            date

                        navController.popBackStack()
                    }
                )
            }
        }

        composable("tracker") {
            val userId =
                UserData.currentUser?.id ?: 0

            val goals =
                UserHealthData.goals(userId)

            TrackerScreen(
                selectedDate =
                    selectedTrackerDate,
                meals =
                    UserHealthData.mealsForDate(
                        userId,
                        selectedTrackerDate
                    ),
                goalCalories =
                    goals.mealGoal,
                onPreviousDate = {
                    selectedTrackerDate =
                        changeDate(
                            selectedTrackerDate,
                            -1
                        )
                },
                onNextDate = {
                    selectedTrackerDate =
                        nextDateNotFuture(
                            selectedTrackerDate
                        )
                },
                onAddClick = {
                    selectedTrackerDate =
                        getCurrentDate()

                    navController.navigate(
                        "logMeal"
                    )
                },
                onMealClick = {
                    navController.navigate(
                        "mealDetail/${it.id}"
                    )
                },
                onWaterClick = {
                    navController.navigate(
                        "water_tracker"
                    )
                },
                onExerciseClick = {
                    navController.navigate(
                        "main_tracker"
                    )
                },
                onSleepClick = {
                    navController.navigate(
                        "sleep_tracker"
                    )
                },
                onHomeClick = {
                    navController.navigate(
                        "home"
                    ) {
                        launchSingleTop = true
                    }
                },
                onInsightClick = {
                    lastTracker = "tracker"
                    navController.navigate(
                        "insight"
                    )
                }
            )
        }

        composable("logMeal") {
            LogMealScreen(
                onBack = {
                    navController.popBackStack()
                },
                onImageSelected = {
                        mealType,
                        uri ->

                    navController
                        .currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            "selectedImageUri",
                            uri.toString()
                        )

                    navController.navigate(
                        "detectResult/$mealType"
                    )
                }
            )
        }

        composable(
            route = "detectResult/{mealType}",
            arguments = listOf(
                navArgument("mealType") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val userId =
                UserData.currentUser?.id ?: 0

            val mealType =
                entry.arguments
                    ?.getString("mealType")
                    ?: "Lunch"

            val imageUri =
                navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>(
                        "selectedImageUri"
                    )

            DetectResultScreen(
                mealType = mealType,
                imageUri = imageUri,
                onSave = { meal ->
                    // Time-based id: unique across devices, so cross-device
                    // Supabase sync doesn't overwrite records with the same id.
                    val nextId = System.currentTimeMillis().toInt()

                    UserHealthData.addMeal(
                        userId,
                        meal.copy(
                            id = nextId
                        )
                    )

                    AchievementNotifier.checkAndNotify(
                        context,
                        userId
                    )

                    selectedTrackerDate =
                        getCurrentDate()

                    successMessage = "Meal logged successfully"

                    // Land on the meal list whether the user started from Home
                    // or from the meal tracker, so "tracker" always exists here.
                    navController.navigate("tracker") {
                        popUpTo("logMeal") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "mealDetail/{mealId}",
            arguments = listOf(
                navArgument("mealId") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val userId =
                UserData.currentUser?.id ?: 0

            val mealId =
                entry.arguments
                    ?.getInt("mealId")
                    ?: -1

            val meal =
                UserHealthData.meals(
                    userId
                ).find {
                    it.id == mealId
                }

            if (meal != null) {
                MealDetailScreen(
                    meal = meal,
                    dailyTarget =
                        UserHealthData
                            .goals(userId)
                            .mealGoal,
                    onBack = {
                        navController.popBackStack()
                    },
                    onDelete = {
                        UserHealthData.deleteMeal(
                            userId,
                            mealId
                        )

                        navController.popBackStack()
                    },
                    onEdit = {
                        navController.navigate(
                            "editMeal/$mealId"
                        )
                    }
                )
            }
        }

        composable(
            route = "editMeal/{mealId}",
            arguments = listOf(
                navArgument("mealId") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val userId =
                UserData.currentUser?.id ?: 0

            val mealId =
                entry.arguments
                    ?.getInt("mealId")
                    ?: -1

            val meal =
                UserHealthData.meals(
                    userId
                ).find {
                    it.id == mealId
                }

            if (meal != null) {
                EditMealScreen(
                    meal = meal,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                        UserHealthData.updateMeal(
                            userId,
                            it
                        )

                        AchievementNotifier.checkAndNotify(
                            context,
                            userId
                        )

                        navController.popBackStack()
                    }
                )
            }
        }

        // --- EXERCISE TRACKER NAVIGATION ---
        composable("main_tracker") {
            val userId = UserData.currentUser?.id ?: 0
            val goals = UserHealthData.goals(userId)

            ExerciseTrackerMainScreen(
                selectedDate = selectedTrackerDate,
                workouts = UserHealthData.workoutsForDate(userId, selectedTrackerDate),
                exerciseGoal = goals.exerciseGoal,
                onPreviousDate = { selectedTrackerDate = changeDate(selectedTrackerDate, -1) },
                onNextDate = { selectedTrackerDate = nextDateNotFuture(selectedTrackerDate) },
                onAddClick = { navController.navigate("select_activity") },
                onWorkoutClick = { workout -> navController.navigate("workout_detail/${workout.id}") },
                onViewAllClick = { navController.navigate("workout_history") },
                onWaterClick = { navController.navigate("water_tracker") },
                onMealClick = { navController.navigate("tracker") },
                onSleepClick = { navController.navigate("sleep_tracker") },
                onHomeClick = {
                    navController.navigate("home") { launchSingleTop = true }
                },
                onInsightClick = {
                    lastTracker = "main_tracker"
                    navController.navigate("insight")
                }
            )
        }

        composable("select_activity") {
            SelectActivityScreen(
                onBack = { navController.popBackStack() },
                onActivitySelected = { activityName: String, distance: Double? ->
                    val distArg = (distance ?: -1.0).toFloat()
                    navController.navigate("calculating_session/$activityName/$distArg")
                },
                onAddCustomClick = { navController.navigate("custom_activity") }
            )
        }

        composable("custom_activity") {
            CustomActivityScreen(
                onBack = { navController.popBackStack() },
                onStartWorkout = { activityName: String ->
                    val distArg = -1.0f
                    navController.navigate("calculating_session/$activityName/$distArg")
                }
            )
        }

        composable(
            route = "calculating_session/{activityName}/{distance}",
            arguments = listOf(
                navArgument("activityName") { type = NavType.StringType },
                navArgument("distance") { type = NavType.FloatType }
            )
        ) { entry ->
            val userId = UserData.currentUser?.id ?: 0
            val activityName = entry.arguments?.getString("activityName") ?: "Exercise"
            val rawDist = entry.arguments?.getFloat("distance")?.toDouble() ?: -1.0
            val distanceKm = if (rawDist < 0) null else rawDist

            CalculatingSessionScreen(
                activityName = activityName,
                userEnteredDistanceKm = distanceKm,
                onBack = { navController.popBackStack() },
                onClick = { durationSec, calories, dist ->
                    // Time-based id: unique across devices (see meal note above).
                    val nextId = System.currentTimeMillis().toString()
                    val today = getCurrentDate()

                    val newWorkout = WorkoutItem(
                        id = nextId,
                        title = activityName,
                        duration = durationSec.toString(),
                        calories = calories,
                        time = getCurrentTime(),
                        distanceKm = dist,
                        date = today
                    )

                    UserHealthData.addWorkout(userId, newWorkout)
                    AchievementNotifier.checkAndNotify(context, userId)

                    selectedTrackerDate = today
                    successMessage = "Workout added successfully"

                    navController.navigate("main_tracker") {
                        popUpTo("select_activity") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("workout_history") {
            val userId = UserData.currentUser?.id ?: 0
            WorkoutHistoryScreen(
                workouts = UserHealthData.workouts(userId),
                onBack = { navController.popBackStack() },
                onWorkoutClick = { workout -> navController.navigate("workout_detail/${workout.id}") },
                onHomeClick = {
                    navController.navigate("home") { launchSingleTop = true }
                },
                onTrackersClick = {
                    navController.navigate("main_tracker") { launchSingleTop = true }
                },
                onInsightClick = {
                    navController.navigate("insight")
                }
            )
        }

        composable(
            route = "workout_detail/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { entry ->
            val userId = UserData.currentUser?.id ?: 0
            val workoutId = entry.arguments?.getString("workoutId") ?: ""
            val workout = UserHealthData.workouts(userId).find { it.id == workoutId }

            if (workout != null) {
                WorkoutDetailScreen(
                    workout = workout,
                    onBack = { updatedWorkout ->
                        // Persist the edit to memory + Room + Supabase.
                        UserHealthData.updateWorkout(userId, updatedWorkout)
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("sleep_tracker") {
            val userId =
                UserData.currentUser?.id ?: 0

            val goals =
                UserHealthData.goals(userId)

            SleepTrackerScreen(
                selectedDate =
                    selectedTrackerDate,
                sleepRecords =
                    UserHealthData.sleepForDate(
                        userId,
                        selectedTrackerDate
                    ),
                goalMinutes =
                    goals.sleepGoal,
                onPreviousDate = {
                    selectedTrackerDate =
                        changeDate(
                            selectedTrackerDate,
                            -1
                        )
                },
                onNextDate = {
                    selectedTrackerDate =
                        nextDateNotFuture(
                            selectedTrackerDate
                        )
                },
                onAddClick = {
                    selectedTrackerDate =
                        getCurrentDate()

                    navController.navigate(
                        "add_sleep"
                    )
                },
                onSleepClick = {
                    navController.navigate(
                        "sleep_detail/${it.id}"
                    )
                },
                onWaterClick = {
                    navController.navigate(
                        "water_tracker"
                    )
                },
                onMealClick = {
                    navController.navigate(
                        "tracker"
                    )
                },
                onExerciseClick = {
                    navController.navigate(
                        "main_tracker"
                    )
                },
                onHomeClick = {
                    navController.navigate(
                        "home"
                    ) {
                        launchSingleTop = true
                    }
                },
                onInsightClick = {
                    lastTracker = "sleep_tracker"
                    navController.navigate(
                        "insight"
                    )
                }
            )
        }

        composable("add_sleep") {
            val userId =
                UserData.currentUser?.id ?: 0

            AddSleepRecordScreen(
                selectedDate = selectedTrackerDate,
                existingRecords = UserHealthData.sleepRecords(userId),
                onBack = {
                    navController.popBackStack()
                },

                onSave = {
                    UserHealthData.addSleep(
                        userId,
                        it
                    )

                    AchievementNotifier.checkAndNotify(
                        context,
                        userId
                    )

                    selectedTrackerDate =
                        it.date

                    successMessage = "Sleep record added successfully"

                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "sleep_detail/{sleepId}",
            arguments = listOf(
                navArgument("sleepId") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val userId =
                UserData.currentUser?.id ?: 0

            val sleepId =
                entry.arguments
                    ?.getInt("sleepId")
                    ?: -1

            val record =
                UserHealthData.sleepRecords(
                    userId
                ).find {
                    it.id == sleepId
                }

            if (record != null) {
                SleepDetail(
                    sleepRecord = record,
                    goalMinutes =
                        UserHealthData
                            .goals(userId)
                            .sleepGoal,
                    onBack = {
                        navController.popBackStack()
                    },
                    onEdit = {
                        navController.navigate(
                            "edit_sleep/$sleepId"
                        )
                    },
                    onDelete = {
                        UserHealthData.deleteSleep(
                            userId,
                            sleepId
                        )

                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = "edit_sleep/{sleepId}",
            arguments = listOf(
                navArgument("sleepId") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val userId =
                UserData.currentUser?.id ?: 0

            val sleepId =
                entry.arguments
                    ?.getInt("sleepId")
                    ?: -1

            val record =
                UserHealthData.sleepRecords(
                    userId
                ).find {
                    it.id == sleepId
                }

            if (record != null) {
                AddSleepRecordScreen(
                    selectedDate = record.date,
                    existingRecord = record,
                    existingRecords = UserHealthData.sleepRecords(userId),
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                        UserHealthData.updateSleep(
                            userId,
                            it
                        )

                        AchievementNotifier.checkAndNotify(
                            context,
                            userId
                        )

                        selectedTrackerDate =
                            it.date

                        navController.popBackStack()
                    }
                )
            }
        }

        composable("insight") {
            val userId =
                UserData.currentUser?.id ?: 0

            InsightScreen(
                userId = userId,
                onHomeClick = {
                    navController.navigate(
                        "home"
                    ) {
                        launchSingleTop = true
                    }
                },
                onTrackerClick = {
                    selectedTrackerDate =
                        getCurrentDate()

                    navController.navigate(
                        lastTracker
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            SuccessPillSnackbar(
                visible = successMessage != null,
                message = successMessage ?: "",
                onDismiss = { successMessage = null }
            )
        }
    }
}


private fun changeDate(
    date: String,
    amount: Int
): String {
    return try {
        val format =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

        val calendar =
            Calendar.getInstance()

        calendar.time =
            format.parse(date)
                ?: return date

        calendar.add(
            Calendar.DAY_OF_MONTH,
            amount
        )

        format.format(
            calendar.time
        )
    } catch (_: Exception) {
        date
    }
}

private fun nextDateNotFuture(
    currentDate: String
): String {
    val candidate =
        changeDate(
            currentDate,
            1
        )

    return if (
        candidate <=
        getCurrentDate()
    ) {
        candidate
    } else {
        currentDate
    }
}
