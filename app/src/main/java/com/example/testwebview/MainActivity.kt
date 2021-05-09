package com.example.testwebview

import android.graphics.Bitmap
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.webkit.*
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val BASE_URL = "https://google.com"
    private val SEARCH_PATH = "/search?q="
    private val KEY = "MY_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        functionsSearching()
        functionsButtons()
        functionsFonts()
    }

    private fun functionsFonts() {
        textBienvenido.typeface = Typeface.createFromAsset(assets, "fonts/StarJedi-DGRW.ttf")
        textSaludo.typeface = Typeface.createFromAsset(assets, "fonts/StarJediHollow-A4lL.ttf")
    }

    private fun functionsButtons() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        buttonGet.setOnClickListener {
            val myPref = prefs.getString(KEY, "NO existe")
            showAlert(myPref!!)
        }

        buttonPut.setOnClickListener {
            val editor = prefs.edit()
            editor.putString(KEY, "Insercion")
            editor.apply()
            showAlert("Valor Guardado")
        }

        buttonDelete.setOnClickListener {
            val editor = prefs.edit()
            editor.remove(KEY)
            editor.apply()
            showAlert("Valor Borrado")
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("My preferences")
        builder.setMessage(message)

        val dialog = builder.create()
        dialog.show()
    }

    private fun functionsSearching() {
        // Refresh
        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }

        // Search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (URLUtil.isValidUrl(it)) {
                        webView.loadUrl(it)
                    } else {
                        webView.loadUrl("$BASE_URL$SEARCH_PATH$it")
                    }
                }

                return false
            }
        })

        // Web View
        webView.webChromeClient = object : WebChromeClient() {

        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                searchView.setQuery(url, false)

                swipeRefresh.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                swipeRefresh.isRefreshing = false
            }
        }

        val settings = webView.settings
        settings.javaScriptEnabled = true

        webView.loadUrl(BASE_URL)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}