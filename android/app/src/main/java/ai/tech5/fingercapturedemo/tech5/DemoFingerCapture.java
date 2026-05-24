package ai.tech5.fingercapturedemo.tech5;

import android.app.Activity;

import ai.tech5.fingercapturedemo.BuildConfig;
import ai.tech5.fingercapturedemo.LightSensorHelper;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import ai.tech5.finger.utils.CaptureMode;
import ai.tech5.finger.utils.CaptureSpeed;
import ai.tech5.finger.utils.ImageConfiguration;
import ai.tech5.finger.utils.ImageType;
import ai.tech5.finger.utils.SegmentationMode;
import ai.tech5.finger.utils.T5FingerCaptureController;

public class DemoFingerCapture {

    public static T5FingerCaptureController configureController(
            Activity activity,
            SettingsPrefManager settings,
            LightSensorHelper lightSensorHelper,
            String username,
            SegmentationMode segmentationMode,
            ArrayList<Integer> missingFingerIds) {

        T5FingerCaptureController controller = T5FingerCaptureController.getInstance();

        controller.setZoomFactor(settings.getZoomRatio());

        String license = BuildConfig.TECH5_LICENSE.isEmpty() ? "" : BuildConfig.TECH5_LICENSE;
        controller.setLicense(license);

        controller.showElipses(settings.isShowEllipsesEnabled());
        controller.setLivenessCheck(settings.isLivenessEnabled());
        controller.setIsGetQuality(settings.isGetQualityEnabled());
        controller.setIsGetNist2Quality(settings.isGetNfiq2QualityEnabled());
        controller.setDetectorThreshold(0.9f);
        controller.setUsername(username);

        LinkedHashSet<SegmentationMode> segmentationModeSet = new LinkedHashSet<>();
        segmentationModeSet.add(segmentationMode);
        controller.setSegmentationModes(segmentationModeSet);

        CaptureMode captureMode = CaptureMode.CAPTURE_MODE_SELF;
        int modeId = settings.getCaptureModeId();
        if (modeId == 0) {
            captureMode = CaptureMode.CAPTURE_MODE_SELF;
        } else if (modeId == 1) {
            captureMode = CaptureMode.CAPTURE_MODE_OPERATOR;
        }
        controller.setCaptureMode(captureMode);
        controller.setTitle("Finger Capture");
        controller.setShowBackButton(false);

        if (missingFingerIds != null && !missingFingerIds.isEmpty()) {
            controller.setMissingFingers(missingFingerIds);
        }

        CaptureSpeed captureSpeed;
        int speedId = settings.getCaptureSpeedId();
        switch (speedId) {
            case 0:
                captureSpeed = CaptureSpeed.CAPTURE_SPEED_VERYSLOW;
                break;
            case 1:
                captureSpeed = CaptureSpeed.CAPTURE_SPEED_SLOW;
                break;
            case 2:
                captureSpeed = CaptureSpeed.CAPTURE_SPEED_MEDIUM;
                break;
            case 3:
                captureSpeed = CaptureSpeed.CAPTURE_SPEED_FAST;
                break;
            case 4:
                captureSpeed = CaptureSpeed.CAPTURE_SPEED_VERYFAST;
                break;
            default:
                captureSpeed = CaptureSpeed.CAPTURE_SPEED_MEDIUM;
                break;
        }
        controller.setCaptureSpeed(captureSpeed);
        controller.setPropDenoise(settings.isProprietaryDenoiseEnabled());
        controller.setCleanFingerPrints(settings.isCleanFingerprintsEnabled());

        float luxOutsideThreshold = 200.0f;
        float luxCurrentValue = lightSensorHelper.getCurrentLightValue();
        boolean outsideCapture = luxCurrentValue > luxOutsideThreshold || luxCurrentValue < 0.0f;
        controller.setOutsideCaptureFlag(outsideCapture);

        controller.setSegmentedFingerImagesConfig(buildSegmentedImageConfiguration());

        ImageConfiguration slapConfig = new ImageConfiguration();
        slapConfig.setPrimaryImageType(ImageType.IMAGE_TYPE_BMP);
        slapConfig.setIsCropImage(false);
        controller.setSlapImagesConfig(slapConfig);

        controller.setTimeoutInSecs(60);
        controller.setsavesdklogs(settings.isSaveSdkLogEnabled());
        controller.setSavefingerprints(true);
        controller.setReversefingerprints(settings.isGetFingerReverseEnabled());

        return controller;
    }

    public static ImageConfiguration buildSegmentedImageConfiguration() {
        ImageConfiguration config = new ImageConfiguration();
        config.setPrimaryImageType(ImageType.IMAGE_TYPE_PNG);
        config.setRequireDisplayImage(false);
        config.setDisplayImageType(ImageType.IMAGE_TYPE_BMP);
        config.setCompressionRatio(10);
        config.setIsCropImage(false);
        config.setCroppedImageWidth(512);
        config.setCroppedImageHeight(512);
        config.setPaddingColor(255);
        return config;
    }
}
