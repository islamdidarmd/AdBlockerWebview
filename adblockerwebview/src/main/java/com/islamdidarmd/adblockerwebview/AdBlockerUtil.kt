package com.islamdidarmd.adblockerwebview

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import android.webkit.WebResourceResponse
import java.io.*
import java.net.URL

class AdBlockerUtil(context: Context) {
    val TAG = "AdBlockerUtil"
    private val AD_HOSTS_FILE_NAME = "adblocker_webview_hosts.txt"
    private val hostsList: MutableList<String> = ArrayList()

    init {
        //loading hostnames from asset
        loadHostsFromInputStream(context.assets.open(AD_HOSTS_FILE_NAME))

        //updating list from server
        loadHostFromServer()
    }


    fun isAd(url: String?): Boolean {
        if (url == null) return false
        val host = Uri.parse(url).host ?: return false

        Log.d(TAG, "Matching host $host...")
        hostsList.forEach {
            if (host.contains(it)) {
                Log.d(TAG, "$url is Ad")
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
            Log.d(TAG, "inputStream is null. Returning....")
            return
        }

        Log.d(TAG, "Loading hosts...")
        var reader: BufferedReader? = null

        try {
            reader = BufferedReader(InputStreamReader(inputStream, "utf-8"))
            val tempList: MutableList<String> = ArrayList()
            var line = reader.readLine()

            while (line != null) {
                tempList.add(line)
                line = reader.readLine()
            }

            hostsList.clear()
            hostsList.addAll(tempList)

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        Log.d(TAG, hostsList.size.toString())
    }

    private fun loadHostFromServer() {
        Thread(Runnable {
            val url = URL("https://pgl.yoyo.org/as/serverlist.php?hostformat=nohtml&showintro=0")
            val connection = url.openConnection()
            connection.connect()
            loadHostsFromInputStream(url.openStream())
        }).start()
    }
}