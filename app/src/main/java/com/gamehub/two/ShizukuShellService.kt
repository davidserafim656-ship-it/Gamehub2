package com.gamehub.two

import java.io.BufferedReader
import java.io.InputStreamReader

class ShizukuShellService : IShizukuShell.Stub() {
    override fun run(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val out = BufferedReader(InputStreamReader(process.inputStream)).readText()
            val err = BufferedReader(InputStreamReader(process.errorStream)).readText()
            process.waitFor()
            if (err.isNotBlank()) err else out.ifBlank { "OK" }
        } catch (e: Exception) {
            "Erro: ${e.message}"
        }
    }
}