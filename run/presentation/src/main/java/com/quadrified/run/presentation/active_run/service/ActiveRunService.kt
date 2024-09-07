package com.quadrified.run.presentation.active_run.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.quadrified.core.presentation.ui.formatted
import com.quadrified.run.domain.RunningTracker
import com.quadrified.run.presentation.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

/**
 * Foreground Service to track run
 */
class ActiveRunService : Service() {

    // by lazy => to initialize only when first interacted with
    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    // Base notification
    private val baseNotification by lazy {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(com.quadrified.core.presentation.designsystem.R.drawable.logo)
            .setContentText(getString(R.string.active_run))
    }

    // Injecting RunningTracker from "run/domain"
    // Singleton and has shared state
    // Not injecting in class because this is a "Service" (check line 18)
    private val runningTracker by inject<RunningTracker>()

    // Creating our own coroutineScope
    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // For Bound service, to communicate with screen
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // Triggered when a new intent is delivered to this service from Activity
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                // Extracting activity class from "putExtra"
                val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_CLASS)
                    ?: throw IllegalArgumentException("No activity class provided")

                start(Class.forName(activityClass))
            }

            ACTION_STOP -> stop()

        }

        // To not kill this service
        return START_STICKY
    }

    // Starting service and Passing class
    private fun start(activityClass: Class<*>) {
        if (!isServiceActive) {
            isServiceActive = true
            createNotificationChannel()

            // Creating Deep Link
            val activityIntent = Intent(applicationContext, activityClass).apply {
                data = "runique://active_run".toUri()
                // open already existing instance of Active Run activity/screen instead of new instance each time link is opened
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            // Creating pendingIntent as deep link to redirect to "run_active" screen
            val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }

            val notification = baseNotification
                .setContentText("00:00:00")
                .setContentIntent(pendingIntent) // fired when notification is clicked
                .build()

            /**
             * To make above work:
             * 1. Register Deep Link in "app/AndroidManifest.xml"
             * 2. Register Deep Link in "NavigationRoot"
             */

            // Launching Service with Notification
            // Do not pass "0"
            startForeground(1, notification)

            // Updating Notification with new tracking timer data
            updateNotification()
        }

    }

    private fun updateNotification() {
        runningTracker.elapsedTime
            .onEach { elapsedTime ->
                val notification = baseNotification
                    .setContentText(elapsedTime.formatted())
                    .build()

                // Notifying an existing notification with new data
                // notify(id) should be same as startForeground(id)
                notificationManager.notify(1, notification)
            }
            .launchIn(serviceScope)
    }

    private fun stop() {
        stopSelf() // Stopping service
        isServiceActive = false
        serviceScope.cancel() // Canceling CoroutineScope

        // Reinitializing for restarting serviceScope
        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.active_run),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        // For as long as run tracking is active
        // True also when run is paused
        var isServiceActive = false
        private const val CHANNEL_ID = "active_run"

        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"

        private const val EXTRA_ACTIVITY_CLASS = "EXTRA_ACTIVITY_CLASS"

        // Intent to start service from outside => used in onStartCommand()
        fun createStartIntent(context: Context, activityClass: Class<*>): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_START
                // putExtra => to pass key-value data between components (such as activities or services)
                putExtra(EXTRA_ACTIVITY_CLASS, activityClass.name)
            }
        }

        // Intent to stop service from outside => used in onStartCommand()
        fun createStopIntent(context: Context): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }
}