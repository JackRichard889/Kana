package dev.jackrichard.konangraphics

annotation class KGLMainView

interface KGLDelegate {
    fun onInitialized()
    fun onScreenResizes(size: Pair<Int, Int>)
    fun onDrawFrame(controller: KGLContext)
}

expect class KGLView