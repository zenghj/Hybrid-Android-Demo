package com.example.julianzeng.webviewdemo;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Button btn_back;
    private Button btn_top;
    private Button btn_refresh;
    private TextView titleView;
    private Handler handler;

    private WebView webview;
    private long exitTime = 0;
    private String urlSchemaPrefix = "myhybrid://";

    class JavaToJS {
        @JavascriptInterface
        public long getExitTime() {
            return exitTime;
        }
        @JavascriptInterface
        public void makeToast(String text) {
            if(text != null) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new Handler();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }
    public void bindViews() {
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new JavaToJS(), "JAVA_TO_JS");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith(urlSchemaPrefix)) {
                    makeToast("hijack request: " + url);
                    return true; // returning true causes the current WebView to abort loading the URL
                } else {
                    makeToast("not hijack request: " + url);
                    return false; // returning false causes the WebView to continue loading the URL as usual.
                }
            }
        });


        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                if(message.startsWith(urlSchemaPrefix)) {
                    makeToast("hijack js prompt: " + message);
                    String cbReg = "cb=([^&]+)";
                    Pattern p = Pattern.compile(cbReg);
                    Matcher m = p.matcher(message);
                    String callbackName = "";
                    if(m.find()) {
                        callbackName = m.group().replace("cb=", "");
                        Log.i("callbackName", callbackName);
                        String jsStr = "javascript:";
                        jsStr += callbackName + "('From Native')";
                        webview.loadUrl(jsStr);
//                    webview.evaluateJavascript(jsStr, new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String value) {
//                            Log.i("ValueCallback", value);
//                        }
//                    });
                    }
                    result.cancel(); // 表示明确取消Prompt，否则系统认为Prompt窗口展示了，下次调用Prompt会无效。
                    //  If the client returns true, WebView will assume that the client will handle the prompt dialog
                    // and call the appropriate JsPromptResult method.
                    return true;
                } else {
                    makeToast("not hijack js prompt: " + message);
                    return false;
                }
            }
        });

        webview.loadUrl("file:///android_asset/demo.html");
    }
    public void makeToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}
