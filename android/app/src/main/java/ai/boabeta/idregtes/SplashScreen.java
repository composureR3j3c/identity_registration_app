package com.boabeta.idregtes;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import io.flutter.plugin.common.MethodCall;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ai.tech5.sdk.abis.T5AirSnap.T5AirSnap;

public class SplashScreen extends AppCompatActivity {
    TextView txt_version_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        txt_version_name = findViewById(R.id.txt_version_name);
        PackageInfo pInfo = null;

        T5AirSnap m_cellSdk = new T5AirSnap(this);
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;

            txt_version_name.setText("v:" + versionName + "(" + m_cellSdk.getVersion() + ")");
            System.out.println("App Version: " + versionName + ", SDK Version: " + m_cellSdk.getVersion());
        } catch (PackageManager.NameNotFoundException e) {
            // throw new RuntimeException(e);
            System.out.println("RuntimeException: " + e.getMessage());

        }

        new Handler().postDelayed(() -> {

            startActivity(new Intent(SplashScreen.this, FingerCaptureActivityLocal.class));
            //  Intent intent = new Intent(
            //             SplashScreen.this,
            //             FingerCaptureActivityLocal.class
            //     );
            // String username = call.argument("username");
            //  String username = getIntent().getStringExtra("username");

            // if (username != null) {

            //     intent.putExtra(
            //             FingerCaptureActivityLocal.EXTRA_USERNAME,
            //             username);
            // }
            finish();

        }, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}