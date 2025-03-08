package com.example.weatherapp.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import java.util.Locale
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val CHANNEL_ID = "WEATHER_NOTIFICATION_CHANNEL"
    private const val NOTIFICATION_WORK_TAG = "test_weather_notification"
    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

    fun createNotificationChannel(context: Context) {
        val name = "Weather Notifications"
        val descriptionText = "Test"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun scheduleTestNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startNotificationWork(context)
                }
                (context as? Activity)?.shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                ) == true -> {
                    showPermissionRationaleDialog(context)
                }
                else -> {
                    (context as? Activity)?.requestPermissions(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        } else {
            startNotificationWork(context)
        }
    }

    /*
    private fun startNotificationWork(context: Context) {
        val constraints = Constraints.Builder().build()

        val notificationWork = PeriodicWorkRequestBuilder<HourlyNotificationWorker>(
            1, TimeUnit.HOURS, 15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NOTIFICATION_WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWork
        )
    }*/
    private fun startNotificationWork(context: Context) {
        val testWorkRequest = OneTimeWorkRequestBuilder<TestNotificationWorker>()
            .setInitialDelay(100, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            NOTIFICATION_WORK_TAG,
            ExistingWorkPolicy.REPLACE,
            testWorkRequest
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationaleDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Bildirim İzni")
            .setMessage("Hava durumu bildirimleri alabilmek için izne ihtiyacımız var.")
            .setPositiveButton("İzin Ver") { _, _ ->
                (context as? Activity)?.requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    class TestNotificationWorker(
        context: Context,
        params: WorkerParameters
    ) : Worker(context, params) {
        override fun doWork(): Result {
            val prefs = applicationContext.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
            val languageCode = prefs.getString("selected_language", Locale.getDefault().language)
                ?: Locale.getDefault().language

            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val configuration = applicationContext.resources.configuration
            configuration.setLocale(locale)
            configuration.setLocales(android.os.LocaleList(locale))
            val localizedContext = applicationContext.createConfigurationContext(configuration)

            val notificationManager = NotificationManagerCompat.from(localizedContext)
            if (ActivityCompat.checkSelfPermission(
                    localizedContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val notification = NotificationCompat.Builder(localizedContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(localizedContext.getString(R.string.notification_title))
                    .setContentText(localizedContext.getString(R.string.notification_text))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(System.currentTimeMillis().toInt(), notification)
            }
            startNotificationWork(applicationContext)
            return Result.success()
        }
    }

}
