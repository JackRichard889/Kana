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

    private val vertices = floatArrayOf(-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f).buffered()
    private val order = shortArrayOf(0, 1, 2, 0, 2, 3).buffered()

    private val texture = KanaTexture.make("test", "png")

    override fun onDrawFrame(context: KanaContext) {
        context.queueUp(pipeline) {
            sendBuffer(vertices)
            drawPrimitives(0, 3, order = order)
        }
    }
}