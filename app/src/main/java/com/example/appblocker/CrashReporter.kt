package com.example.appblocker

import android.util.Log

object CrashReporter {
    fun init() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val stackTrace = Log.getStackTraceString(throwable)
            val sanitizedTrace = sanitizePII(stackTrace)
            Log.e("CrashReporter", "Fatal crash in thread ${thread.name}:\n$sanitizedTrace")
            // Report to centralized dashboard (dummy implementation)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun sanitizePII(message: String): String {
        // Basic sanitization: hide email addresses or phone numbers if any
        return message.replace(Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"), "[EMAIL REDACTED]")
            .replace(Regex("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b"), "[PHONE REDACTED]")
    }
}
