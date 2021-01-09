package com.islamdidarmd.adblockerwebview

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import okhttp3.*
import okio.Okio
import okio.buffer
import okio.source
import java.io.*
import java.net.URL
import kotlin.coroutines.suspendCoroutine

class AdBlockerUtil private constructor() {
    private var isStillLoading: Boolean = false

    private val TAG = "AdBlockerUtil"

    private val AD_HOSTS_FILE_NAME = "adblocker_webview_hosts.txt"
    private val hostsList = mutableListOf<String>()

    companion object {
        private val mInstance by lazy { AdBlockerUtil() }
        fun getInstance(): AdBlockerUtil {
            return mInstance
        }
    }

    fun initialize(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {
        scope.launch {
            loadHostFromServer()
                    .collect { value: InputStream? ->
                        try {
                            val stream = value ?: context.assets.open(AD_HOSTS_FILE_NAME)
                            loadHostsFromInputStream(stream)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
        }
    }

    fun isAd(url: String?): Boolean {
        if (isStillLoading) {
            Log.e(TAG, "isAd: Host entries are still loading.")
            return false
        }
        if (url == null) return false
        val host = Uri.parse(url).host ?: return false

        hostsList.forEach {
            if (host.contains(it)) {
                return true
            }
        }
        return false
    }

    fun createEmptyResponse(): WebResourceResponse {
        return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
    }

    private fun loadHostsFromInputStream(inputStream: InputStream) {
        isStillLoading = true
        val tempList: MutableList<String> = ArrayList()

        inputStream.source().buffer().use { source ->
            generateSequence { source.readUtf8Line() }
                    .forEach { line ->
                        tempList.add(line)
                    }
        }

        hostsList.clear()
        hostsList.addAll(tempList)
        isStillLoading = false
    }

    @ExperimentalCoroutinesApi
    private fun loadHostFromServer() = callbackFlow<InputStream?> {
        val url = "https://pgl.yoyo.org/as/serverlist.php?hostformat=nohtml&showintro=0"
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
                .url(url)
                .build()
        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    offer(response.body?.byteStream())
                    close()
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    offer(null)
                    close()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()

            offer(null)
            close()
        }

        awaitClose()
    }
}