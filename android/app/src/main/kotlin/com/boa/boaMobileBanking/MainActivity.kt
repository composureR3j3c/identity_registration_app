package com.boa.boaMobileBanking

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private val CHANNEL = "tech5/face_capture"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->

            if (call.method == "startFaceCapture") {

                val intent = Intent(this, FaceCaptureActivity::class.java)
                startActivity(intent)

                result.success("Capture Started")

            } else {
                result.notImplemented()
            }
        }
    }
}
