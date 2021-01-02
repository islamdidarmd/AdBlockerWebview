package com.islamdidarmd.adblockerwebview

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.webkit.*

open class AdBlockerWebView : WebView {
    private val TAG = "AdBlockerWebView"
    private var _blockAds = false
    private var client: WebViewClient? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttrs: Int) : super(
            context,
            attrs,
            defStyleAttrs
    )

    private val adBlockerUtil by lazy {
        AdBlockerUtil(context)
    }

    fun setAdBlockEnabled(enabled: Boolean) {
        this._blockAds = enabled
        if (BuildConfig.DEBUG) Log.d(TAG, "setAdBlockEnabled $enabled")
    }

    init {
        super.setWebViewClient(object : WebViewClient() {
            override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
            ): WebResourceResponse? {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    client?.shouldInterceptRequest(view, request)
                }

                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                        _blockAds &&
                        request?.url != null &&
                        adBlockerUtil.isAd(request.url.toString())
                ) {
                    adBlockerUtil.createEmptyResponse()
                } else {
                    return super.shouldInterceptRequest(view, request)
                }
            }

            override fun shouldInterceptRequest(
                    view: WebView?,
                    url: String?
            ): WebResourceResponse? {
                client?.shouldInterceptRequest(view, url)

                return if (_blockAds && adBlockerUtil.isAd(url)) {
                    adBlockerUtil.createEmptyResponse()
                } else {
                    super.shouldInterceptRequest(view, url)
                }
            }

            override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
                client?.shouldOverrideKeyEvent(view, event)
                return super.shouldOverrideKeyEvent(view, event)
            }

            override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
            ): Boolean {
                if (Build.VERSION.SDK_INT >= 24) {
                    client?.shouldOverrideUrlLoading(view, request)
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                client?.shouldOverrideUrlLoading(view, url)
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                client?.onPageFinished(view, url)
                super.onPageFinished(view, url)
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                client?.doUpdateVisitedHistory(view, url, isReload)
                super.doUpdateVisitedHistory(view, url, isReload)
            }

            override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
            ) {
                client?.onReceivedError(view, errorCode, description, failingUrl)
                super.onReceivedError(view, errorCode, description, failingUrl)
            }

            override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    client?.onReceivedError(view, request, error)
                }
                super.onReceivedError(view, request, error)
            }

            override fun onRenderProcessGone(
                    view: WebView?,
                    detail: RenderProcessGoneDetail?
            ): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    client?.onRenderProcessGone(view, detail)
                }
                return super.onRenderProcessGone(view, detail)
            }

            override fun onReceivedLoginRequest(
                    view: WebView?,
                    realm: String?,
                    account: String?,
                    args: String?
            ) {
                client?.onReceivedLoginRequest(view, realm, account, args)
                super.onReceivedLoginRequest(view, realm, account, args)
            }

            override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    client?.onReceivedHttpError(view, request, errorResponse)
                }
                super.onReceivedHttpError(view, request, errorResponse)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                client?.onPageStarted(view, url, favicon)
                //  Log.d(TAG,"onPageStarted $url")
                super.onPageStarted(view, url, favicon)
            }

            override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
                client?.onScaleChanged(view, oldScale, newScale)
                super.onScaleChanged(view, oldScale, newScale)
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    client?.onPageCommitVisible(view, url)
                }
                super.onPageCommitVisible(view, url)
            }

            override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
                client?.onUnhandledKeyEvent(view, event)
                super.onUnhandledKeyEvent(view, event)
            }

            override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    client?.onReceivedClientCertRequest(view, request)
                }
                super.onReceivedClientCertRequest(view, request)
            }

            override fun onReceivedHttpAuthRequest(
                    view: WebView?,
                    handler: HttpAuthHandler?,
                    host: String?,
                    realm: String?
            ) {
                client?.onReceivedHttpAuthRequest(view, handler, host, realm)
                super.onReceivedHttpAuthRequest(view, handler, host, realm)
            }

            override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
            ) {
                client?.onReceivedSslError(view, handler, error)
                super.onReceivedSslError(view, handler, error)
            }

            override fun onTooManyRedirects(
                    view: WebView?,
                    cancelMsg: Message?,
                    continueMsg: Message?
            ) {
                client?.onTooManyRedirects(view, cancelMsg, continueMsg)
                super.onTooManyRedirects(view, cancelMsg, continueMsg)
            }

            override fun onFormResubmission(
                    view: WebView?,
                    dontResend: Message?,
                    resend: Message?
            ) {
                client?.onFormResubmission(view, dontResend, resend)
                super.onFormResubmission(view, dontResend, resend)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                client?.onLoadResource(view, url)
                super.onLoadResource(view, url)
            }
        })
    }

    override fun setWebViewClient(client: WebViewClient?) {
        //saving client reference for sending callbacks
        this.client = client
    }

}