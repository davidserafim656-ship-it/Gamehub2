package com.gamehub.two

object ShizukuRunner {

    fun run(cmd: String) {
        try {
            Runtime.getRuntime().exec(
                arrayOf("sh", "-c", cmd)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}