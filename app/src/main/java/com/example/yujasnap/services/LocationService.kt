package com.example.yujasnap.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A service to handle location updates in the background and save them in a CSV file.
 *
 * This service runs as a foreground service, periodically fetching location updates
 * using the [FusedLocationProviderClient]. The location data is saved to a CSV file.
 * Notifications are sent to inform the user about the current location.
 */
class LocationService : Service() {

    private val channelId = "LocationServiceChannel"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    private val interval = 2000L // 2 seconds
    private var isRunning = false
    private lateinit var csvFile: File


    /**
     * Called when the service is created.
     * Initializes the fused location client and starts the service as a foreground service.
     * Creates a notification channel and a CSV file for saving location data.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        startForeground(1, buildNotification("Waiting for location updates..."))
        createCsvFile()
    }

    /**
     * Called when the service is started.
     * Begins location updates and marks the service as running.
     *
     * @param intent The intent passed to start the service.
     * @param flags Additional flags about the start request.
     * @param startId A unique identifier for this start request.
     * @return The service's start mode (sticky).
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        startLocationUpdates()
        return START_STICKY
    }

    /**
     * Creates a directory and CSV file to store the live location data.
     */
    private fun createCsvFile() {
        // Create 'location' directory
        val locationDir = File(filesDir, "location")
        if (!locationDir.exists()) {
            locationDir.mkdirs()
        }

        // Create a CSV file inside the 'location' directory
        csvFile = File(locationDir, "location_${getCurrentTimestamp()}.csv")
        if (!csvFile.exists()) {
            csvFile.createNewFile()
        }
    }

    /**
     * Starts receiving location updates at the defined interval.
     * Updates the notification and stores the location in a CSV file.
     */
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, interval
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location: Location? = locationResult.lastLocation
                location?.let {
                    saveLocationToCSV(it)
                    updateNotification("Lat: ${it.latitude}, Lng: ${it.longitude}")
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    /**
     * Updates the foreground notification with the current location.
     *
     * @param content The content to display in the notification.
     */
    private fun updateNotification(content: String) {
        val notification = buildNotification(content)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notification)
    }

    /**
     * Builds the notification that will be displayed while the service is running.
     *
     * @param content The content to display in the notification.
     * @return The built notification.
     */
    private fun buildNotification(content: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Live Location Service")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()
    }

    /**
     * Creates a notification channel required for displaying notifications on Android O and above.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Location Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    /**
     * Stops location updates and destroys the service.
     * Removes location updates and marks the service as no longer running.
     */
    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
        }
    }

    /**
     * Binds the service to an activity (not used in this case).
     *
     * @param intent The intent passed to bind the service.
     * @return Always returns null as this service is not bound.
     */
    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Saves the provided location data to the CSV file.
     *
     * @param location The location data to be saved.
     */
    private fun saveLocationToCSV(location: Location) {
        val writer = FileWriter(csvFile, true)
        writer.append("${getCurrentTimestamp()},${location.latitude},${location.longitude}\n")
        writer.flush()
        writer.close()
    }

    /**
     * Returns the current timestamp formatted as "yyyyMMdd_HHmmss".
     *
     * @return The formatted timestamp.
     */
    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }
}