package com.islamdidarmd.adblockerwebview

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

class AdBlockerUtil private constructor() {
    private var isStillLoading: Boolean = false

    private val mTag = "AdBlockerUtil"

    private val mAdHostListFileName = "adblocker_webview_hosts.txt"
    private val hostMap = hashMapOf<String, Boolean>()

    companion object {
        private val mInstance by lazy { AdBlockerUtil() }
        fun getInstance(): AdBlockerUtil {
            return mInstance
        }
    }

    @ExperimentalCoroutinesApi
    fun initialize(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {
        scope.launch {
            loadHostFromServer()
                .collect { value: InputStream? ->
                    try {
                        val stream = value ?: context.assets.open(mAdHostListFileName)
                        loadHostsFromInputStream(stream)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    fun isAd(url: String?): Boolean {
        if (isStillLoading) {
            if (BuildConfig.DEBUG) Log.e(mTag, "isAd: Host entries are still loading.")
            return false
        }
        if (url == null) return false
        val requestHost = Uri.parse(url).host ?: return false
        return hostMap.containsKey(requestHost)
    }

    fun createEmptyResponse(): WebResourceResponse {
        return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
    }

    private fun loadHostsFromInputStream(inputStream: InputStream) {
        isStillLoading = true
        hostMap.clear()

        inputStream.source().buffer().use { source ->
            generateSequence { source.readUtf8Line() }
                .forEach { line ->
                    hostMap[line] = true
                }
        }

        isStillLoading = false
    }

    @ExperimentalCoroutinesApi
    private fun loadHostFromServer() = callbackFlow {
        val url = "https://pgl.yoyo.org/as/serverlist.php?hostformat=nohtml&showintro=0"
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .url(url)
            .build()
        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    trySend(response.body?.byteStream())
                    close()
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    trySend(null)
                    close()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()

            trySend(null)
            close()
        }

        awaitClose()
    }
}