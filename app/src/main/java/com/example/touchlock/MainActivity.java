package com.example.touchlock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

public class MainActivity extends AppCompatActivity {
   private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5;

   private String CHANNEL_ID;
   private BubblesManager bubblesManager;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      Toolbar toolbar =  findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      ActionBar actionBar = getSupportActionBar();

      findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

            if (checkDrawOverlayPermission()) {
               createNotification();
               Intent intent = new Intent(MainActivity.this, WindowManageActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              // Intent startMain = new Intent(Intent.ACTION_MAIN);
               //startMain.addCategory(Intent.CATEGORY_HOME);
               startActivity(intent);
               finish();
            }
         }
      });


   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.option_menu, menu);
      return true;
   }


   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      //..
      return super.onOptionsItemSelected(item);
   }
   private void createNotificationChannel() {
      CharSequence channelName = CHANNEL_ID;
      String channelDesc = "channelDesc";
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         int importance = NotificationManager.IMPORTANCE_DEFAULT;
         NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
         channel.setDescription(channelDesc);
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
            .setContentTitle("Khóa màn hình !")
            .setContentText("Chương trình đang hoạt động.")
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
