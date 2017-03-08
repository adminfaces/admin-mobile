package com.adminfaces.github.admin_app;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.adminfaces.github.admin_app.R;

public class MainActivity extends AppCompatActivity {

    private static ViewGroup webViewParentViewGroup = null;
    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (webView != null) {
            webViewParentViewGroup.removeView(webView);

            setContentView(R.layout.activity_main_no_webview);

            webViewParentViewGroup = (ViewGroup) findViewById(R.id.secondViewGroup);
            webViewParentViewGroup.addView(this.webView);
        } else {
            setContentView(R.layout.activity_main);

            webViewParentViewGroup = (ViewGroup) findViewById(R.id.firstViewGroup);
            webView = (WebView) findViewById(R.id.webview);

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);

            AdminWebViewClient webViewClient = new AdminWebViewClient(this);
            webView.setWebViewClient(webViewClient);
            webView.loadUrl("http://adminfaces-rpestano.rhcloud.com/showcase/");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
