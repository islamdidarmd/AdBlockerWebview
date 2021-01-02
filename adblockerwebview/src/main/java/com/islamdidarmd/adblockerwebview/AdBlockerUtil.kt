package com.islamdidarmd.adblockerwebview

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceResponse
import kotlinx.coroutines.*
import okhttp3.*
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

class AdBlockerUtil(context: Context) {
    val TAG = "AdBlockerUtil"
    private val AD_HOSTS_FILE_NAME = "adblocker_webview_hosts.txt"
    private val hostsList = mutableListOf<String>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            //loading hostname from asset
            withContext(Dispatchers.IO) {
                try {
                    loadHostsFromInputStream(context.assets.open(AD_HOSTS_FILE_NAME))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            delay(200)
            //updating list from server
            loadHostFromServer()
        }
    }

    fun isAd(url: String?): Boolean {
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

    private fun loadHostsFromInputStream(inputStream: InputStream?) {
        if (inputStream == null) {
            return
        }

        val tempList: MutableList<String> = ArrayList()

        inputStream.source().buffer().use { source ->
            generateSequence { source.readUtf8Line() }
                    .forEach { line ->
                        tempList.add(line)
                    }
        }

        hostsList.clear()
        hostsList.addAll(tempList)
    }

    private fun loadHostFromServer() {
        val url = "https://pgl.yoyo.org/as/serverlist.php?hostformat=nohtml&showintro=0"
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
                .url(url)
                .build()
        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    loadHostsFromInputStream(response.body?.byteStream())
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}