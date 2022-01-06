package dev.jackrichard

import dev.jackrichard.kana.*

class FirstView : KanaRenderer {
    private val pipeline = KanaPipeline.initNew()

    override fun onInitialized() {
        val vertexFunctioniOS = KanaShader.compileShader(platform = KanaPlatform.IOS, type = KanaShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctioniOS = KanaShader.compileShader(platform = KanaPlatform.IOS, type = KanaShaderType.FRAGMENT, name = "fragment_main", "")
        val vertexFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.FRAGMENT, name = "fragment_main", "")

        pipeline.setVertexFunction(vertexFunctioniOS)
        pipeline.setFragmentFunction(fragmentFunctioniOS)
        pipeline.setVertexFunction(vertexFunctionAnd)
        pipeline.setFragmentFunction(fragmentFunctionAnd)
    }

    override fun onScreenSized(size: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    override fun onDrawFrame(context: KanaContext) {
        context.queueUp {
            linkPipeline(pipeline)
        }
    }
}