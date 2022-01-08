package dev.jackrichard

import dev.jackrichard.kana.*

val vertexDescriptor = defineDescriptor {
    this vec2 "position"
    this vec4 "color"
}

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

            val a = vec4(1.0F, 0.5F, 0.8F, 1.0F)
            val b = vec2(1.0F, 0.5F) + vec2(1.0F, 0.2F)
            val c = 0.5F + vec2(1.0F, 0.5F)

            val m = mat4(
                1.0F, 0.5F, 0.75F, 0.25F,
                1.0F, 0.5F, 0.75F, 0.25F,
                1.0F, 0.5F, 0.75F, 0.25F,
                1.0F, 0.5F, 0.75F, 0.25F
            )
            val n = Mat4.identity * (Mat4.identity * 2F)
        }
    }
}