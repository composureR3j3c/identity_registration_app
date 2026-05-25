package com.boabeta.idregtes.tech5;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsPrefManager {

    private static final String PREF_NAME = "SettingsPrefs";

    private static final String KEY_ZOOM_RATIO = "zoom_ratio";
    private static final String KEY_LIVENESS = "liveness";
    private static final String KEY_SHOW_ELLIPSES = "showEllipses";
    private static final String KEY_SAVE_SDK_LOG = "saveSdkLog";
    private static final String KEY_PROPRIETARY_DENOISE = "proprietaryDenoise";
    private static final String KEY_CLEAN_FINGERPRINTS = "cleanFingerprints";
    private static final String KEY_CAPTURE_SPEED_ID = "captureSpeedId";
    private static final String KEY_GET_QUALITY = "get_quality";
    private static final String KEY_GET_NFIQ2_QUALITY = "get_nfiq2_quality";
    private static final String KEY_CAPTURE_MODE_ID = "capture_mode_id";
    private static final String KEY_GET_FINGER_REVERSE = "finger_reverse";
    private static final String KEY_LIVENESS_THRESHOLD = "liveness_threshold";
    private static final String KEY_QUALITY_THRESHOLD = "quality_threshold";
    private static final String KEY_LITTLE_FINGER_THRESHOLD = "little_finger_threshold";
    private static final String KEY_RING_FINGER_THRESHOLD = "ring_finger_threshold";
    private static final String IS_PREVIEW_DONE = "IS_Preview_Done";

    private static final int DEFAULT_CAPTURE_SPEED_ID = 2;
    private static final boolean DEFAULT_GET_QUALITY = true;
    private static final boolean DEFAULT_GET_NFIQ2_QUALITY = false;
    private static final int DEFAULT_CAPTURE_MODE_ID = 0;
    private static final float DEFAULT_LIVENESS_THRESHOLD = 0.50f;
    private static final float DEFAULT_QUALITY_THRESHOLD = 30f;
    private static final float DEFAULT_LITTLE_FINGER_THRESHOLD = 15f;
    private static final float DEFAULT_RING_FINGER_THRESHOLD = 25f;
    private static final boolean DEFAULT_IS_PREVIEW_DONE = false;

    private SharedPreferences sharedPref;

    public SettingsPrefManager(Context context) {
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public float getZoomRatio() {
        return sharedPref.getFloat(KEY_ZOOM_RATIO, 1.5f);
    }

    public boolean isLivenessEnabled() {
        return sharedPref.getBoolean(KEY_LIVENESS, true);
    }

    public boolean isShowEllipsesEnabled() {
        return sharedPref.getBoolean(KEY_SHOW_ELLIPSES, true);
    }

    public boolean isSaveSdkLogEnabled() {
        return sharedPref.getBoolean(KEY_SAVE_SDK_LOG, false);
    }

    public boolean isProprietaryDenoiseEnabled() {
        return sharedPref.getBoolean(KEY_PROPRIETARY_DENOISE, true);
    }

    public boolean isCleanFingerprintsEnabled() {
        return sharedPref.getBoolean(KEY_CLEAN_FINGERPRINTS, false);
    }

    public int getCaptureSpeedId() {
        return sharedPref.getInt(KEY_CAPTURE_SPEED_ID, DEFAULT_CAPTURE_SPEED_ID);
    }

    public boolean isGetQualityEnabled() {
        return sharedPref.getBoolean(KEY_GET_QUALITY, DEFAULT_GET_QUALITY);
    }

    public boolean isGetNfiq2QualityEnabled() {
        return sharedPref.getBoolean(KEY_GET_NFIQ2_QUALITY, DEFAULT_GET_NFIQ2_QUALITY);
    }

    public int getCaptureModeId() {
        return sharedPref.getInt(KEY_CAPTURE_MODE_ID, DEFAULT_CAPTURE_MODE_ID);
    }

    public boolean isGetFingerReverseEnabled() {
        return sharedPref.getBoolean(KEY_GET_FINGER_REVERSE, false);
    }

    public float getLivenessThreshold() {
        return sharedPref.getFloat(KEY_LIVENESS_THRESHOLD, DEFAULT_LIVENESS_THRESHOLD);
    }

    public float getQualityThreshold() {
        return sharedPref.getFloat(KEY_QUALITY_THRESHOLD, DEFAULT_QUALITY_THRESHOLD);
    }

    public float getLittleFingerThreshold() {
        return sharedPref.getFloat(KEY_LITTLE_FINGER_THRESHOLD, DEFAULT_LITTLE_FINGER_THRESHOLD);
    }

    public float getRingFingerThreshold() {
        return sharedPref.getFloat(KEY_RING_FINGER_THRESHOLD, DEFAULT_RING_FINGER_THRESHOLD);
    }

    public boolean isDialoguePreviewEnabled() {
        return sharedPref.getBoolean(IS_PREVIEW_DONE, DEFAULT_IS_PREVIEW_DONE);
    }

    public void setDialoguePreviewEnabled(boolean enabled) {
        sharedPref.edit().putBoolean(IS_PREVIEW_DONE, enabled).apply();
    }
}
