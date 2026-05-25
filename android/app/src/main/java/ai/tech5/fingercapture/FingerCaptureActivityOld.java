package com.tech5.fingercapture;

import static ai.tech5.sdk.abis.T5AirSnap.StandardErrorCodes.SE_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tech5.fingercapture.tech5.SettingsPrefManager;

import java.util.LinkedHashSet;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import ai.tech5.finger.utils.CaptureMode;
import ai.tech5.finger.utils.Finger;
import ai.tech5.finger.utils.FingerCaptureResult;
import ai.tech5.finger.utils.ImageConfiguration;
import ai.tech5.finger.utils.SegmentationMode;
import ai.tech5.finger.utils.T5FingerCaptureController;
import ai.tech5.finger.utils.T5FingerCapturedListener;
import ai.tech5.sdk.abis.T5AirSnap.T5AirSnap;
import android.os.Handler;
import android.widget.TextView;

public class FingerCaptureActivityOld extends AppCompatActivity implements T5FingerCapturedListener {

    private static final String TAG = "TECH5";
    public static final String EXTRA_USERNAME = "username";

    private SettingsPrefManager settingsPrefManager;
    private LightSensorHelper lightSensorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lightSensorHelper = new LightSensorHelper(this);
        lightSensorHelper.start();

        settingsPrefManager = new SettingsPrefManager(this);

        startFingerCapture();
    }

    private void startFingerCapture() {
        try {
        
            T5FingerCaptureController controller = T5FingerCaptureController.getInstance();

            // Set license first - empty string for online portal activation
            // String license = BuildConfig.TECH5_LICENSE != null ? BuildConfig.TECH5_LICENSE.trim() : "";
            // controller.setLicense(license);

            controller.setLivenessCheck(true);
            controller.setIsGetQuality(true);
            controller.setIsGetNist2Quality(true);
            controller.showElipses(true);
            controller.setDetectorThreshold(0.9f);
            controller.setTimeoutInSecs(30);
            controller.setCaptureMode(CaptureMode.CAPTURE_MODE_SELF);
            controller.setTitle("Capture Finger");
            controller.setShowBackButton(true);
            controller.setZoomFactor(1.5f);
            controller.setPropDenoise(true);
            controller.setCleanFingerPrints(true);
            controller.setOutsideCaptureFlag(false);

            String username = getIntent().getStringExtra(EXTRA_USERNAME);
            if (username != null && !username.isEmpty()) {
                controller.setUsername(username);
            }

            LinkedHashSet<SegmentationMode> segmentationModes = new LinkedHashSet<>();
            segmentationModes.add(SegmentationMode.SEGMENTATION_MODE_LEFT_THUMB);
            controller.setSegmentationModes(segmentationModes);

            ImageConfiguration segmentedConfig = new ImageConfiguration();
            segmentedConfig.setPrimaryImageType(ai.tech5.finger.utils.ImageType.IMAGE_TYPE_PNG);
            segmentedConfig.setIsCropImage(true);
            segmentedConfig.setCroppedImageWidth(300);
            segmentedConfig.setCroppedImageHeight(400);
            segmentedConfig.setPaddingColor(255);
            controller.setSegmentedFingerImagesConfig(segmentedConfig);

            controller.captureFingers(this, this);
            
        } catch (Exception e) {
            Log.e(TAG, "Unable to start finger capture", e);
            closeCapture();
        }
    }

    @Override
    public void onSuccess(FingerCaptureResult result) {
        try {
            Log.d(TAG, "Finger capture success");

            if (result.fingers == null || result.fingers.isEmpty()) {
                Log.e(TAG, "No fingers captured");
                closeCapture();
                return;
            }

            Finger finger = result.fingers.get(0);
            String base64Image = Base64.encodeToString(finger.primaryImage, Base64.NO_WRAP);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("base64Image", base64Image);
            resultIntent.putExtra("quality", finger.quality);
            resultIntent.putExtra("nistQuality", finger.nistQuality);
            resultIntent.putExtra("position", finger.pos);

            if (result.livenessScores != null && result.livenessScores.size() > finger.pos) {
                resultIntent.putExtra("livenessScore", result.livenessScores.get(finger.pos).score);
            }

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error processing result", e);
            closeCapture();
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        Log.e(TAG, errorMessage);
        Intent intent = new Intent();
        intent.putExtra("error", errorMessage);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onCancelled() {
        Log.e(TAG, "Finger capture cancelled");
        Intent intent = new Intent();
        intent.putExtra("error", "Capture cancelled");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onTimedout() {
        Log.e(TAG, "Finger capture timeout");
        Intent intent = new Intent();
        intent.putExtra("error", "Capture timeout");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private void closeCapture() {
        runOnUiThread(() -> {
            if (!isFinishing()) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensorHelper != null) {
            lightSensorHelper.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lightSensorHelper != null) {
            lightSensorHelper.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lightSensorHelper != null) {
            lightSensorHelper.stop();
        }
    }
}
