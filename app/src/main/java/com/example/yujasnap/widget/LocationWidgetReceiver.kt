package com.example.yujasnap.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * A receiver class for the LocationWidget that extends [GlanceAppWidgetReceiver].
 *
 * This class is responsible for receiving events related to the `LocationWidget` and
 * passing them to the corresponding widget. It associates the widget with the receiver
 * and ensures that the correct widget is used when events are triggered.
 */
class LocationWidgetReceiver : GlanceAppWidgetReceiver() {
    /**
     * The [GlanceAppWidget] that defines the layout and behavior of the widget.
     *
     * This widget displays a user interface with buttons that interact with location services
     * and can be updated based on user actions.
     */
    override val glanceAppWidget: GlanceAppWidget = LocationWidget()
}