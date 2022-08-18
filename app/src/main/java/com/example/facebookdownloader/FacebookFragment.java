package com.example.facebookdownloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import javax.security.auth.callback.Callback;

public class FacebookFragment extends Fragment {

    private static String URL = "https://www.facebook.com/login/";
    private ProgressBar progress;
    WebView webo;
    Context context;
    public FacebookFragment() {}

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_facebook, container, false);
        context=getActivity();
        webo = (WebView) view.findViewById(R.id.webView);

        webo.setWebViewClient(new Callback());
        webo.loadUrl("http://www.facebook.com");
        webo.getSettings().setJavaScriptEnabled(true);

         return view;
    }
    private class Callback extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }

    }
}