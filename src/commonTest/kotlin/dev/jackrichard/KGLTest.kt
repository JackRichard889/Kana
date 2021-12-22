package dev.jackrichard

import dev.jackrichard.konangraphics.*

class FirstView : KGLRenderer {
    override fun onInitialized() {
        val pipeline = KGLPipeline.initNew()
        val vertexFunctioniOS = KGLShader.compileShader(platform = KGLPlatform.IOS, type = KGLShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctioniOS = KGLShader.compileShader(platform = KGLPlatform.IOS, type = KGLShaderType.FRAGMENT, name = "fragment_main", "")
        val vertexFunctionAnd = KGLShader.compileShader(platform = KGLPlatform.ANDROID, type = KGLShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctionAnd = KGLShader.compileShader(platform = KGLPlatform.ANDROID, type = KGLShaderType.FRAGMENT, name = "fragment_main", "")

        pipeline.setVertexFunction(vertexFunctioniOS)
        pipeline.setFragmentFunction(fragmentFunctioniOS)
        pipeline.setVertexFunction(vertexFunctionAnd)
        pipeline.setFragmentFunction(fragmentFunctionAnd)
    }

    override fun onScreenSized(size: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    override fun onDrawFrame(controller: KGLContext) {

    }
}