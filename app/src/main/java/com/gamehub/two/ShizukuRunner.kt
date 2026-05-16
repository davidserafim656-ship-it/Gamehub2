package com.gamehub.two

import rikka.shizuku.Shizuku

object ShizukuRunner {
    fun run(cmd: String) {
        try {
            Shizuku.newProcess(arrayOf("sh", "-c", cmd), null, null)
        } catch (_: Exception) {}
    }
}