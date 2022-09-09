package com.example.dailyselfie;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class Alarm extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;

    // Notification action elements
    private Intent mNotificationIntent;
    private PendingIntent mPendingIntent;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            mNotificationIntent = new Intent(context,MainActivity.class);
            mPendingIntent = PendingIntent.getActivity(context,0,mNotificationIntent,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

            // Cài đặt các thông tin cho thông báo
            Notification.Builder nofi = new Notification.Builder(context)
                    .setTicker("Time for selfie")// Văn bản hiển thị trên thanh appBar
                    .setSmallIcon(R.drawable.ic_photo_camera)// Icon trong bố cục thông báo
                    .setContentTitle("Daily Selfie")// Hàng đầu tiên của thông báo
                    .setContentText("Time for another selfie")// Hàng thứ 2 của thông báo
                    .setContentIntent(mPendingIntent)// Cung cấp 1 hành động khi được nhấp vào
                    .setAutoCancel(true);//Thông báo tự động hủy khi được nhấp vào

            // Cài đặt thông báo cho ứng dụng
            String chanelID = "ALARM";
            NotificationChannel channel = new NotificationChannel(chanelID,"Your alarm is here", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            nofi.setChannelId(chanelID);
            notificationManager.notify(NOTIFICATION_ID,nofi.build());
            Toast.makeText(context, "Notification", Toast.LENGTH_SHORT).show();


        }catch (Exception e){
            Log.d("NOTIFICATION", e.getMessage().toString());
        }
    }
}
