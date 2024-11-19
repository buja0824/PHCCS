package com.phhcs;

import android.os.Bundle;
import android.os.Build;
import android.graphics.Color;
import android.view.WindowManager;
import android.content.res.Configuration;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;

public class MainActivity extends ReactActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // 다크모드 감지
    int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    boolean isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    
    if (isDarkMode) {
      getWindow().setNavigationBarColor(Color.BLACK);
    } else {
      getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }
    
    // 내비게이션 바 영역을 레이아웃에서 고려하도록 설정
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      getWindow().setDecorFitsSystemWindows(true);
    }
  }

  @Override
  protected String getMainComponentName() {
    return "Phhcs";
  }

  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new DefaultReactActivityDelegate(
        this,
        getMainComponentName(),
        DefaultNewArchitectureEntryPoint.getFabricEnabled());
  }
}
