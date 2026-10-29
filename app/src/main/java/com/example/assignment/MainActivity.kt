package com.example.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.assignment.data.UserData
import com.example.assignment.data.UserHealthData
import com.example.assignment.nav.AppNavGraph
import com.example.assignment.ui.theme.AssignmentTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            var isLoaded by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                UserHealthData.initialize(applicationContext)
                UserData.initialize(applicationContext)
                isLoaded = true
                // The actual tracker data is loaded (Supabase -> Room -> memory)
                // by a LaunchedEffect in AppNavGraph once a user is logged in,
                // so a slow network never blocks the first screen.
            }

            AssignmentTheme(
                dynamicColor = false
            ) {
                if (isLoaded) {
                    AppNavGraph()
                }
            }
        }
    }
}
