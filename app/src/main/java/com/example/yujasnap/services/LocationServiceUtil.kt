package com.example.yujasnap.services

import android.app.ActivityManager
import android.content.Context

/**
 * A utility object that provides functionality to check whether a service is running.
 * This can be used to check if the `LocationService` is running in the background.
 */
object LocationServiceUtil {

    /**
     * Checks if a specific service is running in the background.
     *
     * @param context The context used to access system services.
     * @param serviceClass The service class to check.
     * @return True if the service is running, false otherwise.
     */
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        // Get the activity manager system service to access the running services
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Iterate through the running services and check if the specified service is running
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}