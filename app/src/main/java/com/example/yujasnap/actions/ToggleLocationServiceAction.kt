package com.example.yujasnap.actions

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.yujasnap.services.LocationService
import com.example.yujasnap.services.LocationServiceUtil
import com.example.yujasnap.widget.LocationWidget

/**
 * Action callback to toggle the state of the `LocationService` in the app.
 * This action either starts or stops the `LocationService` based on its current running state.
 * It also updates the app widget state to reflect the change in the service's state.
 */
class ToggleLocationServiceAction : ActionCallback {

    /**
     * Called when the action is triggered. This method checks whether the `LocationService` is running
     * and either starts or stops it accordingly. It also updates the app widget state to reflect
     * the current state of the service.
     *
     * @param context The context in which the action is being performed.
     * @param glanceId The unique identifier for the app widget being interacted with.
     * @param parameters The parameters for the action. Not used in this case.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Create an intent to start or stop the LocationService
        val intent = Intent(context, LocationService::class.java)
        // Check if the LocationService is already running
        val isRunning = LocationServiceUtil.isServiceRunning(context, LocationService::class.java)

        // If the service is running, stop it; otherwise, start it
        if (isRunning) {
            context.stopService(intent)
        } else {
            context.startForegroundService(intent)
        }

        // Update the widget to reflect the new state
        LocationWidget().update(context, glanceId)
    }
}
