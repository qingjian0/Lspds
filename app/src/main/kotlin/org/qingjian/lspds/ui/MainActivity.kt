package org.qingjian.lspds.ui

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val density = resources.displayMetrics.density
        val padding = (24 * density).toInt()
        setContentView(TextView(this).apply {
            text = """
                Lspds 0.1.0-dev

                Enable this module for com.deepseek.chat in LSPosed.

                Current mode: observation-only.
                No chat content or credentials are collected.
            """.trimIndent()
            textSize = 16f
            setPadding(padding, padding, padding, padding)
        })
    }
}
