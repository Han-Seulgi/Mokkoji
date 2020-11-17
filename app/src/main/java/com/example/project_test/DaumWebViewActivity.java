package com.example.project_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DaumWebViewActivity extends AppCompatActivity {
    private WebView daum_webView;
    private TextView daum_result;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_web_view);

        daum_result = findViewById(R.id.daum_result);

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();

        // WebView 초기화
        init_webView();
    }


    public void init_webView() {

        // WebView 설정
        daum_webView = (WebView) findViewById(R.id.daum_webview);

        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new AndroidBridge(), "TestApp");

        // web client를 chrome 으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient());

        // webview url load. php 파일 주소
        daum_webView.loadUrl("http://project-lzbnp.run.goorm.io/daum_address.php");
    }


    private class AndroidBridge {

        @JavascriptInterface

        public void setAddress(final String arg1, final String arg2, final String arg3) {

            handler.post(new Runnable() {

                @Override
                public void run() {
                    daum_result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));

                    int request = getIntent().getIntExtra("request", -1);
                    Log.e("daum", String.valueOf(request));
                    switch (request){
                        case 999:
                            Intent intent = new Intent();
                            intent.putExtra("주소", arg2+arg3);
                            setResult(RESULT_OK,intent);
                            finish();
                            Log.e("daum", "죽");
                            break;
                        case 888:
                            Intent intent1 = new Intent();
                            intent1.putExtra("주소", arg2+arg3);
                            setResult(RESULT_OK,intent1);
                            finish();
                            Log.e("daum", "음");
                            break;
                    }


//                    Intent intent2 = new Intent(DaumWebViewActivity.this, RoomActivity.class);
//                    intent2.putExtra("주소", arg1+arg2+arg3);
//                    intent2.putExtra("체크", 1);
//                    setResult(RESULT_OK,intent2);
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();
                }

            });

        }

    }
}
