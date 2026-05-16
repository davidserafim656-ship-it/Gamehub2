package com.gamehub.two

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class OverlayService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }

        val title = TextView(this).apply {
            text = "Game Hub 2.0"
        }

        val lowRes = Button(this).apply {
            text = "Baixar resolução"
            setOnClickListener {
                ShizukuRunner.run(Commands.LOW_RES)
            }
        }

        val normalRes = Button(this).apply {
            text = "Normal"
            setOnClickListener {
                ShizukuRunner.run(Commands.NORMAL_RES)
            }
        }

        val roblox = Button(this).apply {
            text = "Abrir Roblox"
            setOnClickListener {
                ShizukuRunner.run(Commands.OPEN_ROBLOX)
            }
        }

        layout.addView(title)
        layout.addView(lowRes)
        layout.addView(normalRes)
        layout.addView(roblox)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.END

        wm.addView(layout, params)
    }
}