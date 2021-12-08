package dev.jackrichard.konangraphics

import android.opengl.GLES20
import javax.microedition.khronos.opengles.GL10

actual class KGLContext {
    lateinit var delegateView: GL10

    actual fun compileShader(source: String, name: String, type: KGLShaderType) : KGLShader {
        val shader = KGLShader()
        shader.shader = GLES20.glCreateShader(
            when (type) {
                KGLShaderType.FRAGMENT -> GLES20.GL_FRAGMENT_SHADER
                KGLShaderType.VERTEX -> GLES20.GL_VERTEX_SHADER
                else -> 0
            }
        ).also { shader1 ->
            GLES20.glShaderSource(shader1, source)
            GLES20.glCompileShader(shader1)
        }
        return shader
    }
}

actual object KGLGlobals

actual class KGLTexture actual constructor(source: KGLAsset) {
    // private lateinit var raw:
}

actual class KGLModel actual constructor(source: KGLAsset) {
    // TODO: implement models for Android
}

actual class KGLFont actual constructor(source: KGLAsset) {
    // TODO: implement fonts for Android
}

actual class KGLShader {
    var shader: Int = 0
}