//package com.boabeta.idregtesdemo.flutter
//
//import ai.tech5.airsnap_finger_flutter_plugin.Constants
//import android.util.Base64
//import android.util.Log
//import io.flutter.embedding.engine.plugins.FlutterPlugin
//import io.flutter.plugin.common.MethodCall
//import io.flutter.plugin.common.MethodChannel
//import io.flutter.plugin.common.MethodChannel.MethodCallHandler
//import io.flutter.plugin.common.MethodChannel.Result
//
//
//import ai.tech5.finger.utils.CaptureMode
//import ai.tech5.finger.utils.CaptureSpeed
//import ai.tech5.finger.utils.FingerCaptureResult
//import ai.tech5.finger.utils.ImageConfiguration
//import ai.tech5.finger.utils.ImageType
//import ai.tech5.finger.utils.SegmentationMode
//import ai.tech5.finger.utils.T5FingerCaptureController
//import ai.tech5.finger.utils.T5FingerCapturedListener
//import android.app.Activity
//import android.content.Context
//import android.os.Build
//import org.json.JSONArray
//import org.json.JSONException
//import org.json.JSONObject
//import java.io.File
//import java.io.FileOutputStream
//import java.util.ArrayList
//import java.util.LinkedHashSet
//import java.util.Objects
//import io.flutter.embedding.engine.plugins.activity.ActivityAware
//import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
//import java.lang.Exception
//
///** AirsnapFingerFlutterPlugin */
//class AirsnapFingerFlutterPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
//  /// The MethodChannel that will the communication between Flutter and native Android
//  ///
//  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
//  /// when the Flutter Engine is detached from the Activity
//  private lateinit var channel : MethodChannel
//  private var result: MethodChannel.Result? = null
//  private lateinit var context: Context
//  private lateinit var rootDirectory: String
//  private var activity: Activity? = null
//
//  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
//    context = flutterPluginBinding.applicationContext
//    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "airsnap_finger_flutter_plugin")
//    channel.setMethodCallHandler(this)
//
//    rootDirectory =
//      Objects.requireNonNull<File>(context.getExternalFilesDir(null))
//        .absolutePath;
//  }
//
//  override fun onMethodCall(call: MethodCall, result: Result) {
//
//    this.result = result
//
//    if (call.method == "getPlatformVersion") {
//      result.success("Android ${Build.VERSION.RELEASE}")
//    }else if (call.method == "startFingerCapture") {
//
//      val jsonString = call.arguments as String
//
//      Log.d("TAG","capture options from dart to native $jsonString")
//
//      startFingerCapture(jsonString)
//
//    } else {
//      result.notImplemented()
//    }
//  }
//
//  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
//    channel.setMethodCallHandler(null)
//  }
//
//
//  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
//    activity = binding.activity
//  }
//
//  override fun onDetachedFromActivityForConfigChanges() {
//    activity = null
//  }
//
//  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
//    activity = binding.activity
//  }
//
//  override fun onDetachedFromActivity() {
//    activity = null
//  }
//
//
//  private fun startFingerCapture(config: String) {
//    try {
//      val conf = JSONObject(config)
//
//      val controller: T5FingerCaptureController = getDefaultCaptureController()
//      setRNCaptureConfigs(controller, conf)
//
//
//      val fingerImageConfig: ImageConfiguration = getDefaultSegmentFingureImageConfiguration()
//      val fingerImageOptions =
//        conf.optJSONObject(Constants.Companion.FINGER_IMAGE_OPTIONS)
//      if (fingerImageOptions != null) {
//        setRNImageConfig(fingerImageConfig, fingerImageOptions)
//      }
//      controller.setSegmentedFingerImagesConfig(fingerImageConfig)
//
//      val slapConfig: ImageConfiguration = getDefaultSlapConfiguration()
//      val slapImageOptions =
//        conf.optJSONObject(Constants.Companion.SLAP_IMAGE_OPTIONS)
//      if (slapImageOptions != null) {
//        setRNImageConfig(slapConfig, slapImageOptions)
//      }
//      controller.setSlapImagesConfig(slapConfig)
//
//     // controller.captureFingers(activity, this)
//      controller.captureFingers(activity, object : T5FingerCapturedListener {
//        override fun onSuccess(p0: FingerCaptureResult?) {
//
//          if(p0!=null) {
//
//            val capResultString = getCaptureFingerResultJson(p0)
//            Log.d("TAG","result $capResultString")
//
//            val fingerResultJsonPath: String =
//              rootDirectory + File.separator + "cap_result_"+System.currentTimeMillis()+".json";
//
//            writeToFile(capResultString.toString().encodeToByteArray(), fingerResultJsonPath)
//
//            result?.success(fingerResultJsonPath)
//
//            // result?.success(capResultString)
//
//
//          }else{
//            result?.error("FAILED","Finger capture failed",null)
//          }
//        }
//
//        override fun onFailure(s: String) {
//          result?.error("FAILED","Finger capture failed: $s",null)
//        }
//
//        override fun onCancelled() {
//          result?.error("CANCELLED","Capture cancelled",null)
//        }
//
//        override fun onTimedout() {
//          result?.error("CAPTURE_TIMEOUT","Capture timedout",null)
//        }
//      })
//    } catch (_: Exception) {
//
//    }
//  }
//
//
//
//
//
//  @Throws(JSONException::class)
//  private fun getCaptureFingerResultJson(fingerCaptureResult: FingerCaptureResult): JSONObject {
//    val result = JSONObject()
//
//    fingerCaptureResult.livenessScores?.let { livenessScores ->
//      val livenessScoresArray = JSONArray()
//      for (livenessScore in livenessScores) {
//        val livenessScoreJson = JSONObject().apply {
//          put("pos", livenessScore.pos)
//          put("score", livenessScore.score)
//        }
//        livenessScoresArray.put(livenessScoreJson)
//      }
//      result.put("livenessScores", livenessScoresArray)
//    }
//
//    val fingersArray = JSONArray()
//    for (finger in fingerCaptureResult.fingers) {
//      val fingerJson = JSONObject().apply {
//        put("minutiaesNumber", finger.minutiaesNumber)
//        put("quality", finger.quality)
//        put("pos", finger.pos)
//        put("nistQuality", finger.nistQuality)
//        put("nist2Quality", finger.nist2Quality)
//        put(Constants.Companion.IMAGE_OPTIONS_PRIMARY_IMAGE_TYPE, finger.primaryImageType.ordinal)
//        put("primaryImage", Base64.encodeToString(finger.primaryImage, Base64.NO_WRAP))
//
//        if (finger.displayImage != null && finger.displayImage.isNotEmpty()) {
//          put("displayImage", Base64.encodeToString(finger.displayImage, Base64.NO_WRAP))
//          put(Constants.Companion.IMAGE_OPTIONS_DISPLAY_IMAGE_TYPE, finger.displayImageType.ordinal)
//        }
//      }
//      fingersArray.put(fingerJson)
//    }
//    result.put("fingers", fingersArray)
//
//    fingerCaptureResult.slapImages?.let { slapImages ->
//      val slapsArray = JSONArray()
//      for (slap in slapImages) {
//        val slapJson = JSONObject().apply {
//          put("pos", slap.pos)
//          put("imageType", slap.imageType.ordinal)
//          put("image", Base64.encodeToString(slap.image, Base64.NO_WRAP))
//        }
//        slapsArray.put(slapJson)
//      }
//      result.put("slaps", slapsArray)
//    }
//
//    return result
//  }
//
//
//  private fun writeToFile(data: ByteArray, path: String) {
//    try {
//      Log.d("TAG", "saving to $path")
//      val myFile = File(path)
//
//      if (myFile.exists()) {
//        myFile.delete()
//      }
//
//      myFile.createNewFile()
//      val fOut = FileOutputStream(myFile)
//      fOut.write(data)
//
//      fOut.close()
//    } catch (e: Exception) {
//      e.printStackTrace()
//    }
//  }
//
//
//  private fun getDefaultCaptureController(): T5FingerCaptureController {
//    val t5FingerCaptureController: T5FingerCaptureController =
//      T5FingerCaptureController.getInstance()
//    t5FingerCaptureController.setLicense("")
//
//    t5FingerCaptureController.showElipses(true)
//    t5FingerCaptureController.setLivenessCheck(true)
//
//    t5FingerCaptureController.setIsGetQuality(true)
//    t5FingerCaptureController.setIsGetNist2Quality(false)
//
//    //t5FingerCaptureController.setCreateTemplates(true);
//    val segmentationModes: LinkedHashSet<SegmentationMode> = LinkedHashSet<SegmentationMode>()
//    segmentationModes.add(SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP)
//
//
//    t5FingerCaptureController.setDetectorThreshold(0.9f)
//    t5FingerCaptureController.setSegmentationModes(segmentationModes)
//    t5FingerCaptureController.setCaptureMode(CaptureMode.CAPTURE_MODE_SELF)
//
//    //t5FingerCaptureController.setTitle("T5 Finger Capture");
//    t5FingerCaptureController.setCaptureSpeed(CaptureSpeed.CAPTURE_SPEED_LOW)
//    t5FingerCaptureController.setPropDenoise(true)
//    t5FingerCaptureController.setCleanFingerPrints(false)
//    t5FingerCaptureController.setTimeoutInSecs(60)
//
//    return t5FingerCaptureController
//  }
//
//  private fun getDefaultSegmentFingureImageConfiguration(): ImageConfiguration {
//    val segmentedFingersConfiguration: ImageConfiguration = ImageConfiguration()
//
//    segmentedFingersConfiguration.setPrimaryImageType(ImageType.IMAGE_TYPE_WSQ)
//    //compresion ratio is only applicable for IMAGE_TYPE_WSQ
//    segmentedFingersConfiguration.setCompressionRatio(10f)
//
//    segmentedFingersConfiguration.setRequireDisplayImage(false)
//
//    segmentedFingersConfiguration.setIsCropImage(true)
//    segmentedFingersConfiguration.setCroppedImageWidth(512)
//    segmentedFingersConfiguration.setCroppedImageHeight(512)
//    //0->Black color padding; 255->white color padding
//    segmentedFingersConfiguration.setPaddingColor(255)
//    return segmentedFingersConfiguration
//  }
//
//  private fun getDefaultSlapConfiguration(): ImageConfiguration {
//    val slapConfig: ImageConfiguration = ImageConfiguration()
//    slapConfig.setPrimaryImageType(ImageType.IMAGE_TYPE_PNG)
//    slapConfig.setCompressionRatio(10f)
//    slapConfig.setIsCropImage(false)
//    return slapConfig
//  }
//
//  @Throws(JSONException::class)
//  private fun setRNCaptureConfigs(
//    captureController: T5FingerCaptureController,
//    conf: JSONObject,
//  ) {
//    captureController.setLicense(conf.optString(Constants.Companion.LICENCE, ""))
//
//    captureController.showElipses(
//      conf.optBoolean(
//        Constants.Companion.SHOW_ELIPSES,
//        true
//      )
//    )
//
//    captureController.setLivenessCheck(
//      conf.optBoolean(
//        Constants.Companion.LIVENESS_CHECK,
//        true
//      )
//    )
//
//    captureController.setIsGetQuality(
//      conf.optBoolean(
//        Constants.Companion.INCLUDE_QUALITY,
//        true
//      )
//    )
//    captureController.setIsGetNist2Quality(
//      conf.optBoolean(
//        Constants.Companion.INCLUDE_NIST2_QUALITY,
//        false
//      )
//    )
//
//    captureController.setDetectorThreshold(
//      conf.optDouble(
//        Constants.Companion.DETECTOR_THRESHOLD,
//        0.9
//      ).toFloat()
//    )
//
//    val segmentationModes = conf.optJSONArray(Constants.Companion.SEGMENTATION_MODES)
//
//    Log.d("TAG", "segmentation modes json array: $segmentationModes")
//
//
//    val modes: Array<SegmentationMode> = SegmentationMode.entries.toTypedArray()
//
//    for (i in modes.indices) {
//      Log.d("TAG", i.toString() + " mode " + modes[i])
//    }
//
//
//    val segmentationModeLinkedHashSet: LinkedHashSet<SegmentationMode> =
//      LinkedHashSet<SegmentationMode>()
//
//    if (segmentationModes != null) {
//      for (i in 0..<segmentationModes.length()) {
//        val segmenationMode = segmentationModes[i] as Int
//
//        Log.d("TAG", "in for  $segmenationMode")
//
//
//        segmentationModeLinkedHashSet.add(SegmentationMode.entries[segmenationMode])
//      }
//    }else{
//      segmentationModeLinkedHashSet.add(SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP)
//    }
//
//    Log.d("TAG", "segmentation modes json set: $segmentationModeLinkedHashSet")
//
//    captureController.setSegmentationModes(segmentationModeLinkedHashSet)
//
//
//    val missingFingersArray = conf.optJSONArray(Constants.Companion.MISSING_FINGERS)
//    // Create an empty ArrayList
//    val missingFingersList = ArrayList<Int>()
//
//    // Iterate through the JSONArray
//    if (missingFingersArray != null) {
//      for (i in 0..<missingFingersArray.length()) {
//        val element = missingFingersArray[i]
//
//        // Check if the element is an integer
//        if (element is Int) {
//          missingFingersList.add(element)
//        }
//      }
//    }
//
//    Log.d("TAG", "missing fingers : $missingFingersList")
//
//    captureController.setMissingFingers(missingFingersList)
//
//
//    val captureMode = conf.optInt(
//      Constants.Companion.CAPTURE_MODE,
//      CaptureMode.CAPTURE_MODE_SELF.ordinal
//    )
//    captureController.setCaptureMode(CaptureMode.entries[captureMode])
//
//    val title = conf.optString(Constants.Companion.TITLE, "").trim()
//    if (title.isNotEmpty()) {
//      captureController.setTitle(title)
//    }
//
//    val captureSpeed = conf.optInt(
//      Constants.Companion.CAPTURE_SPEED,
//      CaptureSpeed.CAPTURE_SPEED_LOW.ordinal
//    )
//    captureController.setCaptureSpeed(CaptureSpeed.entries[captureSpeed])
//
//    captureController.setPropDenoise(
//      conf.optBoolean(
//        Constants.Companion.DENOISE,
//        false
//      )
//    )
//
//    captureController.setCleanFingerPrints(
//      conf.optBoolean(
//        Constants.Companion.CLEAN_FINGER_PRINTS,
//        false
//      )
//    )
//    captureController.setTimeoutInSecs(
//      conf.optInt(
//        Constants.Companion.TIMEOUT_IN_SEC,
//        20
//      )
//    )
//  }
//
//  private fun setRNImageConfig(
//    segmentedFingersConfiguration: ImageConfiguration,
//    conf: JSONObject,
//  ) {
//    val imageType = conf.optInt(
//      Constants.Companion.IMAGE_OPTIONS_PRIMARY_IMAGE_TYPE,
//      ImageType.IMAGE_TYPE_WSQ.ordinal
//    )
//    segmentedFingersConfiguration.primaryImageType = ImageType.entries[imageType]
//
//    val compRatio = conf.optDouble(
//      Constants.Companion.IMAGE_OPTIONS_IMAGE_COMPRESSION_RATIO,
//      10.0
//    ).toFloat()
//    //compresion ratio is only applicable for IMAGE_TYPE_WSQ
//    segmentedFingersConfiguration.compressionRatio = compRatio
//
//    val requireDisplayImage = conf.optBoolean(
//      Constants.Companion.IMAGE_OPTIONS_REQUIRE_DISPLAY_IMAGE_TYPE,
//      false
//    )
//    segmentedFingersConfiguration.setRequireDisplayImage(requireDisplayImage)
//
//    val displayImageType = conf.optInt(
//      Constants.Companion.IMAGE_OPTIONS_DISPLAY_IMAGE_TYPE,
//      ImageType.IMAGE_TYPE_PNG.ordinal
//    )
//    segmentedFingersConfiguration.setDisplayImageType(ImageType.entries[displayImageType])
//
//
//    segmentedFingersConfiguration.setIsCropImage(
//      conf.optBoolean(
//        Constants.Companion.IMAGE_OPTIONS_IMAGE_CROP,
//        true
//      )
//    )
//    segmentedFingersConfiguration.croppedImageWidth = conf.optInt(
//      Constants.Companion.IMAGE_OPTIONS_IMAGE_CROPPED_WIDTH,
//      512
//    )
//    segmentedFingersConfiguration.croppedImageHeight = conf.optInt(
//      Constants.Companion.IMAGE_OPTIONS_IMAGE_CROPPED_HEIGHT,
//      512
//    )
//    //0->Black color padding; 255->white color padding
//    segmentedFingersConfiguration.paddingColor = conf.optInt(
//      Constants.Companion.IMAGE_OPTIONS_IMAGE_PADDING_COLOR,
//      255
//    )
//  }
//}
