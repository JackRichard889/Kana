package dev.jackrichard.kana

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES32
import javax.microedition.khronos.opengles.GL10

actual class KanaContext {
    lateinit var delegateView: GL10

    actual fun queueUp(func: KanaCommandBuffer.() -> Unit) {
        val kglBuffer = KanaCommandBuffer()
        kglBuffer.func()
    }

    actual class KanaCommandBuffer internal constructor() {

    }
}

@SuppressLint("StaticFieldLeak")
actual object KanaGlobals {
    lateinit var context: Context
}

actual class KanaTexture private constructor(name: String, ext: String) {
    init {

    }

    actual companion object {
        actual fun genNew(name: String, extension: String) = KanaTexture(name, extension)
    }
}

/*actual class KGLModel actual constructor(source: KGLAsset) {
    private var raw: GLESMesh

    init {
        raw = GLESMesh(KGLGlobals.context, "${source.name}.${source.extension}")
    }
}*/

actual class KanaShaderSource { var shader: Int = 0 }

actual class KanaPipeline private actual constructor() {
    private var program: Int = 0

    actual companion object {
        actual fun initNew() : KanaPipeline = KanaPipeline().apply {
            program = GLES32.glCreateProgram()
        }
    }

    actual fun setVertexFunction(shader: KanaShader?) { if (shader != null) { GLES32.glAttachShader(program, shader.compiledSource.shader) } }
    actual fun setFragmentFunction(shader: KanaShader?) { if (shader != null) { GLES32.glAttachShader(program, shader.compiledSource.shader) } }

}

actual class KanaShader private actual constructor(val platform: KanaPlatform, val source: String, val type: KanaShaderType, val name: String) {

    actual var compiledSource: KanaShaderSource = KanaShaderSource().apply {
        shader = GLES32.glCreateShader(
            when (type) {
                KanaShaderType.FRAGMENT -> GLES32.GL_FRAGMENT_SHADER
                KanaShaderType.VERTEX -> GLES32.GL_VERTEX_SHADER
                else -> 0
            }
        ).also { shader1 ->
            GLES32.glShaderSource(shader1, source)
            GLES32.glCompileShader(shader1)
        }
    }

    actual companion object {
        actual fun compileShader(
            platform: KanaPlatform,
            type: KanaShaderType,
            name: String,
            source: String
        ): KanaShader? = if (platform == KanaPlatform.ANDROID) KanaShader(platform, source, type, name) else null
    }

}