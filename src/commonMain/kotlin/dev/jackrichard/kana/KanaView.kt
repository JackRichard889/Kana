package dev.jackrichard.kana

interface KanaRenderer {
    fun onInitialized()
    fun onScreenSized(size: Pair<Int, Int>)
    fun onDrawFrame(context: KanaContext)
}

expect open class KanaView