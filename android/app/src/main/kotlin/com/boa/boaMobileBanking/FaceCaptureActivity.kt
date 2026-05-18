package com.boa.boaMobileBanking

import ai.tech5.pheonix.capture.controller.AirsnapFaceThresholds
import ai.tech5.pheonix.capture.controller.FaceCaptureController
import ai.tech5.pheonix.capture.controller.FaceCaptureListener
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.phoenixcapture.camerakit.FaceBox

class FaceCaptureActivity : AppCompatActivity() {

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
            controller.setCompression(true)
            controller.setEnableCaptureAfter(6)
            controller.setCaptureTimeoutInSecs(30)
            controller.setIsGetFullFrontalCrop(true)
            controller.setFontSize(18)
            controller.setMessagesFrequency(6)

            val thresholds = AirsnapFaceThresholds()

            thresholds.setPITCH_THRESHOLD(15)
            thresholds.setYAW_THRESHOLD(15)
            thresholds.setBRISQUE_THRESHOLD(60)
            thresholds.setMASK_THRESHOLD(0.5)
            thresholds.setSUNGLASS_THRESHOLD(0.5)
            thresholds.setEYE_CLOSE_THRESHOLD(0.4)
            thresholds.setFaceCentreToImageCentreTolerance(10f)
            thresholds.setFaceWidthToImageWidthRatioTolerance(10f)

            controller.setAirsnapFaceThresholds(thresholds)
            controller.setLicense("YOUR_LICENSE_KEY")

            controller.startFaceCapture(
                this,
                object : FaceCaptureListener {

                    override fun onFaceCaptured(
                        faceImageBytes: ByteArray,
                        faceTemplateBytes: ByteArray,
                        faceBox: FaceBox
                    ) {

                        Log.d("TECH5", "FACE CAPTURE SUCCESS")
                        Log.d("TECH5", "IMAGE SIZE: ${faceImageBytes.size}")

                        runOnUiThread {
                            finish()
                        }
                    }

                    override fun OnFaceCaptureFailed(errorMessage: String) {

                        Log.e("TECH5", errorMessage)
                    }

                    override fun onTimedout(faceImage: ByteArray) {

                        Log.e("TECH5", "Capture Timeout")
                    }

                    override fun onCancelled() {

                        Log.e("TECH5", "Capture Cancelled")
                    }
                }
            )

        } catch (e: Exception) {

            Log.e("TECH5", e.message.toString())
        }
    }
}
