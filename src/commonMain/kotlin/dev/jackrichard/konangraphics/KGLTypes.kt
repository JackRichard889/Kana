package dev.jackrichard.konangraphics

expect class KGLContext {
    fun compileShader(source: String, name: String, type: KGLShaderType) : KGLShader
}

enum class KGLShaderType { FRAGMENT, VERTEX }
expect class KGLShader