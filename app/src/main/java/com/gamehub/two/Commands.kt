package com.gamehub.two

object Commands {
    const val LOW_RES = "wm size 720x1600 && wm density 240"
    const val NORMAL_RES = "wm size reset && wm density reset"
    const val OPEN_ROBLOX = "monkey -p com.roblox.client 1"
    const val CLOSE_ROBLOX = "am force-stop com.roblox.client"
}