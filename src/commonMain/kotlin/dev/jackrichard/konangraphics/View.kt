package dev.jackrichard.konangraphics

interface KGLRenderer {
    fun onInitialized()
    fun onScreenSized(size: Pair<Int, Int>)
    fun onDrawFrame(controller: KGLContext)
}

expect open class KGLView()