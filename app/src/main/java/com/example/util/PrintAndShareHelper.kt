package com.example.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

object PrintAndShareHelper {

    // Retain active WebView until PrintManager picks up the PrintDocumentAdapter
    private var activePrintWebView: WebView? = null

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    fun copySummaryToClipboard(context: Context, summaryText: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("JSI 01/2026 Pay Summary", summaryText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "সামারি ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, "কপি করা যায়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyJsonToClipboard(context: Context, jsonText: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("JSI 01/2026 JSON Export", jsonText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "JSON ডাটা ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, "কপি করা যায়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun writeJsonToUri(context: Context, uri: android.net.Uri, jsonText: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(jsonText.toByteArray(Charsets.UTF_8))
                out.flush()
            } ?: return false
            true
        } catch (_: Exception) {
            false
        }
    }

    fun readTextFromUri(context: Context, uri: android.net.Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
        } catch (_: Exception) {
            null
        }
    }

    fun shareSummary(context: Context, summaryText: String) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "জেএসআই ০১/২০২৬ (JSI 01/2026) বেতন ও পেনশন সামারি")
                putExtra(Intent.EXTRA_TEXT, summaryText)
            }
            val chooser = Intent.createChooser(sendIntent, "সামারি শেয়ার করুন").apply {
                if (context.findActivity() == null) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(chooser)
        } catch (_: Exception) {
            copySummaryToClipboard(context, summaryText)
        }
    }

    fun printSummary(context: Context, summaryText: String) {
        val activity = context.findActivity() ?: context
        try {
            val webView = WebView(activity)
            activePrintWebView = webView

            val escaped = summaryText
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>")
            val html = """
                <!DOCTYPE html>
                <html lang="bn">
                <head>
                  <meta charset="utf-8" />
                  <style>
                    body { font-family: sans-serif; padding: 24px; color: #13201B; line-height: 1.65; }
                    .header { border-bottom: 2px solid #0E4D38; padding-bottom: 12px; margin-bottom: 16px; }
                    .title { font-size: 18px; font-weight: bold; color: #0E4D38; }
                    .sub { font-size: 12px; color: #4A5D55; }
                    .content { font-size: 13px; white-space: normal; background: #F7FAF8; padding: 16px; border-radius: 8px; border: 1px solid #C5D2CB; }
                  </style>
                </head>
                <body>
                  <div class="header">
                    <div class="title">জেএসআই ০১/২০২৬ (JSI 01/2026) সশস্ত্র বাহিনী বেতন ও পেনশন ক্যালকুলেটর</div>
                    <div class="sub">গণপ্রজাতন্ত্রী বাংলাদেশ সরকার • প্রতিরক্ষা মন্ত্রণালয় • গেজেট ১৫ নভেম্বর ২০২৬</div>
                  </div>
                  <div class="content">$escaped</div>
                </body>
                </html>
            """.trimIndent()

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                        val printManager = activity.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                        if (printManager != null) {
                            val printAdapter = webView.createPrintDocumentAdapter("JSI_01_2026_Summary")
                            printManager.print(
                                "JSI 01/2026 Pay & Pension Summary",
                                printAdapter,
                                PrintAttributes.Builder().build()
                            )
                        } else {
                            copySummaryToClipboard(activity, summaryText)
                        }
                    } catch (_: Exception) {
                        copySummaryToClipboard(activity, summaryText)
                    } finally {
                        activePrintWebView = null
                    }
                }
            }
            webView.loadDataWithBaseURL("about:blank", html, "text/html", "UTF-8", null)
        } catch (_: Exception) {
            activePrintWebView = null
            copySummaryToClipboard(activity, summaryText)
        }
    }
}
