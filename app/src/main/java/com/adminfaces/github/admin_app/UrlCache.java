package com.adminfaces.github.admin_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by rafael-pestano on 08/03/17.
 */

public class UrlCache {

    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60L * ONE_SECOND;
    public static final long ONE_HOUR = 60L * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;

    private static class CacheEntry {
        public String url;
        public String fileName;
        public String mimeType;
        public String encoding;
        public long maxAgeMillis;

        private CacheEntry(String url, String fileName,
                           String mimeType, String encoding, long maxAgeMillis) {

            this.url = url;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.encoding = encoding;
            this.maxAgeMillis = maxAgeMillis;
        }
    }


    protected Map<String, CacheEntry> cacheEntries = new HashMap<String, CacheEntry>();
    protected Activity activity = null;
    protected File rootDir = null;


    public UrlCache(Activity activity) {
        this.activity = activity;
        this.rootDir = this.activity.getFilesDir();
    }

    public UrlCache(Activity activity, File rootDir) {
        this.activity = activity;
        this.rootDir = rootDir;
    }


    public void register(String url,
                         String mimeType, String encoding,
                         long maxAgeMillis) {

        String cacheFileName = url.substring(url.lastIndexOf("/")+1, url.length());
        CacheEntry entry = new CacheEntry(url, cacheFileName, mimeType, encoding, maxAgeMillis);

        this.cacheEntries.put(url, entry);
    }


    public WebResourceResponse load(String url) {
        WebResourceResponse webResourceResponse = lookIntoAssets(url);
        if (webResourceResponse != null) {
            return webResourceResponse;
        }

        CacheEntry cacheEntry = this.cacheEntries.get(url);

        if (cacheEntry == null) {
            return null;//null means load the original resource instead of cached one
        }

        File cachedFile = new File(this.rootDir.getPath() + File.separator + cacheEntry.fileName);

        if (cachedFile.exists()) {
            long cacheEntryAge = System.currentTimeMillis() - cachedFile.lastModified();
            if (cacheEntryAge > cacheEntry.maxAgeMillis) {
                cachedFile.delete();

                //cached file deleted, call load() again.
                Log.d(getClass().getName(), "Deleting from cache: " + url);
                return load(url);
            }

            //cached file exists and is not too old. Return file.
            Log.d(getClass().getName(), "Loading from cache: " + url);
            try {
                return new WebResourceResponse(
                        cacheEntry.mimeType, cacheEntry.encoding, new FileInputStream(cachedFile));
            } catch (FileNotFoundException e) {
                Log.d(getClass().getName(), "Error loading cached file: " + cachedFile.getPath() + " : "
                        + e.getMessage(), e);
            }

        } else {
            try {
                downloadAndStore(url, cacheEntry);

                //now the file exists in the cache, so we can just call this method again to read it.
                return load(url);
            } catch (Exception e) {
                Log.d(getClass().getName(), "Error reading file over network: " + cachedFile.getPath(), e);
            }
        }

        return null;
    }

    private WebResourceResponse lookIntoAssets(String url) {
        if (url.contains("showcase.css")) {
            return loadFromAssets(url, "css/showcase.css", "text/css", "");
        }
        //TODO more resources

        return null;

    }


    private void downloadAndStore(String url, CacheEntry cacheEntry)
            throws IOException {

        InputStream urlInput = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL urlObj = new URL(url);
            URLConnection urlConnection = urlObj.openConnection();
            urlInput = urlConnection.getInputStream();

            fileOutputStream =
                    this.activity.openFileOutput(cacheEntry.fileName, Context.MODE_PRIVATE);

            int data = urlInput.read();
            while (data != -1) {
                fileOutputStream.write(data);

                data = urlInput.read();
            }
            Log.d(getClass().getName(), "Cache file: " + cacheEntry.fileName + " stored. ");
        }finally {
            if (urlInput != null) {
                urlInput.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    private WebResourceResponse loadFromAssets(String url,
                                               String assetPath, String mimeType, String encoding) {

        AssetManager assetManager = this.activity.getAssets();
        InputStream input = null;
        try {
            input = assetManager.open(assetPath);
            WebResourceResponse response =
                    new WebResourceResponse(mimeType, encoding, input);

            return response;
        } catch (IOException e) {
            Log.e("WEB-APP", "Error loading " + assetPath + " from assets: " +
                    e.getMessage(), e);
        }
        return null;
    }

    private class Download extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            return "Executed!";

        }


    }


}
