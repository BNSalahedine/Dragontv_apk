package com.dragontv.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

public class MainActivity extends Activity {

    private static final String SITE_URL = "https://bnsalahedine.github.io/Dragontv/";
    private static final String START_IO_APP_ID = "210584082";
    // true = test ads (safe while testing). Set to false for the published version.
    private static final boolean TEST_ADS = true;

    private WebView web;
    private View customView;
    private WebChromeClient.CustomViewCallback customCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start.io must be initialised before the layout (which contains the banner) is inflated.
        StartAppSDK.setTestAdsEnabled(TEST_ADS);
        StartAppSDK.initParams(this, START_IO_APP_ID)
                .setReturnAdsEnabled(false)
                .init();

        setContentView(R.layout.activity_main);

        web = findViewById(R.id.web);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);

        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                customView = view;
                customCallback = callback;
                view.setBackgroundColor(Color.BLACK);
                ((FrameLayout) getWindow().getDecorView()).addView(view,
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                hideSystemBars(true);
            }

            @Override
            public void onHideCustomView() {
                if (customView == null) return;
                ((FrameLayout) getWindow().getDecorView()).removeView(customView);
                customView = null;
                if (customCallback != null) customCallback.onCustomViewHidden();
                customCallback = null;
                hideSystemBars(false);
            }
        });

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl(SITE_URL);
        }
    }

    @SuppressWarnings("deprecation")
    private void hideSystemBars(boolean hide) {
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(hide
                ? View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                : View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (customView != null) {
            web.getWebChromeClient().onHideCustomView();
        } else if (web.canGoBack()) {
            web.goBack();
        } else {
            // Interstitial when leaving the app. Delete this line to remove it.
            StartAppAd.showAd(this);
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    protected void onDestroy() {
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
