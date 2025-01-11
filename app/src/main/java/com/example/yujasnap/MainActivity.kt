package com.example.yujasnap

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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

    // Register permission request for camera, location, and notification
    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions["android.permission.ACCESS_FINE_LOCATION"] == true) {
            // Location permission granted, proceed with location-related actions
            checkAndEnableGPS(this) {}
        }
    }

    /**
     * Called when the activity is created. It sets the content view and initializes the theme
     * for the UI. It also calls the `screenRoots` composable to set up the navigation structure.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if required permissions are granted or not
        val permissionsToRequest = permissionList(this)

        // If permissions are needed, request them
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions.launch(permissionsToRequest.toTypedArray())
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

private fun permissionList(context: Context): MutableList<String> {
    // Check if required permissions are granted or not
    val permissionsToRequest = mutableListOf<String>()

    if (ContextCompat.checkSelfPermission(context, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add("android.permission.CAMERA")
    }

    if (ContextCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add("android.permission.ACCESS_FINE_LOCATION")
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add("android.permission.POST_NOTIFICATIONS")
    }

    return permissionsToRequest
}

private fun checkAndEnableGPS(context: Context, onGPSRequired: () -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        // Prompt user to enable GPS (open GPS settings)
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
        onGPSRequired()
    }
}

