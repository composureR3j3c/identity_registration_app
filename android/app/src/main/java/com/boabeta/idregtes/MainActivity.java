package com.boabeta.idregtes;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {

    private static final String FACE_CHANNEL =
            "tech5/face_capture";

//     private static final String FINGER_CHANNEL =
//             "tech5/finger_capture";

    private static final int REQUEST_CODE_FACE_CAPTURE =
            1001;

//     private static final int REQUEST_CODE_FINGER_CAPTURE =
//             2001;

    private MethodChannel.Result pendingFaceResult;

//     private MethodChannel.Result pendingFingerResult;

    @Override
    public void configureFlutterEngine(
            @NonNull FlutterEngine flutterEngine
    ) {

        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(
                flutterEngine.getDartExecutor().getBinaryMessenger(),
                FACE_CHANNEL
        ).setMethodCallHandler((call, result) -> {

            if ("startFaceCapture".equals(call.method)) {

                pendingFaceResult = result;

                Intent intent = new Intent(
                        MainActivity.this,
                        FaceCaptureActivity.class
                );

                if (call.hasArgument("brisqueThreshold")) {
                    intent.putExtra(
                            "brisqueThreshold",
                            ((Number) call.argument("brisqueThreshold")).intValue()
                    );
                }

                if (call.hasArgument("livenessThreshold")) {
                    intent.putExtra(
                            "livenessThreshold",
                            ((Number) call.argument("livenessThreshold")).doubleValue()
                    );
                }

                startActivityForResult(
                        intent,
                        REQUEST_CODE_FACE_CAPTURE
                );

            } else {

                result.notImplemented();
            }
        });


    }

    private void clearKeyboardFocusBeforeFlutterCallback() {

        if (getCurrentFocus() != null) {

            getCurrentFocus().clearFocus();
        }

        if (getWindow() != null &&
                getWindow().getDecorView() != null) {

            getWindow()
                    .getDecorView()
                    .post(() ->
                            getWindow()
                                    .getDecorView()
                                    .clearFocus()
                    );
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        clearKeyboardFocusBeforeFlutterCallback();

        if (requestCode ==
                REQUEST_CODE_FACE_CAPTURE) {

            if (resultCode ==
                    Activity.RESULT_OK) {

                String base64Image =
                        data != null
                                ? data.getStringExtra("base64Image")
                                : "";

                if (base64Image != null &&
                        !base64Image.isEmpty()) {

                    pendingFaceResult.success(
                            base64Image
                    );

                } else {

                    pendingFaceResult.error(
                            "NO_IMAGE",
                            "Face captured but image missing",
                            null
                    );
                }

            } else {

                String error =
                        data != null
                                ? data.getStringExtra("error")
                                : "Face capture failed";

                pendingFaceResult.error(
                        "FACE_CAPTURE_FAILED",
                        error,
                        null
                );
            }

            pendingFaceResult = null;
        }

        // if (requestCode ==
        //         REQUEST_CODE_FINGER_CAPTURE) {

        //     if (resultCode ==
        //             Activity.RESULT_OK) {

        //         String base64Image =
        //                 data != null
        //                         ? data.getStringExtra("base64Image")
        //                         : "";

        //         if (base64Image != null &&
        //                 !base64Image.isEmpty()) {

        //             pendingFingerResult.success(
        //                     base64Image
        //             );

        //         } else {

        //             pendingFingerResult.error(
        //                     "NO_IMAGE",
        //                     "Finger captured but image missing",
        //                     null
        //             );
        //         }

        //     } else {

        //         String error =
        //                 data != null
        //                         ? data.getStringExtra("error")
        //                         : "Finger capture failed";

        //         pendingFingerResult.error(
        //                 "FINGER_CAPTURE_FAILED",
        //                 error,
        //                 null
        //         );
        //     }

        //     pendingFingerResult = null;
        // }
    }
}
