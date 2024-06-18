package es.kab.torneox.Firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import es.kab.torneox.Control.ControlActivity
import es.kab.torneox.R

class PushNotificationsService : FirebaseMessagingService() {

    companion object{
        const val CHANNEL_ID = "FCM CID"
        const val CHANNEL_NAME = "FCM notification channel"
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.notification != null){
            val titol = remoteMessage.notification!!.title
            val cos = remoteMessage.notification!!.body
            setNotification(titol,cos)
        }
    }
    fun setNotification(titol:String?, cos:String?){
        val intent = Intent(this, ControlActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE)
        val notiBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(titol)
            .setContentText(cos)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel  = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }
        manager.notify(1,notiBuilder.build())
    }


}