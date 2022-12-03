package com.aks.mygrocery.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aks.mygrocery.R
import com.aks.mygrocery.app.MyGroceryApp
import com.aks.mygrocery.base.BaseActivity
import com.aks.mygrocery.models.FcmTokenModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String){
        val uid = MyGroceryApp.instance.firebaseAuth.currentUser!!.uid
        val fcmTokenModel = FcmTokenModel(token,uid,"all")
        MyGroceryApp.instance.firebaseFirestore.collection("PushNotificationIDAdminWise")
            .document("bcI5ARwAoHMLLQGdIXlHILEnlZ63")
            .collection("FCMToken")
            .document(uid)
            .set(fcmTokenModel)
            .addOnSuccessListener {
                Log.e("TAG", "sendRegistrationToServer: Saved Successfully")
            }.addOnFailureListener {
                Log.e("TAG", "sendRegistrationToServer: ${it.localizedMessage}")
            }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        sendNotification(message)
    }

    private fun sendNotification(remoteMessage: RemoteMessage){
        val intent = Intent(this, BaseActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,  1/* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = "Default"
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
        }
    }

}