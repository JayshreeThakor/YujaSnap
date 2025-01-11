package com.example.yujasnap.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import com.example.yujasnap.MainActivity
import com.example.yujasnap.actions.ToggleLocationServiceAction

/**
 * Glance widget for managing location services and providing UI actions.
 *
 * This widget includes two buttons: one to open an image and another to toggle the location service.
 * The `provideGlance` function defines the UI content and interaction logic for the widget.
 */
class LocationWidget : GlanceAppWidget() {

    /**
     * Provides the content and actions for the widget.
     *
     * This function defines the layout of the widget and the actions triggered by button clicks.
     * It includes a button for opening the main activity and another for starting the location service.
     *
     * @param context The context in which the widget is being displayed.
     * @param id The unique identifier for the widget.
     */
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier.padding(16.dp)
            ) {
                // Button for opening the main activity to view images
                Button(
                    text = "Click Image",
                    onClick = actionStartActivity<MainActivity>()
                )
                Spacer(modifier = GlanceModifier.height(8.dp))

                // Button for starting the location service

                Button(
                    text = "Start Location Service",
                    onClick = actionRunCallback<ToggleLocationServiceAction>()
                )
            }
        }
    }
}

