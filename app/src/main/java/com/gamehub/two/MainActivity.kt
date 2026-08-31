package com.gamehub.two

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import rikka.shizuku.Shizuku
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private lateinit var status: TextView
    private lateinit var openRobloxButton: Button
    private lateinit var restoreDensityButton: Button

    private val commandExecutor = Executors.newSingleThreadExecutor()
    private var shell: IShizukuShell? = null
    private var bindingUserService = false
    private var userServiceBound = false

    private val userServiceArgs by lazy {
        Shizuku.UserServiceArgs(ComponentName(this, ShizukuShellService::class.java))
            .daemon(false)
            .processNameSuffix("shell")
            .debuggable(false)
            .version(1)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            bindingUserService = false
            shell = binder?.let(IShizukuShell.Stub::asInterface)
            userServiceBound = shell != null
            showStatus(
                if (userServiceBound) "Shizuku conectado" else "Shizuku desconectado"
            )
            updateCommandButtons()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bindingUserService = false
            userServiceBound = false
            shell = null
            showStatus("Shizuku desconectado")
            updateCommandButtons()
        }
    }

    private val permissionListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode != SHIZUKU_PERMISSION_REQUEST_CODE) return@OnRequestPermissionResultListener

            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                bindShizukuUserService()
            } else {
                showStatus("Shizuku sem permissão")
                updateCommandButtons()
            }
        }

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        runOnUiThread { refreshShizukuStatus() }
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        runOnUiThread {
            bindingUserService = false
            userServiceBound = false
            shell = null
            showStatus("Shizuku desconectado")
            updateCommandButtons()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createInterface()

        Shizuku.addRequestPermissionResultListener(permissionListener)
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
    }

    override fun onResume() {
        super.onResume()
        refreshShizukuStatus()
    }

    private fun createInterface() {
        val density = resources.displayMetrics.density
        val horizontalPadding = (24 * density).toInt()
        val verticalPadding = (32 * density).toInt()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
        }

        val title = TextView(this).apply {
            text = "Roblox DPI"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "Shizuku desconectado"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, (20 * density).toInt(), 0, (20 * density).toInt())
        }

        val connectButton = Button(this).apply {
            text = "Conectar Shizuku"
            setOnClickListener { connectShizuku() }
        }

        openRobloxButton = Button(this).apply {
            text = "Abrir Roblox em 600 DPI"
            isEnabled = false
            setOnClickListener { openRobloxAt600Dpi() }
        }

        restoreDensityButton = Button(this).apply {
            text = "Restaurar DPI normal"
            isEnabled = false
            setOnClickListener { restoreNormalDensity() }
        }

        layout.addView(title, matchWidthLayoutParams())
        layout.addView(status, matchWidthLayoutParams())
        layout.addView(connectButton, matchWidthLayoutParams())
        layout.addView(openRobloxButton, matchWidthLayoutParams())
        layout.addView(restoreDensityButton, matchWidthLayoutParams())

        setContentView(layout)
    }

    private fun matchWidthLayoutParams() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    private fun connectShizuku() {
        if (!isShizukuBinderAlive()) return

        if (Shizuku.isPreV11()) {
            showStatus("Versão do Shizuku não suportada")
            return
        }

        val permission = try {
            Shizuku.checkSelfPermission()
        } catch (error: RuntimeException) {
            showStatus("Erro ao verificar Shizuku: ${error.readableMessage()}")
            return
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            showStatus("Shizuku sem permissão")

            if (Shizuku.shouldShowRequestPermissionRationale()) {
                showStatus("Shizuku sem permissão. Autorize o app no Shizuku.")
                return
            }

            try {
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
            } catch (error: RuntimeException) {
                showStatus("Erro ao solicitar permissão: ${error.readableMessage()}")
            }
            return
        }

        bindShizukuUserService()
    }

    private fun bindShizukuUserService() {
        if (!isShizukuBinderAlive()) return

        if (shell != null) {
            showStatus("Shizuku conectado")
            updateCommandButtons()
            return
        }

        if (bindingUserService) return

        try {
            bindingUserService = true
            showStatus("Conectando ao Shizuku...")
            Shizuku.bindUserService(userServiceArgs, connection)
        } catch (error: RuntimeException) {
            bindingUserService = false
            showStatus("Erro ao conectar: ${error.readableMessage()}")
            updateCommandButtons()
        }
    }

    private fun refreshShizukuStatus() {
        if (!Shizuku.pingBinder()) {
            showStatus("Shizuku desconectado")
            updateCommandButtons()
            return
        }

        val permissionGranted = try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (_: RuntimeException) {
            false
        }

        when {
            !permissionGranted -> showStatus("Shizuku sem permissão")
            shell != null -> showStatus("Shizuku conectado")
            !bindingUserService -> showStatus("Shizuku desconectado")
        }
        updateCommandButtons()
    }

    private fun openRobloxAt600Dpi() {
        val launchIntent = packageManager.getLaunchIntentForPackage(ROBLOX_PACKAGE)
        if (launchIntent == null) {
            showStatus("Roblox não está instalado")
            return
        }

        runShizukuCommand(
            command = COMMAND_DENSITY_600,
            progressMessage = "Aplicando 600 DPI..."
        ) {
            try {
                startActivity(launchIntent)
                showStatus("600 DPI aplicada. Roblox aberto.")
            } catch (_: ActivityNotFoundException) {
                showStatus("600 DPI aplicada, mas não foi possível abrir o Roblox")
            }
        }
    }

    private fun restoreNormalDensity() {
        runShizukuCommand(
            command = COMMAND_DENSITY_RESET,
            progressMessage = "Restaurando DPI normal..."
        ) {
            showStatus("DPI normal restaurada")
        }
    }

    private fun runShizukuCommand(
        command: String,
        progressMessage: String,
        onSuccess: () -> Unit
    ) {
        if (!isShizukuBinderAlive()) return

        val service = shell
        if (service == null) {
            showStatus("Shizuku desconectado. Toque em Conectar Shizuku.")
            updateCommandButtons()
            return
        }

        setCommandButtonsEnabled(false)
        showStatus(progressMessage)

        commandExecutor.execute {
            val result = try {
                service.run(command)
            } catch (error: Exception) {
                "Erro ao executar comando: ${error.readableMessage()}"
            }

            runOnUiThread {
                updateCommandButtons()
                if (result == COMMAND_OK) {
                    onSuccess()
                } else {
                    showStatus(result)
                }
            }
        }
    }

    private fun isShizukuBinderAlive(): Boolean {
        if (Shizuku.pingBinder()) return true

        bindingUserService = false
        userServiceBound = false
        shell = null
        showStatus("Shizuku desconectado. Inicie o Shizuku primeiro.")
        updateCommandButtons()
        return false
    }

    private fun updateCommandButtons() {
        setCommandButtonsEnabled(shell != null && Shizuku.pingBinder())
    }

    private fun setCommandButtonsEnabled(enabled: Boolean) {
        openRobloxButton.isEnabled = enabled
        restoreDensityButton.isEnabled = enabled
    }

    private fun showStatus(message: String) {
        status.text = message
    }

    private fun Throwable.readableMessage(): String = message ?: javaClass.simpleName

    override fun onDestroy() {
        Shizuku.removeRequestPermissionResultListener(permissionListener)
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)

        if (bindingUserService || userServiceBound) {
            try {
                Shizuku.unbindUserService(userServiceArgs, connection, true)
            } catch (_: RuntimeException) {
                // O binder pode ter morrido antes da Activity.
            }
        }

        shell = null
        commandExecutor.shutdownNow()
        super.onDestroy()
    }

    companion object {
        private const val SHIZUKU_PERMISSION_REQUEST_CODE = 100
        private const val ROBLOX_PACKAGE = "com.roblox.client"
        private const val COMMAND_DENSITY_600 = "wm density 600"
        private const val COMMAND_DENSITY_RESET = "wm density reset"
        private const val COMMAND_OK = "OK"
    }
}
