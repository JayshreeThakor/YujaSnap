package com.example.yujasnap.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * A composable function that displays the home screen UI. It contains a button that, when clicked,
 * navigates the user to the camera screen to capture an image.
 *
 * @param navController The NavController used to manage navigation between screens.
 */
@Composable
fun homeScreen(navController: NavController) {
    // Box layout to center the content in the screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Button that navigates to the camera screen when clicked
        Button(onClick = { navController.navigate("camera") }) {
            Text("Open Camera")
        }
    }
}
