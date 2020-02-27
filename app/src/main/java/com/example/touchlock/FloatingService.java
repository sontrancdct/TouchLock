package com.example.touchlock;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class FloatingService extends Service implements View.OnTouchListener, View.OnClickListener {
   private WindowManager mWindowManager;
   private MyGroupView mViewIcon;
   private MyGroupView mViewLock;
   private WindowManager.LayoutParams mIconviewParams;
   private WindowManager.LayoutParams mLockviewParams;

   private int state;
   private int i =0;
   private static final int TYPE_ICON = 0;
   private static final int TYPE_LOCK = 1;

   private int previousX;
   private int previousY;
   private float mStartX;
   private float mStartY;

   ImageButton imageButton;
   ImageButton imageButtonRemove;

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      initView();
      return START_STICKY;
   }

   private void initView() {
      mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
      createIconView();
      createLockView();
      showIcon();
   }
   private void createLockView() {
      mViewLock = new MyGroupView(this);
      View view = View.inflate(this, R.layout.lock_fullview, mViewLock);

      Button btn_Exit = view.findViewById(R.id.btn_Exit);
      btn_Exit.setOnClickListener(this);

      mLockviewParams = new WindowManager.LayoutParams();
      mLockviewParams.width = WindowManager.LayoutParams.MATCH_PARENT;
      mLockviewParams.height = WindowManager.LayoutParams.MATCH_PARENT;
      mLockviewParams.gravity = Gravity.CENTER;
      mLockviewParams.format = PixelFormat.TRANSLUCENT;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         mLockviewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
      }
      mLockviewParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
      mLockviewParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
   }

   private void showIcon() {
      try {
         mWindowManager.removeView(mViewLock);

      }catch (Exception e)
      {
         e.printStackTrace();
      }
      mWindowManager.addView(mViewIcon, mIconviewParams);
      state = TYPE_ICON;
   }

   private void showLock() {
      try {
         mWindowManager.removeView(mViewIcon);

      }catch (Exception e)
      {
         e.printStackTrace();
      }
      mWindowManager.addView(mViewLock, mLockviewParams);
      state = TYPE_LOCK;
   }

   @SuppressLint("ClickableViewAccessibility")
   private void createIconView() {
      mViewIcon = new MyGroupView(this);
      View view = View.inflate(this, R.layout.view_icon, mViewIcon);
      mViewIcon.setOnTouchListener(this);

      imageButton = view.findViewById(R.id.imageButtonIcon);
      imageButtonRemove = view.findViewById(R.id.imageButtonRemove);
      imageButton.setOnClickListener(this);
      imageButtonRemove.setOnClickListener(this);
      imageButton.setOnTouchListener(this);

      mIconviewParams = new WindowManager.LayoutParams();
      mIconviewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
      mIconviewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
      mIconviewParams.gravity = Gravity.CENTER;
      mIconviewParams.format = PixelFormat.TRANSLUCENT;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         mIconviewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
      }
      //mIconviewParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
      mIconviewParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

   }
   @Override
   public boolean onTouch(View v, MotionEvent event) {
      switch (event.getAction()){
         case MotionEvent.ACTION_DOWN:
            if(state == TYPE_ICON){
               previousX = mIconviewParams.x;
               previousY = mIconviewParams.y;
            } else {
               previousX = mLockviewParams.x;
               previousY = mLockviewParams.y;
            }
            mStartX = event.getRawX();
            mStartY = event.getRawY();
            break;
         case MotionEvent.ACTION_MOVE:
            double deltaX =  event.getRawX() - mStartX;
            double deltaY =  event.getRawY() - mStartY;

            if(state == TYPE_ICON){
               mIconviewParams.x = (int) (previousX + deltaX);
               mIconviewParams.y = (int) (previousY + deltaY);
               mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);

//               if (MathUtil.betweenExclusive((int) event.getRawX(), 0, screen_width / 5) || MathUtil.betweenExclusive((int) event.getRawX(), screen_width - (screen_width / 5), screen_width)) {
//                  android.view.ViewGroup.LayoutParams layoutParams = imageButton.getLayoutParams();
//                  layoutParams.width = (int) (0.18 * screen_width);
//                  layoutParams.height = (int) (0.18 * screen_width);
//                  imageButton.setLayoutParams(layoutParams);
//                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
//               } else if (MathUtil.betweenExclusive((int) event.getRawX(), 2 * (screen_width / 5), 3 * (screen_width / 5))) {
//                  android.view.ViewGroup.LayoutParams layoutParams = imageButton.getLayoutParams();
//                  layoutParams.width = (int) (0.18 * screen_width) + 100 + 100;
//                  layoutParams.height = (int) (0.18 * screen_width) + 100 + 100;
//                  imageButton.setLayoutParams(layoutParams);
//                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
//               } else if (MathUtil.betweenExclusive((int) event.getRawX(), screen_width / 5, 2 * (screen_width / 5)) || MathUtil.betweenExclusive((int) event.getRawX(), 3 * (screen_width / 5), screen_width)) {
//                  android.view.ViewGroup.LayoutParams layoutParams = imageButton.getLayoutParams();
//                  layoutParams.width = (int) (0.18 * screen_width) + 100;
//                  layoutParams.height = (int) (0.18 * screen_width) + 100;
//                  imageButton.setLayoutParams(layoutParams);
//                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
//
//               }
            }
            break;
         case MotionEvent.ACTION_OUTSIDE:
            break;
      }
      return false;
   }
   @Override
   public void onClick(View v) {
      switch (v.getId()){
         case R.id.imageButtonIcon:
            showLock();
            break;
         case R.id.imageButtonRemove:
            stopSelf();
            removeView();
            break;
         case R.id.btn_Exit:
            exitLock();
            break;
            default:
               break;
      }
   }

   private void exitLock() {
      i++;
      Handler handler = new Handler();
      Runnable r = new Runnable() {
         @Override
         public void run() {
            i = 0;
         }
      };
      if (i == 1) {
         //Single click
         handler.postDelayed(r, 250);
      } else if (i == 2) {
         showIcon();
         i = 0;
      }
   }

   private void removeView() {
      try {
         mWindowManager.removeView(mViewIcon);
      }
      catch (Exception e){
         e.printStackTrace();
      }
   }
}
