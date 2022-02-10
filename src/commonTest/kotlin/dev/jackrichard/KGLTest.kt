package dev.jackrichard

import dev.jackrichard.kana.*

class FirstView : KanaRenderer {
    private object FirstViewUniforms : KanaUniforms() {
        val projectionMatrix = mat4("projection")
        val viewModelMatrix = mat4("viewModel")
    }

    private val pipeline: KanaPipeline = KanaPipeline.create {
        val vertexFunctioniOS = KanaShader.compileShader(platform = KanaPlatform.IOS, type = KanaShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctioniOS = KanaShader.compileShader(platform = KanaPlatform.IOS, type = KanaShaderType.FRAGMENT, name = "fragment_main", "")
        val vertexFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.VERTEX, name = "vertex_main", "")
        val fragmentFunctionAnd = KanaShader.compileShader(platform = KanaPlatform.ANDROID, type = KanaShaderType.FRAGMENT, name = "fragment_main", "")

        vertexShader = vertexFunctionAnd to vertexFunctioniOS
        fragmentShader = fragmentFunctionAnd to fragmentFunctioniOS
        vertexDescriptor = vertexDescriptor {
            this vec2 "position"
            this vec4 "color"
        }
    }

    private val vertices = floatArrayOf(-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f).buffered()
    private val order = shortArrayOf(0, 1, 2, 0, 2, 3).buffered()

    private val texture = KanaTexture.make("test", "png") {
        minFilter = KanaTextureOptions.KanaTextureParameter.LINEAR
        magFilter = KanaTextureOptions.KanaTextureParameter.LINEAR
    }

    private var viewModelMatrix: Mat4 = Mat4.identity
    private var projectionMatrix: Mat4 = Mat4.identity

    override var clearColor: KanaColor = color(1.0, 1.0, 1.0, 1.0)

    private val model = Kana3DModel.make("test", "obj")

    override fun onDrawFrame(context: KanaContext) {
        context.queueUp(pipeline) {
            projectionMatrix = projectionMatrix.translate(2 v 2 v 2)

            sendUniforms<FirstViewUniforms> {
                it.viewModelMatrix.set(viewModelMatrix)
                it.projectionMatrix.set(projectionMatrix)
            }
            sendBuffer(vertices)
            drawPrimitives(0, 3, order = order)
        }
    }
}