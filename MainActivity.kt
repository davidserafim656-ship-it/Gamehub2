package com.gamehub.two

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.*
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.UserServiceArgs

class MainActivity : Activity() {

    private var shell: IShizukuShell? = null
    private lateinit var status: TextView

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            shell = IShizukuShell.Stub.asInterface(binder)
            status.text = "Shizuku conectado ✅"
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            shell = null
            status.text = "Shizuku desconectado"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 50, 30, 30)
        }

        status = TextView(this).apply {
            text = "Game Hub 2.1\nInicie o Shizuku primeiro."
            textSize = 18f
        }

        fun button(text: String, cmd: String): Button {
            return Button(this).apply {
                this.text = text
                setOnClickListener {
                    runCommand(cmd)
                }
            }
        }

        val perm = Button(this).apply {
            text = "Conectar Shizuku"
            setOnClickListener {
                connectShizuku()
            }
        }

        layout.addView(status)
        layout.addView(perm)

        layout.addView(button("Abrir Roblox em 600 DPI",
    "wm density 600; monkey -p com.roblox.client 1"
))

        layout.addView(button("Restaurar DPI normal",
    "wm density reset"
))

        layout.addView(button("Abrir Roblox", 
            "monkey -p com.roblox.client 1"
        ))

        layout.addView(button("Fechar Roblox", 
            "am force-stop com.roblox.client"
        ))

        layout.addView(button("Ver RAM Roblox", 
            "dumpsys meminfo com.roblox.client | head -n 25"
        ))

        setContentView(layout)
    }

    private fun connectShizuku() {
        if (!Shizuku.pingBinder()) {
            status.text = "Shizuku não está ativo."
            return
        }

        if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(100)
            status.text = "Permissão Shizuku solicitada."
            return
        }

        val args = UserServiceArgs(ComponentName(this, ShizukuShellService::class.java))
            .daemon(false)
            .processNameSuffix("shell")
            .debuggable(false)
            .version(1)

        Shizuku.bindUserService(args, connection)
        status.text = "Conectando ao Shizuku..."
    }

    private fun runCommand(cmd: String) {
        val result = shell?.run(cmd)
        status.text = result ?: "Clique em Conectar Shizuku primeiro."
    }
}