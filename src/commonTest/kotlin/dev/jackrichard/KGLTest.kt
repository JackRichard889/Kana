package dev.jackrichard

import dev.jackrichard.konangraphics.*

@KGLMainView
class TestView : KGLDelegate {
    override fun onInitialized() {
        TODO("Not yet implemented")
    }

    override fun onScreenResizes(size: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    override fun onDrawFrame(controller: KGLContext) {
        // TODO: move to initialized area
        controller.compileShader {
            source = ""
            type = KGLShaderType.VERTEX
            platform = KGLPlatform.IOS
        }
    }
}