package dev.jackrichard.konangraphics

expect class KGLContext {
    fun compileShader(source: String, name: String, type: KGLShaderType) : KGLShader
}

expect object KGLGlobals

expect class KGLTexture(source: KGLAsset)
expect class KGLModel(source: KGLAsset)
expect class KGLFont(source: KGLAsset)

class KGLAsset (val name: String, val extension: String) {
    fun asTexture() : KGLTexture = KGLTexture(this)
    fun asModel() : KGLModel = KGLModel(this)
    fun asFont() : KGLFont = KGLFont(this)
}

enum class KGLShaderType { FRAGMENT, VERTEX }
expect class KGLShader