package com.gamehub.two

import kotlin.system.exitProcess

class ShizukuShellService : IShizukuShell.Stub() {

    override fun run(command: String): String {
        val processCommand = when (command) {
            "wm density 600" -> listOf("wm", "density", "600")
            "wm density reset" -> listOf("wm", "density", "reset")
            else -> return "Erro: comando não permitido"
        }

        return try {
            val process = ProcessBuilder(processCommand)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().use { it.readText().trim() }
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                "OK"
            } else {
                val details = output.ifBlank { "sem detalhes" }
                "Erro ao executar comando (código $exitCode): $details"
            }
        } catch (error: Exception) {
            "Erro ao executar comando: ${error.message ?: error.javaClass.simpleName}"
        }
    }

    override fun destroy() {
        exitProcess(0)
    }
}
