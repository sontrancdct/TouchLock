package com.example.touchlock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

public class WindowManageActivity extends AppCompatActivity {
   private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5;
   private FloatViewManager mFloatViewManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      startService(new Intent(this, FloatingService.class));
      finish();
   }
}
