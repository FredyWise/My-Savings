package com.fredy.mysavings.Util

import android.util.Log
import io.grpc.android.BuildConfig

object Log {
    private const val TAG = "BABI"
    private var isDebuggable = true

    fun setDebuggable(debuggable: Boolean) {
        isDebuggable = debuggable
    }

    fun d(message: String, tag: String = TAG) {
        if (isDebuggable) {
            Log.d(tag, buildLogMsg(message))
        }
    }

    fun w(message: String, tag: String = TAG) {
        if (isDebuggable) {
            Log.w(tag, buildLogMsg(message))
        }
    }

    fun i(message: String, tag: String = TAG) {
        if (isDebuggable) {
            Log.i(tag, buildLogMsg(message))
        }
    }

    fun e(message: String, tag: String = TAG) {
        if (isDebuggable) {
            Log.e(tag, buildLogMsg(message))
        }
    }

    fun e(message: String, tag: String = TAG, tr: Throwable) {
        if (isDebuggable) {
            Log.e(tag, buildLogMsg(message), tr)
        }
    }

    private fun buildLogMsg(message: String): String {
        val ste = Thread.currentThread().stackTrace[4]
        return "(${ste.fileName}:${ste.lineNumber}) $message"
    }
}