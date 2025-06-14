package com.pedro.whatsapp.firebaseMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pedro.whatsapp.R
import com.pedro.whatsapp.activities.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val titulo = remoteMessage.notification?.title
        val mensagem = remoteMessage.notification?.body

        if (!titulo.isNullOrEmpty() && !mensagem.isNullOrEmpty()) {
            mostrarNotificacao(titulo, mensagem)
        }
    }

    private fun mostrarNotificacao(titulo: String, mensagem: String) {
        val canalId = "mensagens"

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacao = NotificationCompat.Builder(this, canalId)
            .setSmallIcon(R.drawable.logo_verde)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                canalId, "Mensagens",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(canal)
        }

        manager.notify(1, notificacao)
    }
}
