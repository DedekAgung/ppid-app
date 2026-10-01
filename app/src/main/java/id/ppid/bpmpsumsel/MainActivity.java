package id.ppid.bpmpsumsel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    // ==== UBAH DI SINI KALAU DOMAIN BERGANTI ====
    private static final String START_URL = "https://ppid-bpmpsumsel.id";
    private static final String HOST = "ppid-bpmpsumsel.id";
    // ============================================

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private long backPressedTime = 0;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setBuiltInZoomControls(false);
        s.setSupportZoom(false);
        s.setAllowFileAccess(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        s.setUserAgentString(s.getUserAgentString() + " PPIDApp/1.0");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }

        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl().toString());
            }

            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });

        swipeRefresh.setOnRefreshListener(() -> webView.reload());

        if (savedInstanceState == null) {
            if (isOnline()) {
                webView.loadUrl(START_URL);
            } else {
                webView.loadUrl("file:///android_asset/offline.html");
            }
        } else {
            webView.restoreState(savedInstanceState);
        }

        // Tombol back: navigasi WebView dulu, baru keluar
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    long now = System.currentTimeMillis();
                    if (now - backPressedTime < 2000) {
                        finish();
                    } else {
                        backPressedTime = now;
                        Toast.makeText(MainActivity.this,
                                "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /** true = ditangani (keluar WebView), false = biarkan WebView load sendiri */
    private boolean handleUrl(String url) {
        if (url.startsWith("http")) {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            if (host != null && (host.equals(HOST) || host.endsWith("." + HOST))) {
                return false; // domain sendiri -> muat di WebView
            }
            // domain luar -> buka di browser
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        }
        if (url.startsWith("mailto:") || url.startsWith("tel:") || url.startsWith("whatsapp:")
                || url.startsWith("intent:")) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception ignored) {
                Toast.makeText(this, "Aplikasi tidak tersedia", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    private boolean isOnline() {
        try {
            android.net.ConnectivityManager cm =
                    (android.net.ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            android.net.NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
}
