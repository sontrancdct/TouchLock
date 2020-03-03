package com.example.touchlock;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

public class FloatingService extends Service implements View.OnTouchListener, View.OnClickListener {
   private WindowManager mWindowManager, windowManagerClose;
   private MyGroupView mViewIcon, mViewLock;
   private WindowManager.LayoutParams mIconviewParams, mLockviewParams, _closeParams;

   private ImageView close;
   private LinearLayout layout;
   private MoveAnimator animator;
   private Animation shake;
   private Context context;

   private int screen_width, screen_height;

   private int state;
   private int i =0;
   private static final int TYPE_ICON = 0;
   private static final int TYPE_LOCK = 1;

   ImageButton imageButton;
   private int initialX;
   private int initialY;
   private float initialTouchX;
   private float initialTouchY;



   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      Visibility();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         _closeParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
      }
      _closeParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
      _closeParams.x = 0;
      _closeParams.y = 200;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         mIconviewParams = new WindowManager.LayoutParams(
            (int) (0.18 * screen_width),
            (int) (0.18 * screen_width),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
               | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
         );
      }

      mIconviewParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
      mIconviewParams.x = screen_width / 2;
      mIconviewParams.y = -screen_height / 3;
      mWindowManager.addView(mViewIcon, mIconviewParams);

      windowManagerClose.addView(layout, _closeParams);
      layout.setVisibility(View.INVISIBLE);
      close.startAnimation(shake);

      return START_STICKY;
   }
   @Override
   public void onCreate() {
      super.onCreate();
      createIconFloating();
      createLockView();
      getScreenSize();
      showFloating();
   }




   @SuppressLint("ClickableViewAccessibility")
   private void createIconFloating() {
      mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
      windowManagerClose = (WindowManager) getSystemService(WINDOW_SERVICE);
      mViewIcon = new MyGroupView(this);
      View view = View.inflate(this, R.layout.view_icon, mViewIcon);
      mViewIcon.setOnTouchListener(this);

      imageButton = view.findViewById(R.id.imageButtonIcon);
      imageButton.setOnTouchListener(this);
      imageButton.setOnClickListener(this);

      close = new ImageView(this);
      close.setImageResource(R.drawable.close);
      shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wiggle);
      shake.setRepeatCount(Animation.INFINITE);
      layout = new LinearLayout(this);
      layout.addView(close);
      animator = new MoveAnimator();

   }

   @SuppressLint("ClickableViewAccessibility")
   private void showFloating() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         _closeParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
      }
      _closeParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
      _closeParams.x = 0;
      _closeParams.y = 100;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         mIconviewParams = new WindowManager.LayoutParams(
            (int) (0.18 * screen_width),
            (int) (0.18 * screen_width),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
               | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
         );
      }
      mIconviewParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
      mIconviewParams.x = screen_width / 2;
      mIconviewParams.y = 0;

      if (!mViewIcon.isShown()) {
         mWindowManager.addView(mViewIcon, mIconviewParams);
         windowManagerClose.addView(layout, _closeParams);
         layout.setVisibility(View.INVISIBLE);
         close.startAnimation(shake);

      }
   }
    @Override
   public boolean onTouch(View v, MotionEvent event) {
      switch (event.getAction()){
         case MotionEvent.ACTION_DOWN:
            initialX = mIconviewParams.x;
            initialY = mIconviewParams.y;
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            animator.stop();
            break;
         case MotionEvent.ACTION_UP:
            if (MathUtil.betweenExclusive(mIconviewParams.x, -100, 100) && !MathUtil.betweenExclusive(mIconviewParams.y, screen_height / 3, screen_height / 2)) {
               //moving to center range of screen
               animator.start(screen_width / 2, mIconviewParams.y);
               ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
               layoutParams.width = (int) (0.18 * screen_width);
               layoutParams.height = (int) (0.18 * screen_width);
               mViewIcon.setLayoutParams(layoutParams);
               mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
               layout.setVisibility(View.INVISIBLE);

            } else if (MathUtil.betweenExclusive((int) event.getRawX(), 0, screen_width / 5)) {
               //move to left of screen
               if (MathUtil.betweenExclusive((int) event.getRawY(), 0, screen_height / 10)) {
                  // myParams.y = 0 ;
                  animator.start(-screen_width / 2, -((screen_height / 2) - 150));
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               } else if (MathUtil.betweenExclusive((int) event.getRawY(), 9 * (screen_height / 10), screen_height)) {
                  animator.start(-screen_width / 2, screen_height / 2 - 150);
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               } else {
                  animator.start(-screen_width / 2, mIconviewParams.y);
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               }

            } else if (MathUtil.betweenExclusive((int) event.getRawX(), screen_width - (screen_width / 5), screen_width)) {
               //move to right of screen
               if (MathUtil.betweenExclusive((int) event.getRawY(), 0, screen_height / 10)) {
                  // myParams.y = 0 ;
                  animator.start(screen_width / 2, -((screen_height / 2) - 150));
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               } else if (MathUtil.betweenExclusive((int) event.getRawY(), 9 * (screen_height / 10), screen_height)) {
                  animator.start(screen_width / 2, screen_height / 2 - 150);
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               } else {
                  animator.start(screen_width / 2, mIconviewParams.y);
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               }

            } else if (MathUtil.betweenExclusive((int) event.getRawX(), screen_width / 5, 2 * (screen_width / 5))) {
               //move to left of screen
               if (MathUtil.betweenExclusive((int) event.getRawY(), 0, screen_height / 10)) {
                  animator.start(-screen_width / 2, -((screen_height / 2) - 150));
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               } else if (MathUtil.betweenExclusive((int) event.getRawY(), 9 * (screen_height / 10), screen_height)) {
                  animator.start(-screen_width / 2, screen_height / 2 - 150);
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               } else {
                  animator.start(-screen_width / 2, mIconviewParams.y);
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               }
            } else if (MathUtil.betweenExclusive((int) event.getRawX(), 3 * (screen_width / 5), screen_width)) {
               //move to right of screen
               if (MathUtil.betweenExclusive((int) event.getRawY(), 0, screen_height / 10)) {

                  // myParams.y = 0 ;
                  animator.start(screen_width / 2, -((screen_height / 2) - 150));
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               } else if (MathUtil.betweenExclusive((int) event.getRawY(), 9 * (screen_height / 10), screen_height)) {
                  animator.start(screen_width / 2, screen_height / 2 - 150);
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               } else {
                  animator.start(screen_width / 2, mIconviewParams.y);
                  ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
                  layoutParams.width = (int) (0.18 * screen_width);
                  layoutParams.height = (int) (0.18 * screen_width);
                  mViewIcon.setLayoutParams(layoutParams);
                  mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
                  layout.setVisibility(View.INVISIBLE);
               }
            } else if (MathUtil.betweenExclusive(mIconviewParams.x, -50, 50) && MathUtil.betweenExclusive(mIconviewParams.y, screen_height / 5, screen_height / 2)) {
               Visibility();
               stopSelf();
            } else {
               //not in either of the above cases
               animator.start(screen_width / 2, mIconviewParams.y);
               ViewGroup.LayoutParams layoutParams = mViewIcon.getLayoutParams();
               layoutParams.width = (int) (0.18 * screen_width);
               layoutParams.height = (int) (0.18 * screen_width);
               mViewIcon.setLayoutParams(layoutParams);
               mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
               layout.setVisibility(View.INVISIBLE);
            }
            break;
         case MotionEvent.ACTION_MOVE:
            layout.setVisibility(View.VISIBLE);
            mIconviewParams.x = initialX + (int) (event.getRawX() - initialTouchX);
            mIconviewParams.y = initialY + (int) (event.getRawY() - initialTouchY);
            mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
            break;
      }
      return false;
   }
   int clickCount = 0;
   long startTime;
   long duration;
   static final int MAX_DURATION = 400;
   @SuppressLint("ClickableViewAccessibility")
   private void createLockView() {
      mViewLock = new MyGroupView(this);
      View view = View.inflate(this, R.layout.lock_fullview, mViewLock);
      
      mViewLock.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction() & MotionEvent.ACTION_MASK)
            {
               case MotionEvent.ACTION_DOWN:
                  startTime = System.currentTimeMillis();
                  clickCount++;
                  break;
               case MotionEvent.ACTION_UP:
                  long time = System.currentTimeMillis() - startTime;
                  duration=  duration + time;
                  if(clickCount == 3)
                  {
                     if(duration<= MAX_DURATION)
                     {
                        showIcon();
                        Toast.makeText(FloatingService.this, "Thoát khóa !", Toast.LENGTH_SHORT).show();
                     }
                     clickCount = 0;
                     duration = 0;
                     break;
                  }
            }
            return true;
         }
      });


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
         mWindowManager.removeViewImmediate(mViewLock);

      }catch (Exception e)
      {
         e.printStackTrace();
      }
      mWindowManager.addView(mViewIcon, mIconviewParams);
      state = TYPE_ICON;
   }

   private void showLock() {
      try {
         mWindowManager.removeViewImmediate(mViewIcon);

      }catch (Exception e)
      {
         e.printStackTrace();
      }
      mWindowManager.addView(mViewLock, mLockviewParams);
      state = TYPE_LOCK;
   }

   @Override
   public void onClick(View v) {
      switch (v.getId()){
         case R.id.imageButtonIcon:
            showLock();
            Toast.makeText(this, "Đã khóa cảm ứng !", Toast.LENGTH_SHORT).show();
            break;
            default:
               break;
      }
   }

   private static class MathUtil {
      public static boolean betweenExclusive(int x, int min, int max) {
         return x > min && x < max;
      }
   }
   private void getScreenSize() {
      Display display = mWindowManager.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      screen_width = size.x;
      screen_height = size.y;

   }
   private class MoveAnimator implements Runnable {

      private Handler handler = new Handler(Looper.getMainLooper());
      private float destinationX;
      private float destinationY;
      private long startingTime;

      private void start(float x, float y) {
         this.destinationX = x;
         this.destinationY = y;
         startingTime = System.currentTimeMillis();
         handler.post(this);
      }

      @Override
      public void run() {
         if (mViewIcon != null && mViewIcon.getParent() != null) {
            float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);

            float deltaX = (destinationX - mIconviewParams.x) * progress;
            float deltaY = (destinationY - mIconviewParams.y) * progress;
            move(deltaX, deltaY);
            if (progress < 1) {
               handler.post(this);
            }
         }
      }

      private void stop() {
         handler.removeCallbacks(this);
      }
   }
   protected void move(float deltaX, float deltaY) {
      mIconviewParams.x += deltaX;
      mIconviewParams.y += deltaY;
      mWindowManager.updateViewLayout(mViewIcon, mIconviewParams);
   }
   private void Visibility() {
      if (mWindowManager != null) {
         mWindowManager.removeViewImmediate(mViewIcon);
         windowManagerClose.removeViewImmediate(layout);
      }
   }

}
