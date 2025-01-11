package com.example.yujasnap

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yujasnap.screens.cameraScreen
import com.example.yujasnap.screens.homeScreen
import com.example.yujasnap.screens.previewScreen
import com.example.yujasnap.ui.theme.YujaSnapTheme
import com.example.yujasnap.viewmodel.ImageUriViewModel

/**
 * MainActivity is the entry point of the application. It sets up the UI and navigation structure
 * for the app. It uses Jetpack Compose to define the UI and hosts the navigation graph.
 */
class MainActivity : ComponentActivity() {

    // Register the permission request with a callback to handle permission result
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    /**
     * Called when the activity is created. It sets the content view and initializes the theme
     * for the UI. It also calls the `screenRoots` composable to set up the navigation structure.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Request camera permission before setting the content
        if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.CAMERA"
            ) != PackageManager.PERMISSION_GRANTED) {
            // Launch the permission request if not granted
            requestCameraPermission.launch("android.permission.CAMERA")
        }

        setContent {
            YujaSnapTheme {
                screenRoots()
            }
        }
    }
}

/**
 * A composable that sets up the navigation graph for the app.
 * This defines the different screens and their associated routes.
 * The `NavHost` is used to manage the navigation between different screens.
 */
@Composable
fun screenRoots() {
    // Initialize the NavController for managing navigation
    val navController = rememberNavController()
    // Initialize the ViewModel to manage image URI state
    val imageUriViewModel: ImageUriViewModel = viewModel()

    // Set up the navigation graph
    NavHost(navController = navController, startDestination = "home") {
        // Define the composable for each screen and their routes
        composable("home") { homeScreen(navController) }
        composable("camera") { cameraScreen(navController, imageUriViewModel) }
        composable("preview") { previewScreen(imageUriViewModel) }
    }
}


