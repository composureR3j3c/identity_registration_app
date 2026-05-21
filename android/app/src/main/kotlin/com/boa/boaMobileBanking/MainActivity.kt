package com.boa.boaMobileBanking

import android.app.Activity
import android.content.Intent
import android.util.Base64
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private val CHANNEL = "tech5/face_capture"
    private val REQUEST_CODE_FACE_CAPTURE = 1001

    private var pendingResult: MethodChannel.Result? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->

                if (call.method == "startFaceCapture") {

                    pendingResult = result

                    val intent = Intent(this, FaceCaptureActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_FACE_CAPTURE)

                } else {
                    result.notImplemented()
                }
            }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_FACE_CAPTURE) {

            if (resultCode == Activity.RESULT_OK) {

                val base64Image = data?.getStringExtra("base64Image") ?: ""

                if (base64Image != null) {
                    pendingResult?.success(base64Image)
                } else {
                    pendingResult?.error(
                        "NO_IMAGE",
                        "Face captured but image missing",
                        null
                    )
                }

            } else {

                val error = data?.getStringExtra("error") ?: "Capture failed"

                pendingResult?.error(
                    "FACE_CAPTURE_FAILED",
                    error,
                    null
                )
            }

            pendingResult = null
        }
    }
}