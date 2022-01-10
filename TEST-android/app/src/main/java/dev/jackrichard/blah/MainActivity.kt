package dev.jackrichard.blah

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        val vertexFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.VERTEX, name = "vertex_main",
            "attribute vec2 position;\n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = vec4(position, 0.0, 1.0);\n" +
                "}"
        )
        val fragmentFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.FRAGMENT, name = "fragment_main",
            "void main()\n" +
                "{\n" +
                "     gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);\n" +
                "}"
        )

        pipeline.setVertexFunction(vertexFunctioniOS)
        pipeline.setFragmentFunction(fragmentFunctioniOS)
        pipeline.setVertexFunction(vertexFunctionAnd)
        pipeline.setFragmentFunction(fragmentFunctionAnd)

        pipeline.setVertexDescriptor(vertexDescriptor)
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

            Mat4.identity.translate(2 v 3 v 2)
            m.scale(2 v 2 v 2).scale(3 v 3 v 3)
        }
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = KanaView(ctx = this, renderer = FirstView())
        setContentView(view.glView)
    }
}