package dev.jackrichard.konangraphics

interface KGLDelegate {
    fun onInitialized()
    fun onScreenResizes(size: Pair<Int, Int>)
    fun onDrawFrame(controller: KGLContext)
}

expect class KGLView