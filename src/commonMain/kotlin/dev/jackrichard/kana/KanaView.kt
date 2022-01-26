package dev.jackrichard.kana

interface KanaRenderer {
    fun onScreenSized(size: Pair<Int, Int>) { }
    fun onDrawFrame(context: KanaContext)
}

expect class KanaView
expect object KanaBuilder