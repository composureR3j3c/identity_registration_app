package com.boabeta.idregtes

import android.app.Activity
import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    // =========================
    // CHANNELS
    // =========================

    private val FACE_CHANNEL =
        "tech5/face_capture"

    private val FINGER_CHANNEL =
        "tech5/finger_capture"

    // =========================
    // REQUEST CODES
    // =========================

    private val REQUEST_CODE_FACE_CAPTURE =
        1001

    private val REQUEST_CODE_FINGER_CAPTURE =
        2001

    // =========================
    // RESULTS
    // =========================

    private var pendingFaceResult:
        MethodChannel.Result? = null

    private var pendingFingerResult:
        MethodChannel.Result? = null

    override fun configureFlutterEngine(
        flutterEngine: FlutterEngine
    ) {

        super.configureFlutterEngine(
            flutterEngine
        )

        // =========================
        // FACE CHANNEL
        // =========================

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            FACE_CHANNEL
        ).setMethodCallHandler { call, result ->

            when (call.method) {

                "startFaceCapture" -> {

                    pendingFaceResult = result

                    val intent = Intent(
                        this,
                        FaceCaptureActivity::class.java
                    )

                    startActivityForResult(
                        intent,
                        REQUEST_CODE_FACE_CAPTURE
                    )
                }

                else -> {
                    result.notImplemented()
                }
            }
        }

        // =========================
        // FINGER CHANNEL
        // =========================

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            FINGER_CHANNEL
        ).setMethodCallHandler { call, result ->

            when (call.method) {

                "startFingerCapture" -> {

                    pendingFingerResult = result

                    val intent = Intent(
                        this,
                        FingerCaptureActivity::class.java
                    )

                    startActivityForResult(
                        intent,
                        REQUEST_CODE_FINGER_CAPTURE
                    )
                }

                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        // =========================
        // FACE RESULT
        // =========================

        if (requestCode ==
            REQUEST_CODE_FACE_CAPTURE
        ) {

            if (resultCode ==
                Activity.RESULT_OK
            ) {

                val base64Image =
                    data?.getStringExtra(
                        "base64Image"
                    ) ?: ""

                if (base64Image.isNotEmpty()) {

                    pendingFaceResult?.success(
                        base64Image
                    )

                } else {

                    pendingFaceResult?.error(
                        "NO_IMAGE",
                        "Face captured but image missing",
                        null
                    )
                }

            } else {

                val error =
                    data?.getStringExtra(
                        "error"
                    ) ?: "Face capture failed"

                pendingFaceResult?.error(
                    "FACE_CAPTURE_FAILED",
                    error,
                    null
                )
            }

            pendingFaceResult = null
        }

        // =========================
        // FINGER RESULT
        // =========================

        if (requestCode ==
            REQUEST_CODE_FINGER_CAPTURE
        ) {

            if (resultCode ==
                Activity.RESULT_OK
            ) {

                val base64Image =
                    data?.getStringExtra(
                        "base64Image"
                    ) ?: ""

                if (base64Image.isNotEmpty()) {

                    pendingFingerResult?.success(
                        base64Image
                    )

                } else {

                    pendingFingerResult?.error(
                        "NO_IMAGE",
                        "Finger captured but image missing",
                        null
                    )
                }

            } else {

                val error =
                    data?.getStringExtra(
                        "error"
                    ) ?: "Finger capture failed"

                pendingFingerResult?.error(
                    "FINGER_CAPTURE_FAILED",
                    error,
                    null
                )
            }

            pendingFingerResult = null
        }
    }
}