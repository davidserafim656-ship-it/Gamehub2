package com.gamehub.two

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.UserServiceArgs

class MainActivity : Activity() {

    private var shell: IShizukuShell? = null
    private lateinit var status: TextView

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            shell = IShizukuShell.Stub.asInterface(binder)
            status.text = "Shizuku conectado"
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            shell = null
            status.text = "Shizuku desconectado"
        }
    }

    private val permissionListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == 100) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    connectShizuku()
                } else {
                    status.text = "Permissão do Shizuku negada"
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Shizuku.addRequestPermissionResultListener(permissionListener)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 50, 30, 30)
        }

        status = TextView(this).apply {
            text = "Inicie o Shizuku primeiro"
            textSize = 18f
        }

        val connect = Button(this).apply {
            text = "Conectar Shizuku"
            setOnClickListener {
                connectShizuku()
            }
        }

        val roblox600 = Button(this).apply {
            text = "Abrir Roblox em 600 DPI"

            setOnClickListener {
                val service = shell

                if (service == null) {
                    status.text = "Conecte o Shizuku primeiro"
                    return@setOnClickListener
                }

                val result = service.run("wm density 600")

                if (result.startsWith("Erro")) {
                    status.text = result
                    return@setOnClickListener
                }

                val intent =
                    packageManager.getLaunchIntentForPackage("com.roblox.client")

                if (intent != null) {
                    startActivity(intent)
                    status.text = "600 DPI aplicado"
                } else {
                    status.text = "Roblox não encontrado"
                }
            }
        }

        val restore = Button(this).apply {
            text = "Restaurar DPI normal"

            setOnClickListener {
                val service = shell

                if (service == null) {
                    status.text = "Conecte o Shizuku primeiro"
                    return@setOnClickListener
                }

                status.text = service.run("wm density reset")
            }
        }

        layout.addView(status)
        layout.addView(connect)
        layout.addView(roblox600)
        layout.addView(restore)

        setContentView(layout)
    }

    private fun connectShizuku() {
        if (!Shizuku.pingBinder()) {
            status.text = "Shizuku não está ativo"
            return
        }

        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(100)
            status.text = "Solicitando permissão..."
            return
        }

        val args =
            UserServiceArgs(
                ComponentName(this, ShizukuShellService::class.java)
            )
                .daemon(false)
                .processNameSuffix("shell")
                .debuggable(false)
                .version(1)

        Shizuku.bindUserService(args, connection)

        status.text = "Conectando..."
    }

    override fun onDestroy() {
        Shizuku.removeRequestPermissionResultListener(permissionListener)
        super.onDestroy()
    }
}
