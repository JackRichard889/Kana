package dev.jackrichard

import dev.jackrichard.kana.*

class FirstView : KanaRenderer {
    private val pipeline: KanaPipeline = KanaPipeline.create {
        val vertexFunctioniOS = KanaShader.compileShader(platform = KanaPlatform.IOS, type = KanaShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctioniOS = KanaShader.compileShader(platform = KanaPlatform.IOS, type = KanaShaderType.FRAGMENT, name = "fragment_main", "")
        val vertexFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.FRAGMENT, name = "fragment_main", "")

        vertexShader = vertexFunctionAnd to vertexFunctioniOS
        fragmentShader = fragmentFunctionAnd to fragmentFunctioniOS
        vertexDescriptor = defineDescriptor {
            this vec2 "position"
            this vec4 "color"
        }
    }

    private val vertices = floatArrayOf(0.0F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F).buffered()

    override fun onDrawFrame(context: KanaContext) {
        context.queueUp {
            linkPipeline(pipeline)
            sendBuffer(vertices)
            drawPrimitives(0, 3)
        }
    }
}