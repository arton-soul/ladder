package com.dearyoti.lexiladder.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.dearyoti.lexiladder.MainActivity
import com.dearyoti.lexiladder.domain.model.Level
import com.dearyoti.lexiladder.domain.service.MasteryTestEngine
import com.dearyoti.lexiladder.navigation.Screen

object ExamNotificationHelper {

    const val CHANNEL_ID = "exam_readiness_channel"
    const val NOTIFICATION_ID_BASE = 3000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Level Mastery & Exam Alerts"
            val descriptionText = "Notifications when you become eligible to take a level certification exam."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showExamUnlockedNotification(
        context: Context,
        level: Level,
        isHundredPercent: Boolean,
        uiLanguage: String = "en"
    ) {
        createNotificationChannel(context)

        // Android 13+ runtime permission guard
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!permissionGranted) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("startDestination", Screen.MasteryTest.createRoute(level.id))
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            level.order * 10 + (if (isHundredPercent) 2 else 1),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isId = uiLanguage == "id"
        val title = if (isHundredPercent) {
            if (isId) "100% Materi Terselesaikan! 🏆" else "100% Material Completed! 🏆"
        } else {
            if (isId) "Ujian Level Terbuka! 🎯" else "Level Exam Unlocked! 🎯"
        }

        val message = if (isHundredPercent) {
            if (isId)
                "Luar biasa! 100% materi kosakata di ${level.title} telah Anda pelajari. Ayo ikuti Ujian Tingkat Level sekarang untuk sertifikasi!"
            else
                "Incredible! 100% of vocabulary lessons in ${level.title} completed. Take the Level Exam now to get certified!"
        } else {
            val reqWords = MasteryTestEngine.calculateRequiredStudyCount(level.totalWords)
            if (isId)
                "Selamat! Anda telah mempelajari 80% materi ($reqWords kata) di ${level.title}. Ujian Naik Level kini sudah terbuka!"
            else
                "Congratulations! You've completed 80% of lesson material ($reqWords words) in ${level.title}. Level Exam is now unlocked!"
        }

        val notifId = NOTIFICATION_ID_BASE + (level.order * 10) + (if (isHundredPercent) 2 else 1)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_big_on)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notifId, builder.build())
        } catch (_: SecurityException) {
            // Safe fallback if notification permission is revoked
        }
    }
}
