package dev.jackrichard.konangraphics

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES32
import dev.jackrichard.konangraphics.specifics.GLESMesh
import javax.microedition.khronos.opengles.GL10

actual class KGLContext {
    lateinit var delegateView: GL10
}

@SuppressLint("StaticFieldLeak")
actual object KGLGlobals {
    lateinit var context: Context
}

actual class KGLTexture actual constructor(source: KGLAsset) {
    // private lateinit var raw:
}

actual class KGLModel actual constructor(source: KGLAsset) {
    private var raw: GLESMesh

    init {
        raw = GLESMesh(KGLGlobals.context, "${source.name}.${source.extension}")
    }
}

actual class KGLShaderSource {
    var shader: Int = 0
}

actual class KGLPipeline private actual constructor() {
    private var program: Int = 0

    actual companion object {
        actual fun initNew() : KGLPipeline = KGLPipeline().apply {
            program = GLES32.glCreateProgram()
        }
    }

    actual fun setVertexFunction(shader: KGLShader?) { if (shader != null) { GLES32.glAttachShader(program, shader.compiledSource.shader) } }
    actual fun setFragmentFunction(shader: KGLShader?) { if (shader != null) { GLES32.glAttachShader(program, shader.compiledSource.shader) } }

}

actual class KGLShader private actual constructor(val platform: KGLPlatform, val source: String, val type: KGLShaderType, val name: String) {

    actual var compiledSource: KGLShaderSource = KGLShaderSource().apply {
        shader = GLES32.glCreateShader(
            when (type) {
                KGLShaderType.FRAGMENT -> GLES32.GL_FRAGMENT_SHADER
                KGLShaderType.VERTEX -> GLES32.GL_VERTEX_SHADER
                else -> 0
            }
        ).also { shader1 ->
            GLES32.glShaderSource(shader1, source)
            GLES32.glCompileShader(shader1)
        }
    }

    actual companion object {
        actual fun compileShader(
            platform: KGLPlatform,
            type: KGLShaderType,
            name: String,
            source: String
        ): KGLShader? = if (platform == KGLPlatform.ANDROID) KGLShader(platform, source, type, name) else null
    }

}