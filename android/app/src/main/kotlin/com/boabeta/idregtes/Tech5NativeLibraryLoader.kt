package com.boabeta.idregtes

import android.util.Log

/**
 * T5AirSnap does not load libncnn, but initSdkNative uses it. Loading ncnn first
 * avoids SIGSEGV during license / SDK initialization on release builds.
 */
object Tech5NativeLibraryLoader {

    private const val TAG = "TECH5"

    @Volatile
    private var loaded = false

    @Synchronized
    fun load() {
        if (loaded) return

        try {
            System.loadLibrary("ncnn")
            System.loadLibrary("c++_shared")
            System.loadLibrary("opencv_world")
            System.loadLibrary("T5NistLibrary")
            System.loadLibrary("T5AirSnap")
            loaded = true
            Log.d(TAG, "Tech5 native libraries loaded")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load Tech5 native libraries", e)
            throw e
        }
    }
}
