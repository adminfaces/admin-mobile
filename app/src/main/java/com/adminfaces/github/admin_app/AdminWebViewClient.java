package com.adminfaces.github.admin_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by rafael-pestano on 08/03/17.
 */

public class AdminWebViewClient extends WebViewClient {

    private MainActivity activity = null;

    private UrlCache urlCache = null;


    public AdminWebViewClient(MainActivity activity) {
        this.activity = activity;
        this.urlCache = new UrlCache(activity);
        /*this.urlCache.register("adminfaces-rpestano.rhcloud.com/showcase/index.xhtml",
                "application/xhtml+xml", "UTF-8", 10 * UrlCache.ONE_MINUTE);*/
    }

    /**
     * open external links into webview instead of device browser
     * @param webView
     * @param url
     * @return true if its an external link and false otherwise
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if (url.indexOf(activity.getContext()) > -1) {
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        //pre fetching (put some pages into cache on app startup, pages below should load faster
         if((activity.getContext()+"/index.xhtml").equals(url)){
            this.urlCache.register(activity.getContext()+"/pages/components/messages.xhtml",
                    "application/xhtml+xml", "UTF-8", 30 * UrlCache.ONE_MINUTE);
            this.urlCache.load(activity.getContext()+"/pages/components/messages.xhtml");
            Log.d(getClass().getName(), "Pre fetching finished successfully!");
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return this.urlCache.load(url);
    }






}
