package com.example.yujasnap.widget

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.Switch
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import com.example.yujasnap.MainActivity
import com.example.yujasnap.services.LocationService
import com.example.yujasnap.services.LocationServiceUtil
import kotlinx.coroutines.runBlocking

/**
 * Glance widget for managing location services and providing UI actions.
 *
 * This widget includes two buttons: one to open an image and another to toggle the location service.
 * The `provideGlance` function defines the UI content and interaction logic for the widget.
 */
class LocationWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*>?
        get() = super.stateDefinition

    /**
     * Provides the content and actions for the widget.
     *
     * This function defines the layout of the widget and the actions triggered by button clicks.
     * It includes a button for opening the main activity and another for starting the location service.
     *
     * @param context The context in which the widget is being displayed.
     * @param id The unique identifier for the widget.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val enableKey = booleanPreferencesKey("location_logging_enabled")
            val enabled = currentState(enableKey) ?: false
            Column(
                modifier = GlanceModifier.padding(16.dp),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                // Button to open the main activity
                Button(
                    text = "Click Image",
                    onClick = actionStartActivity<MainActivity>(
                        parameters = actionParametersOf(
                            ActionParameters.Key<Boolean>("capture") to true
                        )
                    )
                )
                Spacer(modifier = GlanceModifier.height(16.dp))

                // Switch to toggle location tracking
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        runBlocking {
                            // Toggle state and save it
                            updateAppWidgetState(context, id) { prefs ->
                                prefs[enableKey] = !enabled
                            }

                            // Start or stop the LocationService based on the new state
                            val intent = Intent(context, LocationService::class.java)
                            val isRunning = LocationServiceUtil.isServiceRunning(context, LocationService::class.java)

                            if (!enabled) {
                                if (!isRunning) context.startForegroundService(intent)
                            } else {
                                if (isRunning) context.stopService(intent)
                            }

                            // Update the widget to reflect the new state
                            LocationWidget().update(context, id)
                        }
                    }
                )
            }
        }
    }
}

