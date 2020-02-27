package com.example.touchlock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

public class MainActivity extends AppCompatActivity {
   private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5;
   private FloatViewManager mFloatViewManager;

   private String CHANNEL_ID;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mFloatViewManager = new FloatViewManager(MainActivity.this);

      findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {


            if (checkDrawOverlayPermission()) {

               createNotification();
               //Tao su kien ket thuc app
               Intent startMain = new Intent(Intent.ACTION_MAIN);
               startMain.addCategory(Intent.CATEGORY_HOME);
               startActivity(startMain);
               finish();
            }
         }
      });
   }

   private void createNotificationChannel() {
      CharSequence channelName = CHANNEL_ID;
      String channelDesc = "channelDesc";
      // Create the NotificationChannel, but only on API 26+ because
      // the NotificationChannel class is new and not in the support library
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         int importance = NotificationManager.IMPORTANCE_DEFAULT;
         NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
         channel.setDescription(channelDesc);
         // Register the channel with the system; you can't change the importance
         // or other notification behaviors after this
         NotificationManager notificationManager = getSystemService(NotificationManager.class);
         assert notificationManager != null;
         NotificationChannel currChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
         if (currChannel == null)
            notificationManager.createNotificationChannel(channel);
      }
   }
   public void createNotification() {

      CHANNEL_ID = "Lock";
         createNotificationChannel();

         Intent intent = new Intent(this, WindowManageActivity.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

         PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

         NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_lock_black_24dp)
            .setContentTitle("Khóa màn hình !") //UiUtil.getStringSafe(R.string.app_name)
            .setContentText("Nhấn để hiển biểu tượng khóa.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_child_care_black_24dp, "Start", pendingIntent)
            .addAction(R.drawable.ic_child_care_black_24dp, "Stop", pendingIntent)
            .setAutoCancel(false);
         Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
         mBuilder.setSound(uri);


         NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
         int notificationId = (int) (System.currentTimeMillis()/4);
         notificationManager.notify(notificationId, mBuilder.build());
      }



   private void createNotification1() {
      Intent intent = new Intent(this, WindowManageActivity.class);
      PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

      Notification noti = new Notification.Builder(this)
         .setContentTitle("Khóa màn hình !")
         .setContentText("Nhấn để hiển biểu tượng khóa.")
         .setSmallIcon(R.drawable.ic_lock_black_24dp)
         .setContentIntent(pIntent)
         .addAction(R.drawable.ic_child_care_black_24dp, "Start", pIntent)
         .addAction(R.drawable.ic_child_care_black_24dp, "Stop", pIntent).build();
      NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      // hide the notification after its selected
      //noti.flags |= Notification.FLAG_AUTO_CANCEL;

      notificationManager.notify(0, noti);
   }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == REQUEST_CODE_DRAW_OVERLAY_PERMISSION) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
           // mFloatViewManager.showFloatView();
         }
      }
   }
   private boolean checkDrawOverlayPermission() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
         Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
         startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION);
         return false;
      } else {
         return true;
      }
   }
}
