package com.example.assignment.exercisescreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat
import com.example.assignment.model.WorkoutItem
import com.example.assignment.ui.theme.*
import java.util.Locale

fun formatWorkoutDuration(rawDuration: String): String {
    val trimmed = rawDuration.trim()

    // 1. Handle mm:ss or hh:mm:ss format (e.g. "01:10")
    if (trimmed.contains(":")) {
        val parts = trimmed.split(":").mapNotNull { it.toLongOrNull() }
        val totalSecs = when (parts.size) {
            2 -> (parts[0] * 60) + parts[1]
            3 -> (parts[0] * 3600) + (parts[1] * 60) + parts[2]
            else -> 0L
        }
        return formatFromTotalSeconds(totalSecs)
    }

    // 2. Extract minutes and seconds using Regex patterns
    val minMatch = Regex("""(\d+)\s*(?:min|mins|m)""", RegexOption.IGNORE_CASE).find(trimmed)
    val secMatch = Regex("""(\d+)\s*(?:sec|secs|s)""", RegexOption.IGNORE_CASE).find(trimmed)

    if (minMatch != null || secMatch != null) {
        val mins = minMatch?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L
        val secs = secMatch?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L
        val totalSecs = (mins * 60) + secs
        return formatFromTotalSeconds(totalSecs)
    }

    // 3. If raw input is strictly numeric (e.g., "10" or "70" passed directly as seconds)
    val totalSeconds = trimmed.filter { it.isDigit() }.toLongOrNull() ?: return rawDuration
    return formatFromTotalSeconds(totalSeconds)
}

private fun formatFromTotalSeconds(totalSeconds: Long): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60

    return when {
        mins == 0L -> "$secs sec"
        secs == 0L -> "$mins min"
        else -> "$mins min $secs sec"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workout: WorkoutItem,
    onBack: (WorkoutItem) -> Unit,
) {
    val context = LocalContext.current

    val isDistanceBased = workout.title.contains("Run", ignoreCase = true) ||
            workout.title.contains("Cycl", ignoreCase = true) ||
            workout.title.contains("Bike", ignoreCase = true) ||
            workout.title.contains("Walk", ignoreCase = true) ||
            workout.title.contains("Hike", ignoreCase = true)

    val isNewWorkout = workout.time.contains("Just now", ignoreCase = true) ||
            workout.time.contains("sec", ignoreCase = true)

    val initialKm = remember(workout) {
        when {
            workout.distanceKm != null -> workout.distanceKm
            isNewWorkout -> 0.0
            workout.title.contains("Run", ignoreCase = true) -> 5.20
            workout.title.contains("Cycl", ignoreCase = true) || workout.title.contains("Bike", ignoreCase = true) -> 12.80
            workout.title.contains("Walk", ignoreCase = true) || workout.title.contains("Hike", ignoreCase = true) -> 3.10
            else -> 0.0
        }
    }

    var totalDistanceMeters by remember(workout) { mutableDoubleStateOf(initialKm * 1000.0) }
    var previousLocation by remember { mutableStateOf<Location?>(null) }
    
    // Editable states
    var editableTitle by remember(workout) { mutableStateOf(workout.title) }
    var editableNotes by remember(workout) { mutableStateOf(workout.notes) }
    
    // Dialog states
    var showTitleDialog by remember { mutableStateOf(false) }
    var showDistanceDialog by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(isDistanceBased) {
        if (isDistanceBased && !hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    DisposableEffect(isDistanceBased, hasPermission) {
        if (isDistanceBased && hasPermission) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    previousLocation?.let { prev ->
                        val delta = prev.distanceTo(location).toDouble()
                        if (delta > 1.5) {
                            totalDistanceMeters += delta
                        }
                    }
                    previousLocation = location
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
            }

            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000L,
                    2f,
                    locationListener
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

            onDispose {
                locationManager.removeUpdates(locationListener)
            }
        } else {
            onDispose { }
        }
    }

    val currentDistanceKm = totalDistanceMeters / 1000.0
    val formattedDistance = String.format(Locale.US, "%.2f", currentDistanceKm)

    val formattedDurationText = remember(workout.duration) {
        formatWorkoutDuration(workout.duration)
    }

    val activityIcon = when {
        editableTitle.contains("Run", true) || editableTitle.contains("Jog", true) -> Icons.AutoMirrored.Filled.DirectionsRun
        editableTitle.contains("Cycl", true) || editableTitle.contains("Bike", true) -> Icons.AutoMirrored.Filled.DirectionsBike
        editableTitle.contains("Walk", true) -> Icons.AutoMirrored.Filled.DirectionsWalk
        editableTitle.contains("Swim", true) -> Icons.Default.Pool
        editableTitle.contains("Squash", true) || editableTitle.contains("Badminton", true) || editableTitle.contains("Tennis", true) || editableTitle.contains("Pickle", true) -> Icons.Default.SportsTennis
        editableTitle.contains("Ping", true) || editableTitle.contains("Table Tennis", true) -> Icons.Default.SportsBaseball
        editableTitle.contains("Yoga", true) || editableTitle.contains("Pilates", true) || editableTitle.contains("Stretch", true) -> Icons.Default.SelfImprovement
        editableTitle.contains("Hike", true) -> Icons.Default.Hiking
        editableTitle.contains("Basket", true) -> Icons.Default.SportsBasketball
        editableTitle.contains("Box", true) || editableTitle.contains("Martial", true) -> Icons.Default.SportsMartialArts
        editableTitle.contains("Foot", true) || editableTitle.contains("Soc", true) -> Icons.Default.SportsSoccer
        editableTitle.contains("Volley", true) -> Icons.Default.SportsVolleyball
        editableTitle.contains("Dance", true) || editableTitle.contains("Zumba", true) -> Icons.Default.SportsKabaddi
        editableTitle.contains("Gym", true) || editableTitle.contains("Weight", true) || editableTitle.contains("Lift", true) -> Icons.Default.FitnessCenter
        else -> Icons.Default.FitnessCenter
    }

    // --- Dialogs ---
    if (showTitleDialog) {
        var tempTitle by remember { mutableStateOf(editableTitle) }
        AlertDialog(
            onDismissRequest = { showTitleDialog = false },
            title = { Text("Edit Workout Name") },
            text = {
                OutlinedTextField(
                    value = tempTitle,
                    onValueChange = { tempTitle = it },
                    label = { Text("Workout Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    editableTitle = tempTitle
                    showTitleDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showTitleDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDistanceDialog) {
        var tempDist by remember { mutableStateOf(formattedDistance) }
        AlertDialog(
            onDismissRequest = { showDistanceDialog = false },
            title = { Text("Edit Distance") },
            text = {
                OutlinedTextField(
                    value = tempDist,
                    onValueChange = { tempDist = it },
                    label = { Text("Distance (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val d = tempDist.toDoubleOrNull()
                    if (d != null) {
                        totalDistanceMeters = d * 1000.0
                    }
                    showDistanceDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDistanceDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showNotesDialog) {
        var tempNotes by remember { mutableStateOf(editableNotes) }
        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text("Edit Notes") },
            text = {
                OutlinedTextField(
                    value = tempNotes,
                    onValueChange = { tempNotes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    editableNotes = tempNotes
                    showNotesDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Detail", color = PrimaryGreen, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        val updatedWorkout = workout.copy(
                            title = editableTitle,
                            notes = editableNotes,
                            distanceKm = if (isDistanceBased) currentDistanceKm else null
                        )
                        onBack(updatedWorkout)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
                    }
                },
                actions = {
                    Surface(
                        color = LightGreenBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BrightGreen))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("LIVE SENSOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth().clickable { showTitleDialog = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(editableTitle, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Text("Today • ${workout.time}", fontSize = 14.sp, color = GrayText)
                }
                Icon(Icons.Default.Edit, contentDescription = "Edit title", tint = GrayText, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreenBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = activityIcon,
                                    contentDescription = editableTitle,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = editableTitle.uppercase(),
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DURATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GrayText)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("GPS SYNC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(formattedDurationText, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { 0.7f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = PrimaryGreen,
                        trackColor = BorderOutline
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isDistanceBased) {
                    Card(
                        modifier = Modifier.weight(1f).clickable { showDistanceDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("DISTANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayText)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Edit, contentDescription = null, tint = GrayText, modifier = Modifier.size(10.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(formattedDistance, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                Text(" km", fontSize = 14.sp, color = GrayText, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BURNED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayText)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(workout.calories.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text(" kcal", fontSize = 14.sp, color = GrayText, modifier = Modifier.padding(bottom = 4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Workout Notes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                modifier = Modifier.fillMaxWidth().clickable { showNotesDialog = true }
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (editableNotes.isBlank()) "Add notes about your workout..." else editableNotes,
                        fontSize = 14.sp,
                        color = if (editableNotes.isBlank()) GrayText else DarkText,
                        lineHeight = 20.sp
                    )
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Notes",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp).align(Alignment.TopEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WorkoutDetailScreenPreview() {
    WorkoutDetailScreen(
        workout = WorkoutItem("1", "Running", "07:15 AM", "70", 2, distanceKm = 0.05),
        onBack = {}
    )
}