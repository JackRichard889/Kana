package dev.jackrichard.kana

interface KanaRenderer {
    var clearColor: KanaColor

    fun onScreenSized(size: Pair<Int, Int>) { }
    fun onDrawFrame(context: KanaContext)
}

expect class KanaView
expect object KanaBuilder