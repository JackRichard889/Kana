package dev.jackrichard.blah

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.jackrichard.kana.*

val vertexDescriptor = defineDescriptor {
    this vec2 "position"
}

class FirstView : KanaRenderer {
    private lateinit var pipeline: KanaPipeline
    private val vertices = floatArrayOf(0.0F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F).buffered()

    override fun onInitialized() {
        pipeline = KanaPipeline.initNew()

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

        pipeline.setVertexFunction(vertexFunctioniOS to vertexFunctionAnd)
        pipeline.setFragmentFunction(fragmentFunctioniOS to fragmentFunctionAnd)
        pipeline.setVertexDescriptor(vertexDescriptor)
    }

    override fun onScreenSized(size: Pair<Int, Int>) { }

    override fun onDrawFrame(context: KanaContext) {
        context.queueUp {
            linkPipeline(pipeline)
            sendBuffer(vertices)
            drawPrimitives(0, 3)
        }
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KanaView(ctx = this, renderer = FirstView()).also { setContentView(it) }
    }
}