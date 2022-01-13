package dev.jackrichard

import dev.jackrichard.kana.*

val vertexDescriptor = defineDescriptor {
    this vec2 "position"
    this vec4 "color"
}

class FirstView : KanaRenderer {
    private val pipeline = KanaPipeline.initNew()
    private val vertices = floatArrayOf(0.0F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F).buffered()

    override fun onInitialized() {
        val vertexFunctioniOS = KanaShader.compileShader(platform = KanaPlatform.IOS, type = KanaShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctioniOS = KanaShader.compileShader(platform = KanaPlatform.IOS, type = KanaShaderType.FRAGMENT, name = "fragment_main", "")
        val vertexFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.FRAGMENT, name = "fragment_main", "")

        pipeline.setVertexFunction(vertexFunctioniOS to vertexFunctionAnd)
        pipeline.setFragmentFunction(fragmentFunctioniOS to fragmentFunctionAnd)

        pipeline.setVertexDescriptor(vertexDescriptor)
    }

    override fun onScreenSized(size: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    override fun onDrawFrame(context: KanaContext) {
        context.queueUp {
            linkPipeline(pipeline)
            sendBuffer(vertices)
            drawPrimitives(0, 3)
        }
    }
}