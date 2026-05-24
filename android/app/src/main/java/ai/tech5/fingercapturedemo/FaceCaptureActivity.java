package ai.tech5.fingercapturedemo;

import ai.tech5.pheonix.capture.controller.AirsnapFaceThresholds;
import ai.tech5.pheonix.capture.controller.FaceCaptureController;
import ai.tech5.pheonix.capture.controller.FaceCaptureListener;
import ai.tech5.pheonix.capture.controller.GlassDetection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.phoenixcapture.camerakit.FaceBox;

public class FaceCaptureActivity extends AppCompatActivity {

    private static final String TAG = "TECH5";

    private static final int PITCH_THRESHOLD = 15;
    private static final int YAW_THRESHOLD = 15;
    private static final int ROLL_THRESHOLD = 10;

    private static final double MASK_THRESHOLD = 0.5;
    private static final double ANY_GLASS_THRESHOLD = 0.5;
    private static final double SUNGLASS_THRESHOLD = 0.5;

    private static final int BRISQUE_THRESHOLD = 60;

    private static final double LIVENESS_THRESHOLD = 0.5;
    private static final double EYE_CLOSE_THRESHOLD = 0.8;

    private static final float FACE_CENTRE_TOLERANCE = 10f;
    private static final float FACE_WIDTH_TOLERANCE = 10f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startCapture();
    }

    private void startCapture() {

        try {

            FaceCaptureController controller =
                    FaceCaptureController.getInstance();

            controller.setAutoCapture(true);
            controller.setUseBackCamera(false);
            controller.setFrameCapture(true);
            controller.setOcclusionEnabled(true);
            controller.setEyeClosedEnabled(true);

            controller.setGlassDetection(
                    GlassDetection.ANY_GLASSES
            );

            controller.setCompression(true);

            controller.setEnableCaptureAfter(6);

            controller.setCaptureTimeoutInSecs(30);

            controller.setIsGetFullFrontalCrop(true);

            controller.setFontSize(18);

            controller.setMessagesFrequency(6);

            AirsnapFaceThresholds thresholds =
                    new AirsnapFaceThresholds();

            thresholds.setPITCH_THRESHOLD(PITCH_THRESHOLD);
            thresholds.setYAW_THRESHOLD(YAW_THRESHOLD);

            thresholds.setRollThreshold(ROLL_THRESHOLD);

            thresholds.setMASK_THRESHOLD(MASK_THRESHOLD);

            thresholds.setANYGLASS_THRESHOLD(
                    ANY_GLASS_THRESHOLD
            );

            thresholds.setSUNGLASS_THRESHOLD(
                    SUNGLASS_THRESHOLD
            );

            thresholds.setBRISQUE_THRESHOLD(
                    BRISQUE_THRESHOLD
            );

            thresholds.setLIVENESS_THRESHOLD(
                    LIVENESS_THRESHOLD
            );

            thresholds.setEYE_CLOSE_THRESHOLD(
                    EYE_CLOSE_THRESHOLD
            );

            thresholds.setFaceCentreToImageCentreTolerance(
                    FACE_CENTRE_TOLERANCE
            );

            thresholds.setFaceWidthToImageWidthRatioTolerance(
                    FACE_WIDTH_TOLERANCE
            );

            controller.setAirsnapFaceThresholds(thresholds);

            controller.startFaceCapture(
                    this,
                    new FaceCaptureListener() {

                        @Override
                        public void onFaceCaptured(
                                byte[] faceImageBytes,
                                byte[] faceTemplateBytes,
                                FaceBox faceBox
                        ) {

                            Log.d(TAG, "FACE CAPTURE SUCCESS");

                            String base64Image =
                                    Base64.encodeToString(
                                            faceImageBytes,
                                            Base64.NO_WRAP
                                    );

                            Intent resultIntent = new Intent();

                            resultIntent.putExtra(
                                    "base64Image",
                                    base64Image
                            );

                            setResult(
                                    Activity.RESULT_OK,
                                    resultIntent
                            );

                            finish();
                        }

                        @Override
                        public void OnFaceCaptureFailed(
                                String errorMessage
                        ) {

                            closeCapture();
                        }

                        @Override
                        public void onTimedout(
                                byte[] faceImage
                        ) {

                            Log.e(TAG, "Capture Timeout");

                            closeCapture();
                        }

                        @Override
                        public void onCancelled() {

                            Log.e(TAG, "Capture Cancelled");

                            closeCapture();
                        }
                    }
            );

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Unable to start face capture",
                    e
            );

            closeCapture();
        }
    }

    private void closeCapture() {

        runOnUiThread(() -> {

            if (!isFinishing()) {
                finish();
            }
        });
    }
}
