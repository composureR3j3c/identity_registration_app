package com.tech5.fingercapture;

import static ai.tech5.sdk.abis.T5AirSnap.StandardErrorCodes.SE_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ai.tech5.finger.utils.CaptureMode;
import ai.tech5.finger.utils.CaptureSpeed;
import ai.tech5.finger.utils.Finger;
import ai.tech5.finger.utils.FingerCaptureResult;
import ai.tech5.finger.utils.ImageConfiguration;
import ai.tech5.finger.utils.ImageType;
import ai.tech5.finger.utils.LivenessScore;
import ai.tech5.finger.utils.SegmentationMode;
//import ai.tech5.finger.utils.Slap;
import ai.tech5.finger.utils.T5FingerCaptureController;
import ai.tech5.finger.utils.T5FingerCapturedListener;
import com.tech5.fingercapture.databinding.ActivityMainBinding;
import ai.tech5.sdk.abis.T5AirSnap.NistPosCode;


public class FingerCaptureActivity extends AppCompatActivity implements T5FingerCapturedListener {


    ActivityMainBinding binding;

    SettingsPrefManager settingsPrefManager;

    private static final String[] APP_PERMISSIONS = {Manifest.permission.CAMERA};
    private static String m_rootDirectory;
    private static ExecutorService m_service = null;
    private LightSensorHelper m_lightSensorHelper = null;

    private static void writeToFile(byte[] data, String path) {
        try {

            Log.d("TAG", "saving to " + path);
            File myFile = new File(path);

            File parentDir = myFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (myFile.exists()) {
                myFile.delete();
            }

            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            fOut.write(data);
            fOut.flush();
            fOut.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("FingerCaptureActivity", "onCreate()...");

        m_lightSensorHelper = new LightSensorHelper(this);
        m_lightSensorHelper.start();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        m_service = Executors.newFixedThreadPool(10);
        System.out.println("Executor service initialized with 10 threads");
        binding.btnCaptureFingers.setOnClickListener((View v) -> {
settingsPrefManager=new SettingsPrefManager(FingerCaptureActivity.this);

//            MyApp app = (MyApp) getApplication();
//            settingsPrefManager = new ViewModelProvider(app,
//                    ViewModelProvider.AndroidViewModelFactory.getInstance(app))
//                    .get(SettingsPrefManager.class);

            if (hasAllPermissionsGranted()) {
                startFingerCapture();
            } else {
                requestPermissionLauncher.launch(APP_PERMISSIONS);
            }


        });

        RadioButton[] buttons = {
                binding.chkBoxLeftSlap,
                binding.chkBoxRightSlap,
                binding.chkBoxLeftThumb,
                binding.chkBoxRightThumb,
                binding.chkBoxLeftIndex,
                binding.chkBoxRightIndex,
                binding.chkBoxLeftIndexMiddle,
                binding.chkBoxRightIndexMiddle,
                binding.chkBoxThumbs,
                binding.chkBox442,
                binding.chkBox4411
        };

        for (RadioButton rb : buttons) {
            rb.setOnClickListener(v -> {
                for (RadioButton other : buttons) {
                    if (other != rb) other.setChecked(false);
                }
            });
        }

//        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(FingerCaptureActivity.this, SettingsActivity.class);
//                startActivity(intent);
//finish();            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_deregister) {
            deregisterDevice();
        }else  if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
//           finish();
           return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void deregisterDevice() {

        showProgress(true);

        T5FingerCaptureController fingerCaptureController = T5FingerCaptureController.getInstance();
        fingerCaptureController.deregisterDevice(FingerCaptureActivity.this, isDeviceDeregistered -> {
            showProgress(false);

            if (isDeviceDeregistered) {
                showToast("Device DeRegistration success");

            } else {
                showToast("Device DeRegistration failed");
            }

        });

    }

    private void startFingerCapture() {

        if (binding.nameEditText.getText().toString().isEmpty()) {
            // Show error message, since this field is required
            binding.nameEditText.setError("Please enter Name");
            binding.nameEditText.requestFocus();
            return; // stop further execution
        }
        try {
            String selectedSlap = null;
            if (binding.chkBoxLeftSlap.isChecked()) {
                selectedSlap = "LeftSlap";
            } else if (binding.chkBoxRightSlap.isChecked()) {
                selectedSlap = "RightSlap";
            }

            if (selectedSlap != null) {
                getmissingfingerdialogue(FingerCaptureActivity.this, selectedSlap);
            } else  {
                if (!settingsPrefManager.is_Dialogue_PreviewEnabled()) {
                    Log.d("enrollactivity", "in !preferencesHelper.is_Dialogue_PreviewEnabled()");
                    showPreviewDialog(null);
                } else {
                    Log.d("enrollactivity", "in else");
                    capture(null);

                }
            }

        } catch (Exception ignored) {

        }
    }

//    public void capture(ArrayList<Integer> missingfingerId){
//        T5FingerCaptureController t5FingerCaptureController = T5FingerCaptureController.getInstance();
//        t5FingerCaptureController.setLicense("");
//
//        t5FingerCaptureController.showElipses(settingsPrefManager.isShowEllipsesEnabled());
//        t5FingerCaptureController.setLivenessCheck(settingsPrefManager.isLivenessEnabled());
//
//        t5FingerCaptureController.setIsGetQuality(settingsPrefManager.isGetQualityEnabled());
//        t5FingerCaptureController.setIsGetNist2Quality(settingsPrefManager.isGetNfiq2QualityEnabled());
//
//
//            t5FingerCaptureController.setDetectorThreshold(0.9f);
//        t5FingerCaptureController.setUsername(binding.nameEditText.getText().toString());
//        LinkedHashSet<SegmentationMode> segmentationModeSet = new LinkedHashSet<>();
////            if (binding.chkBoxLeftSlap.isChecked()) {
////                segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP);
////            }
////            if (binding.chkBoxRightSlap.isChecked()) {
////                segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_SLAP);
////            }
////            if (binding.chkBoxLeftThumb.isChecked()) {
////                segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_THUMB);
////            }
////
////            if (binding.chkBoxRightThumb.isChecked()) {
////                segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_THUMB);
////            }
//        if (binding.chkBoxLeftSlap.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP);
//        } else if (binding.chkBoxRightSlap.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_SLAP);
//        } else if (binding.chkBoxLeftThumb.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_THUMB);
//        } else if (binding.chkBoxRightThumb.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_THUMB);
//        } else if (binding.chkBoxLeftIndex.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_INDEX);
//        } else if (binding.chkBoxRightIndex.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_INDEX);
//        } else if (binding.chkBoxLeftIndexMiddle.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_INDEX_MIDDLE);
//        } else if (binding.chkBoxRightIndexMiddle.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_INDEX_MIDDLE);
//        } else if (binding.chkBoxThumbs.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_AND_RIGHT_THUMBS);
//        } else if (binding.chkBox442.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP);
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_SLAP);
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_AND_RIGHT_THUMBS);
//        } else if (binding.chkBox4411.isChecked()) {
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP);
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_SLAP);
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_THUMB);
//            segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_THUMB);
//        }
//
//
//        t5FingerCaptureController.setSegmentationModes(segmentationModeSet);
//
//        CaptureMode captureMode = CaptureMode.CAPTURE_MODE_SELF;
//
//        int mode = settingsPrefManager.getCaptureModeId();
//        if (mode == 0) {
//
//            captureMode = CaptureMode.CAPTURE_MODE_SELF;
//        } else if (mode == 1) {
//            captureMode = CaptureMode.CAPTURE_MODE_OPERATOR;
//
//        }
//
////        t5FingerCaptureController.setDetectorThreshold(settingsPrefManager.getDetectorThreshold());
//        t5FingerCaptureController.setCaptureMode(captureMode);
//        t5FingerCaptureController.setTitle("Finger Capture");
//        t5FingerCaptureController.setShowBackButton(false);
//
////        //add missing fingers here
////        ArrayList<Integer> missingFingers = new ArrayList<>();
//////          missingFingers.add(7);
//////            missingFingers.add(8);
//////            missingFingers.add(2);
//////         missingFingers.add(9);
//////         missingFingers.add(10);
//
//        if (missingfingerId!=null){
//            t5FingerCaptureController.setMissingFingers(missingfingerId);
//        }
//        int getspeed = settingsPrefManager.getCaptureSpeedId();
//
//        CaptureSpeed captureSpeed = CaptureSpeed.CAPTURE_SPEED_LOW;
//
//        if (getspeed == 0) {
//            captureSpeed = CaptureSpeed.CAPTURE_SPEED_LOW;
//        } else if (getspeed == 1) {
//            captureSpeed = CaptureSpeed.CAPTURE_SPEED_NORMAL;
//        } else if (getspeed == 2) {
//            captureSpeed = CaptureSpeed.CAPTURE_SPEED_HIGH;
//        }
//
//        t5FingerCaptureController.setCaptureSpeed(captureSpeed);
//        t5FingerCaptureController.setPropDenoise(settingsPrefManager.isProprietaryDenoiseEnabled());
//        t5FingerCaptureController.setCleanFingerPrints(settingsPrefManager.isCleanFingerprintsEnabled());
//
//        float luxOutsideThreshold = 200.0f;
//        float luxCurrentValue = m_lightSensorHelper.getCurrentLightValue();
//
//        boolean outsideCapture = ((luxCurrentValue > luxOutsideThreshold) ||
//                (luxCurrentValue < 0.0f));
//
//
//        t5FingerCaptureController.setOutsideCaptureFlag(outsideCapture);
//
//        ImageConfiguration segmentedFingersConfiguration = getImageConfiguration();
//
//
//        t5FingerCaptureController.setSegmentedFingerImagesConfig(segmentedFingersConfiguration);
//
//
//        ImageConfiguration slapConfig = new ImageConfiguration();
//        slapConfig.setPrimaryImageType(ImageType.IMAGE_TYPE_BMP);
////            slapConfig.setCompressionRatio(10);
//
//        slapConfig.setIsCropImage(false);
////            slapConfig.setCroppedImageWidth(1600);
////            slapConfig.setCroppedImageHeight(1500);
////            //0->Black color padding; 255->white color padding
////            slapConfig.setPaddingColor(0);
//
//        t5FingerCaptureController.setSlapImagesConfig(slapConfig);
//
//        t5FingerCaptureController.setTimeoutInSecs(60);
//
//        t5FingerCaptureController.setsavesdklogs(settingsPrefManager.isSaveSdkLogEnabled());
////        t5FingerCaptureController.setSavefingerprints(settingsPrefManager.isSaveFingerprintsEnabled());
//
//        t5FingerCaptureController.setSavefingerprints(true);
//        t5FingerCaptureController.captureFingers(FingerCaptureActivity.this, this);
//
//
//    }
public void capture(ArrayList<Integer> missingfingerId){
    System.out.println("Starting capture " + missingfingerId);
    T5FingerCaptureController t5FingerCaptureController = T5FingerCaptureController.getInstance();
    t5FingerCaptureController.setZoomFactor(settingsPrefManager.getZoomRatio());
    System.out.println("Zoom factor set to: " + settingsPrefManager.getZoomRatio());
    t5FingerCaptureController.setLicense(BuildConfig.TECH5_LICENSE);
    System.out.println("License set successfully");

    t5FingerCaptureController.showElipses(settingsPrefManager.isShowEllipsesEnabled());
    t5FingerCaptureController.setLivenessCheck(settingsPrefManager.isLivenessEnabled());

    t5FingerCaptureController.setIsGetQuality(settingsPrefManager.isGetQualityEnabled());
    t5FingerCaptureController.setIsGetNist2Quality(settingsPrefManager.isGetNfiq2QualityEnabled());


    t5FingerCaptureController.setDetectorThreshold(0.9f);
    t5FingerCaptureController.setUsername(binding.nameEditText.getText().toString());
    LinkedHashSet<SegmentationMode> segmentationModeSet = new LinkedHashSet<>();
    System.out.println("Segmentation modes set: " + segmentationModeSet);
    if (binding.chkBoxLeftSlap.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP);
    } else if (binding.chkBoxRightSlap.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_SLAP);
    } else if (binding.chkBoxLeftThumb.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_THUMB);
    } else if (binding.chkBoxRightThumb.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_THUMB);
    } else if (binding.chkBoxLeftIndex.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_INDEX);
    } else if (binding.chkBoxRightIndex.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_INDEX);
    } else if (binding.chkBoxLeftIndexMiddle.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_INDEX_MIDDLE);
    } else if (binding.chkBoxRightIndexMiddle.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_INDEX_MIDDLE);
    } else if (binding.chkBoxThumbs.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_AND_RIGHT_THUMBS);
    } else if (binding.chkBox442.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP);
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_SLAP);
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_AND_RIGHT_THUMBS);
    } else if (binding.chkBox4411.isChecked()) {
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP);
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_SLAP);
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_THUMB);
        segmentationModeSet.add(SegmentationMode.SEGMENTATION_MODE_RIGHT_THUMB);
    }
    System.out.println("Final Segmentation modes: " + segmentationModeSet);

    t5FingerCaptureController.setSegmentationModes(segmentationModeSet);

    CaptureMode captureMode = CaptureMode.CAPTURE_MODE_SELF;
    System.out.println("Capture mode set to: " + captureMode);
    int mode = settingsPrefManager.getCaptureModeId();
    if (mode == 0) {

        captureMode = CaptureMode.CAPTURE_MODE_SELF;
    } else if (mode == 1) {
        captureMode = CaptureMode.CAPTURE_MODE_OPERATOR;

    }
    System.out.println("Final capture mode: " + captureMode);

//        t5FingerCaptureController.setDetectorThreshold(settingsPrefManager.getDetectorThreshold());
    t5FingerCaptureController.setCaptureMode(captureMode);
    t5FingerCaptureController.setTitle("Finger Capture");
    t5FingerCaptureController.setShowBackButton(false);
    System.out.println("Finger capture initialized");

//        //add missing fingers here
//        ArrayList<Integer> missingFingers = new ArrayList<>();
////          missingFingers.add(7);
////            missingFingers.add(8);
////            missingFingers.add(2);
////         missingFingers.add(9);
////         missingFingers.add(10);

    if (missingfingerId!=null){
        t5FingerCaptureController.setMissingFingers(missingfingerId);
    }
    int getspeed = settingsPrefManager.getCaptureSpeedId();

    CaptureSpeed captureSpeed = CaptureSpeed.CAPTURE_SPEED_MEDIUM;

    if (getspeed == 0) {
        captureSpeed = CaptureSpeed.CAPTURE_SPEED_VERYSLOW;
    } else if (getspeed == 1) {
        captureSpeed = CaptureSpeed.CAPTURE_SPEED_SLOW;
    } else if (getspeed == 2) {
        captureSpeed = CaptureSpeed.CAPTURE_SPEED_MEDIUM;
    } else if (getspeed == 3) {
        captureSpeed = CaptureSpeed.CAPTURE_SPEED_FAST;
    } else if (getspeed == 4) {
        captureSpeed = CaptureSpeed.CAPTURE_SPEED_VERYFAST;
    }
    System.out.println("Capture speed set to: " + captureSpeed);
    t5FingerCaptureController.setCaptureSpeed(captureSpeed);
    t5FingerCaptureController.setPropDenoise(settingsPrefManager.isProprietaryDenoiseEnabled());
    t5FingerCaptureController.setCleanFingerPrints(settingsPrefManager.isCleanFingerprintsEnabled());
    System.out.println("Proprietary denoise: " + settingsPrefManager.isProprietaryDenoiseEnabled() + ", Clean fingerprints: " + settingsPrefManager.isCleanFingerprintsEnabled());
    float luxOutsideThreshold = 200.0f;
    float luxCurrentValue = m_lightSensorHelper.getCurrentLightValue();

    boolean outsideCapture = ((luxCurrentValue > luxOutsideThreshold) ||
            (luxCurrentValue < 0.0f));


    t5FingerCaptureController.setOutsideCaptureFlag(outsideCapture);

    ImageConfiguration segmentedFingersConfiguration = getImageConfiguration();


    t5FingerCaptureController.setSegmentedFingerImagesConfig(segmentedFingersConfiguration);


    ImageConfiguration slapConfig = new ImageConfiguration();
    slapConfig.setPrimaryImageType(ImageType.IMAGE_TYPE_BMP);
//            slapConfig.setCompressionRatio(10);

    slapConfig.setIsCropImage(false);
//            slapConfig.setCroppedImageWidth(1600);
//            slapConfig.setCroppedImageHeight(1500);
//            //0->Black color padding; 255->white color padding
//            slapConfig.setPaddingColor(0);

    t5FingerCaptureController.setSlapImagesConfig(slapConfig);

    t5FingerCaptureController.setTimeoutInSecs(60);

    t5FingerCaptureController.setsavesdklogs(settingsPrefManager.isSaveSdkLogEnabled());
//        t5FingerCaptureController.setSavefingerprints(settingsPrefManager.isSaveFingerprintsEnabled());

    t5FingerCaptureController.setSavefingerprints(true);
    t5FingerCaptureController.setReversefingerprints(settingsPrefManager.isGetFingerReverseEnabled());


    t5FingerCaptureController.captureFingers(FingerCaptureActivity.this, this);


}
    private @NonNull ImageConfiguration getImageConfiguration() {
        ImageConfiguration segmentedFingersConfiguration = new ImageConfiguration();
        segmentedFingersConfiguration.setPrimaryImageType(ImageType.IMAGE_TYPE_PNG);
        segmentedFingersConfiguration.setRequireDisplayImage(false);
        segmentedFingersConfiguration.setDisplayImageType(ImageType.IMAGE_TYPE_BMP);

        //compresion ratio is only applicable for IMAGE_TYPE_WSQ
        segmentedFingersConfiguration.setCompressionRatio(10);

        segmentedFingersConfiguration.setIsCropImage(false);
        segmentedFingersConfiguration.setCroppedImageWidth(512);
        segmentedFingersConfiguration.setCroppedImageHeight(512);
        //0->Black color padding; 255->white color padding
        segmentedFingersConfiguration.setPaddingColor(255);
        return segmentedFingersConfiguration;
    }
    // 1. Result class


    // 2. Main validation method (your exact logic + details)
//    private QualityLivenessResult checkQualityLiveness(FingerCaptureResult result) {
//        List<String> failures = new ArrayList<>();
//        boolean isLivenessFailed = false;
//        boolean isQualityFailed = false;
//
//        // YOUR EXACT LIVELINESS LOGIC
//        if (settingsPrefManager.isLivenessEnabled() && result.livenessScores != null) {
//            for (LivenessScore livenessScore : result.livenessScores) {
//                if (livenessScore.score < settingsPrefManager.getLivenessThreshold()) {
//                    Log.d("Score", "liveness............." + livenessScore.score + "......." + settingsPrefManager.getLivenessThreshold());
//                    isLivenessFailed = true;
//                    String livenessMsg = getLivenessFingerName(livenessScore);
//                    failures.add(livenessMsg);
//                    // No break - collect ALL failures
//                }
//            }
//        }
//
//        // YOUR EXACT QUALITY LOGIC
//        if (settingsPrefManager.isGetQualityEnabled() && result.fingers != null && !result.fingers.isEmpty()) {
//            for (Finger finger : result.fingers) {
//                float qualityThreshold = settingsPrefManager.getQualityThreshold();
//
//                if (finger.pos == 5 || finger.pos == 10) {
//                    qualityThreshold = settingsPrefManager.getLittleFingerThreshold();
//                }
//                if (finger.pos == 4 || finger.pos == 9) {
//                    qualityThreshold = settingsPrefManager.getRingFingerThreshold();
//                }
//
//                if (finger.quality < qualityThreshold) {
//                    Log.d("Score", "quality............." + finger.quality + "......." + qualityThreshold);
//                    isQualityFailed = true;
//                    String qualityMsg = getQualityFingerName(finger.pos, finger.quality);
//                    failures.add(qualityMsg);
//                } else {
//                    Log.d("Score", "quality....success........." + finger.quality + "......." + qualityThreshold);
//                }
//            }
//        }
//
//        boolean isFailed = isLivenessFailed || isQualityFailed;
//        String details = isFailed ? String.join("\n", failures) : null;
//
//        return new QualityLivenessResult(isFailed, details);
//    }

    private QualityLivenessResult checkQualityLiveness(FingerCaptureResult result) {
        List<String> failures = new ArrayList<>();
        List<String> successes = new ArrayList<>();  // NEW: Success scores
        boolean isLivenessFailed = false;
        boolean isQualityFailed = false;

        // LIVELINESS - Failures + Successes
        if (settingsPrefManager.isLivenessEnabled() && result.livenessScores != null) {
            for (LivenessScore livenessScore : result.livenessScores) {
                float threshold = settingsPrefManager.getLivenessThreshold();
                if (livenessScore.score < threshold) {
                    Log.d("Score", "liveness............." + livenessScore.score + "......." + threshold);
                    isLivenessFailed = true;
                    String livenessMsg = getLivenessFingerName(livenessScore);
                    failures.add( "x " + livenessMsg);
                } else {
                    // ADD SUCCESS ✓
                    String successMsg = "✓ " + getLivenessFingerName(livenessScore);
                    successes.add(successMsg);
                }
            }
        }

        // QUALITY - Failures + Successes
        if (settingsPrefManager.isGetQualityEnabled() && result.fingers != null && !result.fingers.isEmpty()) {
            for (Finger finger : result.fingers) {
                float qualityThreshold = settingsPrefManager.getQualityThreshold();
                if (finger.pos == 5 || finger.pos == 10) {
                    qualityThreshold = settingsPrefManager.getLittleFingerThreshold();
                }
                if (finger.pos == 4 || finger.pos == 9) {
                    qualityThreshold = settingsPrefManager.getRingFingerThreshold();
                }

                if (finger.quality < qualityThreshold) {
                    Log.d("Score", "quality............." + finger.quality + "......." + qualityThreshold);
                    isQualityFailed = true;
                    String qualityMsg = getQualityFingerName(finger.pos, finger.quality);
                    failures.add( "x " + qualityMsg + " (threshold: " + (int)qualityThreshold + ")");
                } else {
                    Log.d("Score", "quality....success........." + finger.quality + "......." + qualityThreshold);
                    // ADD SUCCESS ✓
                    String successMsg = "✓ " + getQualityFingerName(finger.pos, finger.quality) +
                            " (threshold: " + (int)qualityThreshold + ")";
                    successes.add(successMsg);
                }
            }
        }

        boolean isFailed = isLivenessFailed || isQualityFailed;
        String failureDetails = isFailed ? String.join("\n", failures) : null;
        String successDetails = String.join("\n", successes);  // All ✓ scores

        return new QualityLivenessResult(isFailed, failureDetails, successDetails);
    }

    // 3. Your Liveness NIST mapping
    private String getLivenessFingerName(LivenessScore livenessScore) {
        if (livenessScore.pos == NistPosCode.POS_CODE_PL_L_4F) {
            return "Left Slap Liveness score: " + String.format("%.2f", livenessScore.score);
        } else if (livenessScore.pos == NistPosCode.POS_CODE_PL_R_4F) {
            return "Right Slap Liveness score: " + String.format("%.2f", livenessScore.score);
        } else if (livenessScore.pos == NistPosCode.POS_CODE_L_THUMB) {
            return "Left Thumb Liveness score: " + String.format("%.2f", livenessScore.score);
        } else if (livenessScore.pos == NistPosCode.POS_CODE_R_THUMB) {
            return "Right Thumb Liveness score: " + String.format("%.2f", livenessScore.score);
        } else if (livenessScore.pos == NistPosCode.POS_CODE_L_INDEX_F) {
            return "Left Index Liveness score: " + String.format("%.2f", livenessScore.score);
        } else if (livenessScore.pos == NistPosCode.POS_CODE_R_INDEX_F) {
            return "Right Index Liveness score: " + String.format("%.2f", livenessScore.score);
        } else if (livenessScore.pos == NistPosCode.POS_CODE_L_INDEX_MIDDLE) {
            return "Left Index Middle Liveness score: " + String.format("%.2f", livenessScore.score);
        } else if (livenessScore.pos == NistPosCode.POS_CODE_R_INDEX_MIDDLE) {
            return "Right Index Middle Liveness score: " + String.format("%.2f", livenessScore.score);
        } else {
            return "Pos: " + livenessScore.pos + " Liveness score: " + String.format("%.2f", livenessScore.score);
        }
    }

    // 4. Quality NIST mapping with score
    private String getQualityFingerName(int pos, int qualityScore) {
        switch (pos) {
            case NistPosCode.POS_CODE_R_THUMB:
                return "Right Thumb quality: " + qualityScore;
            case NistPosCode.POS_CODE_R_INDEX_F:
                return "Right Index quality: " + qualityScore;
            case NistPosCode.POS_CODE_R_MIDDLE_F:
                return "Right Middle quality: " + qualityScore;
            case NistPosCode.POS_CODE_R_RING_F:
                return "Right Ring quality: " + qualityScore;
            case NistPosCode.POS_CODE_R_LITTLE_F:
                return "Right Little quality: " + qualityScore;
            case NistPosCode.POS_CODE_L_THUMB:
                return "Left Thumb quality: " + qualityScore;
            case NistPosCode.POS_CODE_L_INDEX_F:
                return "Left Index quality: " + qualityScore;
            case NistPosCode.POS_CODE_L_MIDDLE_F:
                return "Left Middle quality: " + qualityScore;
            case NistPosCode.POS_CODE_L_RING_F:
                return "Left Ring quality: " + qualityScore;
            case NistPosCode.POS_CODE_L_LITTLE_F:
                return "Left Little quality: " + qualityScore;
            case NistPosCode.POS_CODE_PL_R_4F:
                return "Right Slap quality: " + qualityScore;
            case NistPosCode.POS_CODE_PL_L_4F:
                return "Left Slap quality: " + qualityScore;
            default:
                return "Finger pos " + pos + " quality: " + qualityScore;
        }
    }

    @Override
    public void onSuccess(FingerCaptureResult result) {

//        if (isQualityOrLivenessFailed(result)) {
//            Toast.makeText(FingerCaptureActivity.this, "Something went wrong. Try again!", Toast.LENGTH_LONG).show();
//            return;
//        }
//        QualityLivenessResult checkResult = checkQualityLiveness(result);
//        if (checkResult.isFailed) {  // Same as your original boolean
//            new AlertDialog.Builder(FingerCaptureActivity.this)
//                    .setTitle("Capture Failed")
//                    .setMessage(checkResult.failureDetails)  // Shows finger names + scores
//                    .setPositiveButton("Retry", null)
//                    .setCancelable(false)
//                    .show();
//            return;
//        }
//        QualityLivenessResult checkResult = checkQualityLiveness(result);
//        if (checkResult.isFailed) {
////            TextView messageView = new TextView(FingerCaptureActivity.this);
////            messageView.setText(checkResult.failureDetails);
////            messageView.setTextColor(Color.RED);
////            messageView.setPadding(50, 40, 50, 40);
////            messageView.setTextSize(16);
////
////            new AlertDialog.Builder(FingerCaptureActivity.this)
////                    .setTitle("Capture Failed")
////                    .setView(messageView)
////                    .setPositiveButton("Retry", null)
////                    .setCancelable(false)
////                    .show();
//            Intent intent = new Intent(FingerCaptureActivity.this, ResultScreen.class);
//            intent.putExtra("checkResult",checkResult);
//            startActivity(intent);
//
//            return;
//        }

        if (settingsPrefManager.isGetFingerReverseEnabled() && result.reversefingerScores!=SE_OK){
    Toast.makeText(FingerCaptureActivity.this, "Something went wrong. Try again!", Toast.LENGTH_LONG).show();
    return;
}
        Result captureResult = new Result();
        captureResult.livenessScores = result.livenessScores;


        m_rootDirectory = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + File.separator + System.currentTimeMillis();

//        File dir = new File(m_rootDirectory);
//        if (!dir.exists()) dir.mkdirs();

//        if (result.slapImages != null && !result.slapImages.isEmpty()) {
//
//            new Thread(() -> {
//
//                for (Slap slap : result.slapImages) {
//
//                    String extn = ".wsq";
//
//                    if (slap.imageType == ImageType.IMAGE_TYPE_BMP) {
//                        extn = ".bmp";
//                    } else if (slap.imageType == ImageType.IMAGE_TYPE_PNG) {
//                        extn = ".png";
//                    }
//
//                    String fingerImgPath = m_rootDirectory + File.separator + "slap_" + slap.pos + extn;
//
////                    writeToFile(slap.image, fingerImgPath);
//                }
//
//
//            }).start();
//        }


        if (result.fingers != null && !result.fingers.isEmpty()) {


            captureResult.fingers = saveFingerImages(result.fingers);


        }


        Intent intent = new Intent(FingerCaptureActivity.this, ResultScreen.class);
        intent.putExtra("result", captureResult);
        startActivity(intent);


    }

    @Override
    public void onTimedout() {

        Toast.makeText(FingerCaptureActivity.this, "capture timedout ", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onFailure(String errorMessage) {

        Toast.makeText(FingerCaptureActivity.this, "error " + errorMessage, Toast.LENGTH_LONG).show();

        Log.e("TAG", errorMessage);
    }


    @Override
    public void onCancelled() {
        Toast.makeText(FingerCaptureActivity.this, "User cancelled ", Toast.LENGTH_LONG).show();

    }


    private boolean hasAllPermissionsGranted() {

        for (String permission : APP_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

                boolean isAllPermissionsGranted = true;

                for (Boolean isGranted : result.values()) {

                    if (Boolean.FALSE.equals(isGranted)) {
                        isAllPermissionsGranted = false;
                        break;

                    }
                }

                if (isAllPermissionsGranted) {
                    startFingerCapture();
                }


            });


    private ArrayList<FingerData> saveFingerImages(ArrayList<Finger> fingers) {


        ArrayList<FingerData> list = new ArrayList<>();

        int threadCount = fingers.size();
        SaveImageThread[] saveImageThreads = null;

        try {
            saveImageThreads = new SaveImageThread[threadCount];
            ArrayList<Future<Runnable>> futures = new ArrayList<>();
            int threadIndex;
            for (threadIndex = 0; threadIndex < threadCount; threadIndex++) {


                Finger finger = fingers.get(threadIndex);

                Log.d("TAG", "finger pos " + finger.pos + " type " + finger.primaryImageType + " prop quality " + finger.quality + " nist2 quality " + finger.nist2Quality + " nist quality " + finger.nistQuality + " image size " + (finger.primaryImage == null ? "null" : finger.primaryImage.length));

                saveImageThreads[threadIndex] = new SaveImageThread(finger);

                Future future = m_service.submit(saveImageThreads[threadIndex]);
                futures.add(future);
            }

            for (Future<Runnable> future : futures) {
                future.get();
            }

            for (threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                SaveImageThread thread = saveImageThreads[threadIndex];

                FingerData fingerData = thread.getFingerData();
                list.add(fingerData);

            }

        } catch (Exception ignore) {
        }

        return list;
    }


    public static class SaveImageThread implements Runnable {


        private final Finger finger;

        private FingerData fingerData;

        public FingerData getFingerData() {
            return fingerData;
        }

        public SaveImageThread(Finger finger) {
            this.finger = finger;
        }


        @Override
        public void run() {


            fingerData = new FingerData(finger);

            String extn = ".wsq";

            if (finger.primaryImageType == ImageType.IMAGE_TYPE_BMP) {
                extn = ".bmp";
            } else if (finger.primaryImageType == ImageType.IMAGE_TYPE_PNG) {
                extn = ".png";
            }


            String fingerImgPath = m_rootDirectory + File.separator + "prim_finger_" + finger.pos + extn;

//            File rootDir = this.getExternalFilesDir(null);
//            File sessionDir = new File(rootDir, String.valueOf(System.currentTimeMillis()));
//            File file = new File(sessionDir, "prim_finger_" + finger.pos + extn);

            writeToFile(finger.primaryImage, fingerImgPath);

            fingerData.primaryImagePath = fingerImgPath;


            if (finger.displayImage != null && finger.displayImage.length > 0) {

                String displImgextn = ".wsq";

                if (finger.displayImageType == ImageType.IMAGE_TYPE_BMP) {
                    displImgextn = ".bmp";
                } else if (finger.displayImageType == ImageType.IMAGE_TYPE_PNG) {
                    displImgextn = ".png";
                }


                String displayfingerImgPath = m_rootDirectory + File.separator + "disp_finger_" + finger.pos + displImgextn;

                writeToFile(finger.displayImage, displayfingerImgPath);

                fingerData.displayImagePath = displayfingerImgPath;


            }
        }
    }


    private ProgressDialog dialog;

    private void showProgress(boolean isShow) {

        runOnUiThread(() -> {

            if (isShow) {

                if (dialog == null) {
                    dialog = new ProgressDialog(FingerCaptureActivity.this);
                    dialog.setMessage(getString(R.string.please_wait));
                    dialog.setCancelable(false);

                    dialog.show();
                }

            } else {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }


        });

    }

    private void showToast(String message) {
        Toast.makeText(FingerCaptureActivity.this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        m_lightSensorHelper.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_lightSensorHelper.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    private boolean isQualityOrLivenessFailed(FingerCaptureResult result){


        boolean isLivenessFailed = false;
        boolean isQualityFailed = false;


        if (settingsPrefManager.isLivenessEnabled() && result.livenessScores != null) {


            for (LivenessScore livenessScore : result.livenessScores) {
//                if (livenessScore.score < 0.6f) {
                if (livenessScore.score < settingsPrefManager.getLivenessThreshold()) {
                    Log.d("Score","liveness............."+livenessScore.score +"......."+settingsPrefManager.getLivenessThreshold());

                    isLivenessFailed = true;
                    break;
                }

            }
        }


        if (settingsPrefManager.isGetQualityEnabled() && result.fingers != null && !result.fingers.isEmpty()) {


            for (Finger finger : result.fingers) {

//                float qualityThreshold = 35f;
                float qualityThreshold = settingsPrefManager.getQualityThreshold();

                if (finger.pos == 5 || finger.pos == 10) {
//                    qualityThreshold = 25;
                    qualityThreshold = settingsPrefManager.getLittleFingerThreshold();

                }
                if (finger.pos == 4 || finger.pos == 9) {

                    qualityThreshold = settingsPrefManager.getRingFingerThreshold();

                }
                if (finger.quality < qualityThreshold) {
                    Log.d("Score","quality............."+finger.quality +"......."+qualityThreshold);
                    isQualityFailed = true;
                    break;
                }else {
                    Log.d("Score","quality....success........."+finger.quality +"......."+qualityThreshold);

                }


            }

        }


        return isLivenessFailed || isQualityFailed;

    }
    Dialog previewDialog1;

    private void showPreviewDialog(ArrayList<Integer> missingfingerId) {
        try {
            previewDialog1 = new Dialog(this, android.R.style.Theme_Light);
            previewDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View view = LayoutInflater.from(this).inflate(R.layout.preview_dialog_layout, null);
            previewDialog1.setContentView(view);
            WindowManager.LayoutParams layoutParams = previewDialog1.getWindow().getAttributes();
            previewDialog1.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            previewDialog1.getWindow().setAttributes(layoutParams);
            previewDialog1.setCancelable(true);

            CheckBox preview_check = view.findViewById(R.id.preview_check);

            Button Next = view.findViewById(R.id.next_bt);

            TextView Tool_text = view.findViewById(R.id.Tool_text);
            Button back_img = view.findViewById(R.id.back_img);
//            if (type.equalsIgnoreCase("enroll")) {

                Tool_text.setText("Capture Fingers");

//            } else if (type.equalsIgnoreCase("verify")) {
//                Tool_text.setText(getResources().getString(R.string.label_verify));
//            }
            back_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAGBACK", "Clicked");
                    if (previewDialog1 != null && previewDialog1.isShowing()) {
                        previewDialog1.dismiss();
                    }

                }
            });


            preview_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        settingsPrefManager.setIs_Dialogue_PreviewEnabled(true);
                        Log.d("TAGCHECK", "CHECKED");
                    } else {
                        settingsPrefManager.setIs_Dialogue_PreviewEnabled(false);
                        Log.d("TAGCHECK", "UN_CHECKED");
                    }

                }
            });
            Next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        previewDialog1.cancel();
                    capture(missingfingerId);
                }
            });

            previewDialog1.show();
        } catch (Exception e) {

        }
    }

    public void getmissingfingerdialogue(Context context, String selectedSlap){
        // In your Activity or Fragment
        View dialogView = LayoutInflater.from(context).inflate(R.layout.missing_fingers, null);

        CheckBox cbIndex = dialogView.findViewById(R.id.checkbox_index);
        CheckBox cbMiddle = dialogView.findViewById(R.id.checkbox_middle);
        CheckBox cbRing = dialogView.findViewById(R.id.checkbox_ring);
        CheckBox cbLittle = dialogView.findViewById(R.id.checkbox_little);
        CheckBox cbNone = dialogView.findViewById(R.id.checkbox_none);

        CheckBox[] all = {cbIndex, cbMiddle, cbRing, cbLittle, cbNone};

//// Only allow one selection at a time (like a RadioGroup with CheckBoxes)
//        CompoundButton.OnCheckedChangeListener singleSelectListener = (buttonView, isChecked) -> {
//            if (isChecked) {
//                for (CheckBox c : all) {
//                    if (c != buttonView) c.setChecked(false);
//                }
//            } else {
//                // Prevent having none of the checkboxes checked, always have one checked!
//                boolean atLeastOne = false;
//                for (CheckBox c : all) if (c.isChecked()) atLeastOne = true;
//                if (!atLeastOne) ((CheckBox)buttonView).setChecked(true);
//            }
//        };
//// Attach listener to each checkbox
//        for (CheckBox cb : all) cb.setOnCheckedChangeListener(singleSelectListener);

        // Remove your current singleSelectListener

// Add new listeners
        CompoundButton.OnCheckedChangeListener multiSelectListener = (buttonView, isChecked) -> {
            // If None is checked, uncheck all others
            if (buttonView == cbNone && isChecked) {
                cbIndex.setChecked(false);
                cbMiddle.setChecked(false);
                cbRing.setChecked(false);
                cbLittle.setChecked(false);
                return;
            }

            // If any of first 4 options is checked, uncheck None
            if ((buttonView == cbIndex || buttonView == cbMiddle ||
                    buttonView == cbRing || buttonView == cbLittle) && isChecked) {
                cbNone.setChecked(false);

                // Enforce max 3 selections for first 4 checkboxes
                int count = 0;
                if (cbIndex.isChecked()) count++;
                if (cbMiddle.isChecked()) count++;
                if (cbRing.isChecked()) count++;
                if (cbLittle.isChecked()) count++;

                // If more than 3 selected, disallow this last check
                if (count > 3) {
                    buttonView.setChecked(false);
                    Toast.makeText(context, "Select up to 3 fingers only", Toast.LENGTH_SHORT).show();
                }
            }
        };
        for (CheckBox cb : all) {
            cb.setOnCheckedChangeListener(multiSelectListener);
        }

        AlertDialog dlg = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialogView.findViewById(R.id.next_bt).setOnClickListener(v -> {
//            String result = null;
//            if (cbIndex.isChecked()) result = "Index Finger";
//            else if (cbMiddle.isChecked()) result = "Middle Finger";
//            else if (cbRing.isChecked()) result = "Ring Finger";
//            else if (cbLittle.isChecked()) result = "Little Finger";
//            else if (cbNone.isChecked()) result = "None";
//            // ... use result as needed
//
//
//            if (!settingsPrefManager.is_Dialogue_PreviewEnabled()) {
//                Log.d("enrollactivity", "in !preferencesHelper.is_Dialogue_PreviewEnabled()");
//                showPreviewDialog();
//            } else {
//                Log.d("enrollactivity", "in else");
//                capture();
//
//            }


            ArrayList<Integer> missingFingers = new ArrayList<>();

            boolean isLeftSlap = binding.chkBoxLeftSlap.isChecked();
            boolean isRightSlap = binding.chkBoxRightSlap.isChecked();

            if (cbIndex.isChecked()) {
                int fingerId = isLeftSlap ? 7 : (isRightSlap ? 2 : -1);
                if (fingerId != -1) missingFingers.add(fingerId);
            }
            if (cbMiddle.isChecked()) {
                int fingerId = isLeftSlap ? 8 : (isRightSlap ? 3 : -1);
                if (fingerId != -1) missingFingers.add(fingerId);
            }

            if (cbRing.isChecked()) {
                int fingerId = isLeftSlap ? 9 : (isRightSlap ? 4 : -1);
                if (fingerId != -1) missingFingers.add(fingerId);
            }
            if (cbLittle.isChecked()) {
                int fingerId = isLeftSlap ? 10 : (isRightSlap ? 5 : -1);
                if (fingerId != -1) missingFingers.add(fingerId);
            }

            if (cbNone.isChecked()) {
                missingFingers.clear(); // None overrides other selections
            }


// Now missingFingers contains all selected fingers by their IDs

            for (Integer fingerId : missingFingers) {
                // Use fingerId here
                Log.d("missingfingers","Missing finger ID: " + fingerId);
            }
            if (!settingsPrefManager.is_Dialogue_PreviewEnabled()) {
                showPreviewDialog(missingFingers);
            } else {
                capture(missingFingers);
            }


            dlg.dismiss();
        });
        dlg.show();

    }
}