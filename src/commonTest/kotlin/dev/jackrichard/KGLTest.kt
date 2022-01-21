package dev.jackrichard

import dev.jackrichard.kana.*

class FirstView : KanaRenderer {
    private var pipeline: KanaPipeline? = null
    private val vertices = floatArrayOf(0.0F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F).buffered()

    override fun onInitialized() {
        pipeline = KanaPipeline.create {
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
    }

    override fun onScreenSized(size: Pair<Int, Int>) {

    }

    override fun onDrawFrame(context: KanaContext) {
        context.queueUp {
            linkPipeline(pipeline!!)
            sendBuffer(vertices)
            drawPrimitives(0, 3)
        }
    }
}