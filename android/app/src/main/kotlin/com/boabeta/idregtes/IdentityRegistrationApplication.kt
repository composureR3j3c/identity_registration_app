package com.boabeta.idregtes

import android.app.Application

class IdentityRegistrationApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            Tech5NativeLibraryLoader.load()
        } catch (_: UnsatisfiedLinkError) {
            // Finger capture is unavailable; the capture activity reports the error.
        }
    }
}
