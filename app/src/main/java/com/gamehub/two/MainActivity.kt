package com.gamehub.two

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 30, 30, 30)
        }

        val overlay = Button(this).apply {
            text = "Permitir Overlay"
            setOnClickListener {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }

        val start = Button(this).apply {
            text = "Iniciar Game Hub"
            setOnClickListener {
                startService(Intent(this@MainActivity, OverlayService::class.java))
            }
        }

        layout.addView(overlay)
        layout.addView(start)

        setContentView(layout)
    }
}