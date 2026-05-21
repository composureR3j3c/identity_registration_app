package com.boa.boaMobileBanking

import ai.tech5.pheonix.capture.controller.AirsnapFaceThresholds
import ai.tech5.pheonix.capture.controller.FaceCaptureController
import ai.tech5.pheonix.capture.controller.FaceCaptureListener
import ai.tech5.pheonix.capture.controller.GlassDetection
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.phoenixcapture.camerakit.FaceBox

class FaceCaptureActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "TECH5"
        private const val PITCH_THRESHOLD = 15
        private const val YAW_THRESHOLD = 15
        private const val ROLL_THRESHOLD = 10
        private const val MASK_THRESHOLD = 0.5
        private const val ANY_GLASS_THRESHOLD = 0.5
        private const val SUNGLASS_THRESHOLD = 0.5
        private const val BRISQUE_THRESHOLD = 60
        private const val LIVENESS_THRESHOLD = 0.5
        private const val EYE_CLOSE_THRESHOLD = 0.8
        private const val FACE_CENTRE_TOLERANCE = 10f
        private const val FACE_WIDTH_TOLERANCE = 10f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startCapture()
    }

    private fun startCapture() {

        try {

            val controller = FaceCaptureController.getInstance()

            controller.setAutoCapture(true)
            controller.setUseBackCamera(false)
            controller.setFrameCapture(true)
            controller.setOcclusionEnabled(true)
            controller.setEyeClosedEnabled(true)
            controller.setGlassDetection(GlassDetection.ANY_GLASSES)
            controller.setCompression(true)
            controller.setEnableCaptureAfter(6)
            controller.setCaptureTimeoutInSecs(30)
            controller.setIsGetFullFrontalCrop(true)
            controller.setFontSize(18)
            controller.setMessagesFrequency(6)

            val thresholds = AirsnapFaceThresholds()

            thresholds.setPITCH_THRESHOLD(PITCH_THRESHOLD)
            thresholds.setYAW_THRESHOLD(YAW_THRESHOLD)
            thresholds.setRollThreshold(ROLL_THRESHOLD)
            thresholds.setMASK_THRESHOLD(MASK_THRESHOLD)
            thresholds.setANYGLASS_THRESHOLD(ANY_GLASS_THRESHOLD)
            thresholds.setSUNGLASS_THRESHOLD(SUNGLASS_THRESHOLD)
            thresholds.setBRISQUE_THRESHOLD(BRISQUE_THRESHOLD)
            thresholds.setLIVENESS_THRESHOLD(LIVENESS_THRESHOLD)
            thresholds.setEYE_CLOSE_THRESHOLD(EYE_CLOSE_THRESHOLD)
            thresholds.setFaceCentreToImageCentreTolerance(FACE_CENTRE_TOLERANCE)
            thresholds.setFaceWidthToImageWidthRatioTolerance(FACE_WIDTH_TOLERANCE)

            controller.setAirsnapFaceThresholds(thresholds)

            controller.startFaceCapture(
                this,
                object : FaceCaptureListener {

                    override fun onFaceCaptured(
                        faceImageBytes: ByteArray,
                        faceTemplateBytes: ByteArray,
                        faceBox: FaceBox
                    ) {
                        Log.d(TAG, "FACE CAPTURE SUCCESS")

                        val base64Image = Base64.encodeToString(
                            faceImageBytes,
                            Base64.NO_WRAP
                        )

                        val resultIntent = Intent().apply {
                            putExtra("base64Image", base64Image)
                        }

                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }

                    override fun OnFaceCaptureFailed(errorMessage: String) {

                        Log.e(TAG, errorMessage)
                        closeCapture()
                    }

                    override fun onTimedout(faceImage: ByteArray) {

                        Log.e(TAG, "Capture Timeout")
                        closeCapture()
                    }

                    override fun onCancelled() {

                        Log.e(TAG, "Capture Cancelled")
                        closeCapture()
                    }
                }
            )

        } catch (e: Exception) {

            Log.e(TAG, "Unable to start face capture", e)
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
