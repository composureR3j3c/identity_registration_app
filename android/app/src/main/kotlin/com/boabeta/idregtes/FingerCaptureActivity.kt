package com.boabeta.idregtes

import ai.tech5.finger.utils.CaptureMode
import ai.tech5.finger.utils.FingerCaptureResult
import ai.tech5.finger.utils.ImageConfiguration
import ai.tech5.finger.utils.ImageType
import ai.tech5.finger.utils.SegmentationMode
import ai.tech5.finger.utils.T5FingerCaptureController
import ai.tech5.finger.utils.T5FingerCapturedListener
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class FingerCaptureActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TECH5"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startFingerCapture()
    }

    private fun startFingerCapture() {

        try {

            
            val controller = T5FingerCaptureController.getInstance()

            // IMPORTANT: empty string for online portal activation
            // controller.setLicense("")

            // val initialized = controller.initSDK(this)

            // if (!initialized) {
            //     Log.e("TECH5", "SDK initialization failed (license activation failed): ${initialized.toString()}")
            //     return
            // }

            // =========================
            // OPTIONAL SETTINGS
            // =========================

            controller.setLivenessCheck(true)

            controller.setIsGetQuality(true)

            controller.setIsGetNist2Quality(true)

            controller.showElipses(true)

            controller.setDetectorThreshold(0.9f)

            controller.setTimeoutInSecs(30)

            controller.setCaptureMode(CaptureMode.CAPTURE_MODE_SELF)

            controller.setTitle("Capture Finger")

            controller.setShowBackButton(true)

            controller.setZoomFactor(1.5f)

            controller.setPropDenoise(true)

            controller.setCleanFingerPrints(true)

            controller.setOutsideCaptureFlag(false)

            controller.setCaptureSpeed(ai.tech5.finger.utils.CaptureSpeed.CAPTURE_SPEED_MEDIUM)

            // =========================
            // SEGMENTATION MODE
            // =========================

            val segmentationModes = linkedSetOf<SegmentationMode>()

            segmentationModes.add(SegmentationMode.SEGMENTATION_MODE_LEFT_THUMB)

            controller.setSegmentationModes(segmentationModes)

            // =========================
            // IMAGE CONFIG
            // =========================

            val segmentedConfig = ImageConfiguration()

            segmentedConfig.setPrimaryImageType(ImageType.IMAGE_TYPE_PNG)

            segmentedConfig.setIsCropImage(true)

            segmentedConfig.setCroppedImageWidth(300)

            segmentedConfig.setCroppedImageHeight(400)

            segmentedConfig.setPaddingColor(255)

            controller.setSegmentedFingerImagesConfig(segmentedConfig)

            // =========================
            // START CAPTURE
            // =========================

            controller.captureFingers(
                    this,
                    object : T5FingerCapturedListener {

                        override fun onSuccess(result: FingerCaptureResult) {

                            try {

                                Log.d(TAG, "Finger capture success")

                                if (result.fingers.isEmpty()) {

                                    Log.e(TAG, "No fingers captured")

                                    closeCapture()
                                    return
                                }

                                val finger = result.fingers[0]

                                val base64Image =
                                        Base64.encodeToString(finger.primaryImage, Base64.NO_WRAP)

                                val resultIntent =
                                        Intent().apply {
                                            putExtra("base64Image", base64Image)

                                            putExtra("quality", finger.quality)

                                            putExtra("nistQuality", finger.nistQuality)

                                            putExtra(
                                                    "livenessScore",
                                                    result.livenessScores[finger.pos]
                                            )

                                            putExtra("position", finger.pos)
                                        }

                                setResult(Activity.RESULT_OK, resultIntent)

                                finish()
                            } catch (e: Exception) {

                                Log.e(TAG, "Error processing result", e)

                                closeCapture()
                            }
                        }

                        override fun onFailure(errorMessage: String) {

                            Log.e(TAG, errorMessage)

                            val intent = Intent().apply { putExtra("error", errorMessage) }

                            setResult(Activity.RESULT_CANCELED, intent)

                            finish()
                        }

                        override fun onCancelled() {

                            Log.e(TAG, "Finger capture cancelled")

                            val intent = Intent().apply { putExtra("error", "Capture cancelled") }

                            setResult(Activity.RESULT_CANCELED, intent)

                            finish()
                        }

                        override fun onTimedout() {

                            Log.e(TAG, "Finger capture timeout")

                            val intent = Intent().apply { putExtra("error", "Capture timeout") }

                            setResult(Activity.RESULT_CANCELED, intent)

                            finish()
                        }
                    }
            )
        } catch (e: Exception) {

            Log.e(TAG, "Unable to start finger capture", e)

            closeCapture()
        }
    }

    private fun closeCapture() {

        runOnUiThread {
            if (!isFinishing) {
                finish()
            }
        }
    }
}
