package com.tech5.fingercapture;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class SettingsActivity extends AppCompatActivity {
    private EditText livenessThresholdEditText, qualityThresholdEditText;
    private EditText littlefingerThresholdEditText,ringfingerThresholdEditText;

    private CheckBox getQualityCheckBox, getNfiq2QualityCheckBox,fingerreversedCheckbox;
    private RadioGroup captureModeRadioGroup;

    private CheckBox livenessCheckBox, showEllipsesCheckBox,
             proprietaryDenoiseCheckBox,
            cleanFingerprintsCheckBox,saveSdkLogCheckBox;
//            indexCheckBox, middleCheckBox, ringCheckBox, littleCheckBox ,createTemplatesCheckBox ,
//            orientationCheckBox, ,saveFingerprintsCheckBox;

    private RadioGroup captureSpeedRadioGroup;
//    private EditText detectorThresholdEditText;
    private Button submitButton;

    private SettingsPrefManager prefManager;
    private String[] zoomValues;

    private Spinner spinnerZoomRatios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

////        MyApp app = (MyApp) getApplication();
//        Application app = getApplication();
//
//        prefManager = new ViewModelProvider((ViewModelStoreOwner) app,
//                ViewModelProvider.AndroidViewModelFactory.getInstance(app))
//                .get(SettingsPrefManager.class);
        prefManager=new SettingsPrefManager(SettingsActivity.this);
        spinnerZoomRatios=findViewById(R.id.spinner_zoom_ratios);

        getQualityCheckBox = findViewById(R.id.get_quality_checkbox);
        getNfiq2QualityCheckBox = findViewById(R.id.get_nfiq2_quality);
        fingerreversedCheckbox=findViewById(R.id.get_finger_reversed);
        captureModeRadioGroup = findViewById(R.id.rad_group_cap_mode);
        livenessThresholdEditText = findViewById(R.id.livenessThresholdEditText);
        qualityThresholdEditText = findViewById(R.id.qualityThresholdEditText);
        littlefingerThresholdEditText = findViewById(R.id.littlefingerThresholdEditText);
        ringfingerThresholdEditText = findViewById(R.id.ringfingerThresholdEditText);

        livenessCheckBox = findViewById(R.id.livenessCheckBox);
        showEllipsesCheckBox = findViewById(R.id.showEllipsesCheckBox);
//        createTemplatesCheckBox = findViewById(R.id.createTemplatesCheckBox);
//        orientationCheckBox = findViewById(R.id.orientationCheckBox);
        saveSdkLogCheckBox = findViewById(R.id.saveSdkLogCheckBox);
        proprietaryDenoiseCheckBox = findViewById(R.id.proprietaryDenoiseCheckBox);
        cleanFingerprintsCheckBox = findViewById(R.id.cleanFingerprintsCheckBox);
//        saveFingerprintsCheckBox = findViewById(R.id.saveFingerprintsCheckBox);

//        indexCheckBox = findViewById(R.id.indexCheckBox);
//        middleCheckBox = findViewById(R.id.middleCheckBox);
//        ringCheckBox = findViewById(R.id.ringCheckBox);
//        littleCheckBox = findViewById(R.id.littleCheckBox);

        captureSpeedRadioGroup = findViewById(R.id.captureSpeedRadioGroup);
//        detectorThresholdEditText = findViewById(R.id.detectorThresholdEditText);

        submitButton = findViewById(R.id.submitButton);

        zoomValues = getResources().getStringArray(R.array.zoom_ratios_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                SettingsActivity.this, // get the Context from the fragment
                android.R.layout.simple_spinner_item,
                zoomValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZoomRatios.setAdapter(adapter);


        // Set spinner selection based on saved zoom ratio
        float savedZoom = prefManager.getZoomRatio();
        int selectedIndex = 0;
        for (int i = 0; i < zoomValues.length; i++) {
            if (Float.parseFloat(zoomValues[i]) == savedZoom) {
                selectedIndex = i;
                break;
            }
        }
        spinnerZoomRatios.setSelection(selectedIndex);

        loadDataFromPrefs();

        submitButton.setOnClickListener(v -> saveDataToPrefs());
    }

    private void loadDataFromPrefs() {livenessThresholdEditText.setText(String.valueOf(prefManager.getLivenessThreshold()));
        qualityThresholdEditText.setText(String.valueOf(prefManager.getQualityThreshold()));
        littlefingerThresholdEditText.setText(String.valueOf(prefManager.getLittleFingerThreshold()));
        ringfingerThresholdEditText.setText(String.valueOf(prefManager.getRingFingerThreshold()));

        //Toast.makeText(SettingsActivity.this,"liveness"+prefManager.isLivenessEnabled(),Toast.LENGTH_SHORT).show();
//        Toast.makeText(SettingsActivity.this,"denoise"+prefManager.isProprietaryDenoiseEnabled(),Toast.LENGTH_SHORT).show();

        livenessCheckBox.setChecked(prefManager.isLivenessEnabled());
        showEllipsesCheckBox.setChecked(prefManager.isShowEllipsesEnabled());
//        createTemplatesCheckBox.setChecked(prefManager.isCreateTemplatesEnabled());
//        orientationCheckBox.setChecked(prefManager.isOrientationEnabled());
        saveSdkLogCheckBox.setChecked(prefManager.isSaveSdkLogEnabled());
        proprietaryDenoiseCheckBox.setChecked(prefManager.isProprietaryDenoiseEnabled());
        cleanFingerprintsCheckBox.setChecked(prefManager.isCleanFingerprintsEnabled());
//        saveFingerprintsCheckBox.setChecked(prefManager.isSaveFingerprintsEnabled());

//        indexCheckBox.setChecked(prefManager.isIndexFingerChecked());
//        middleCheckBox.setChecked(prefManager.isMiddleFingerChecked());
//        ringCheckBox.setChecked(prefManager.isRingFingerChecked());
//        littleCheckBox.setChecked(prefManager.isLittleFingerChecked());

        int speedId = prefManager.getCaptureSpeedId();

        if (speedId != -1) {
//            Log.d("...........",".....speedid......."+speedId);
//            captureSpeedRadioGroup.check(speedId);

// SET value from preference to RadioButton



                if (speedId == 0) {
                    captureSpeedRadioGroup.check(R.id.veryslowSpeedRadioButton);

                } else if (speedId == 1) {
                    captureSpeedRadioGroup.check(R.id.slowSpeedRadioButton);

                } else if (speedId == 2) {
                    captureSpeedRadioGroup.check(R.id.mediumSpeedRadioButton);

                } else if (speedId == 3) {
                    captureSpeedRadioGroup.check(R.id.fastSpeedRadioButton);

                } else if (speedId == 4) {
                    captureSpeedRadioGroup.check(R.id.veryfastSpeedRadioButton);
                }
            }


//        float threshold = prefManager.getDetectorThreshold();
//        detectorThresholdEditText.setText(threshold == 0 ? "" : String.valueOf(threshold));


        getQualityCheckBox.setChecked(prefManager.isGetQualityEnabled());
        getNfiq2QualityCheckBox.setChecked(prefManager.isGetNfiq2QualityEnabled());


        fingerreversedCheckbox.setChecked(prefManager.isGetFingerReverseEnabled());

        int captureModeId = prefManager.getCaptureModeId();
        if (captureModeId != -1) {
//            Log.d("...........","............1"+captureModeId);
//
//            captureModeRadioGroup.check(captureModeId);
//        }


            if (captureModeId == 0) {
                captureModeRadioGroup.check(R.id.rad_btn_cap_mode_self);
            } else if (captureModeId == 1) {
                captureModeRadioGroup.check(R.id.rad_btn_cap_mode_oprtr);
            }
        }
    }

    private void saveDataToPrefs() {
        prefManager.setGetQualityEnabled(getQualityCheckBox.isChecked());
        prefManager.setGetNfiq2QualityEnabled(getNfiq2QualityCheckBox.isChecked());
        prefManager.setGetFingerReverseEnabled(fingerreversedCheckbox.isChecked());

        int selectedCaptureModeId = captureModeRadioGroup.getCheckedRadioButtonId();
        if (selectedCaptureModeId != -1) {
//            Log.d("...........","............2"+selectedCaptureModeId);
//
//            prefManager.setCaptureModeId(selectedCaptureModeId);
//        }
            int captureMode = 0; // default
            if (selectedCaptureModeId == R.id.rad_btn_cap_mode_self) {
                captureMode = 0;
            } else if (selectedCaptureModeId == R.id.rad_btn_cap_mode_oprtr) {
                captureMode = 1;
            }
            prefManager.setCaptureModeId(captureMode);
        }
        prefManager.setLivenessEnabled(livenessCheckBox.isChecked());
        prefManager.setShowEllipsesEnabled(showEllipsesCheckBox.isChecked());
//        prefManager.setCreateTemplatesEnabled(createTemplatesCheckBox.isChecked());
//        prefManager.setOrientationEnabled(orientationCheckBox.isChecked());
        prefManager.setSaveSdkLogEnabled(saveSdkLogCheckBox.isChecked());
        prefManager.setProprietaryDenoiseEnabled(proprietaryDenoiseCheckBox.isChecked());
        prefManager.setCleanFingerprintsEnabled(cleanFingerprintsCheckBox.isChecked());
//        prefManager.setSaveFingerprintsEnabled(saveFingerprintsCheckBox.isChecked());

//        prefManager.setIndexFingerChecked(indexCheckBox.isChecked());
//        prefManager.setMiddleFingerChecked(middleCheckBox.isChecked());
//        prefManager.setRingFingerChecked(ringCheckBox.isChecked());
//        prefManager.setLittleFingerChecked(littleCheckBox.isChecked());

//        int SpeedMode = captureSpeedRadioGroup.getCheckedRadioButtonId();
//        if (SpeedMode != -1) {

            int selectedSpeedRadioId = captureSpeedRadioGroup.getCheckedRadioButtonId();

            int speedValue = 0;

            if (selectedSpeedRadioId == R.id.veryslowSpeedRadioButton) {

                speedValue = 0;

            } else if (selectedSpeedRadioId == R.id.slowSpeedRadioButton) {

                speedValue = 1;

            } else if (selectedSpeedRadioId == R.id.mediumSpeedRadioButton) {

                speedValue = 2;

            } else if (selectedSpeedRadioId == R.id.fastSpeedRadioButton) {

                speedValue = 3;

            } else if (selectedSpeedRadioId == R.id.veryfastSpeedRadioButton) {

                speedValue = 4;
            }

            prefManager.setCaptureSpeedId(speedValue);



//        }

//        String thresholdStr = detectorThresholdEditText.getText().toString().trim();
//        if (!TextUtils.isEmpty(thresholdStr)) {
//            try {
//                int thresholdVal = Integer.parseInt(thresholdStr);
//                prefManager.setDetectorThreshold(thresholdVal);
//            } catch (NumberFormatException e) {
//                Toast.makeText(this, "Invalid threshold value", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//        String thresholdStr = detectorThresholdEditText.getText().toString().trim();
//        if (TextUtils.isEmpty(thresholdStr)) {
//            Toast.makeText(this, "Threshold value is required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        try {
//            float thresholdVal = Float.parseFloat(thresholdStr);
//            prefManager.setDetectorThreshold(thresholdVal);
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Invalid threshold value", Toast.LENGTH_SHORT).show();
//            return;
//        }


        // Liveness threshold (0 to 1, inclusive)
        String livenessStr = livenessThresholdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(livenessStr)) {
            Toast.makeText(this, "Liveness threshold is required", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float livenessValue = Float.parseFloat(livenessStr);
            if (livenessValue < 0.0f || livenessValue > 1.0f) {
                Toast.makeText(this, "Liveness threshold must be between 0 and 1", Toast.LENGTH_SHORT).show();
                return;
            }
            prefManager.setLivenessThreshold(livenessValue);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid liveness threshold", Toast.LENGTH_SHORT).show();
            return;
        }

// Quality threshold (0 to 100, inclusive)
        String qualityStr = qualityThresholdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(qualityStr)) {
            Toast.makeText(this, "Quality threshold is required", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float qualityValue = Float.parseFloat(qualityStr);
            if (qualityValue < 0.0f || qualityValue > 100.0f) {
                Toast.makeText(this, "Quality threshold must be between 0 and 100", Toast.LENGTH_SHORT).show();
                return;
            }
            prefManager.setQualityThreshold(qualityValue);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quality threshold", Toast.LENGTH_SHORT).show();
            return;
        }

        String littleFingerStr = littlefingerThresholdEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(littleFingerStr)) {
            try {
                float littleFingerValue = Float.parseFloat(littleFingerStr);
                if (littleFingerValue < 0.0f || littleFingerValue > 100.0f) {
                    Toast.makeText(this, "Little finger threshold must be between 0 and 100", Toast.LENGTH_SHORT).show();
                    return;
                }
                prefManager.setLittleFingerThreshold(littleFingerValue);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid little finger threshold", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String ringFingerStr = ringfingerThresholdEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(ringFingerStr)) {
            try {
                float ringFingerValue = Float.parseFloat(ringFingerStr);
                if (ringFingerValue < 0.0f || ringFingerValue > 100.0f) {
                    Toast.makeText(this, "Ring finger threshold must be between 0 and 100", Toast.LENGTH_SHORT).show();
                    return;
                }
                prefManager.setRingFingerThreshold(ringFingerValue);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid ring finger threshold", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        int selectedPosition = spinnerZoomRatios.getSelectedItemPosition();
        float selectedZoom = Float.parseFloat(zoomValues[selectedPosition]);
        prefManager.setZoomRatio(selectedZoom);

        //        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this, FingerCaptureActivity.class);
        startActivity(intent);
        finish();
    }
}
