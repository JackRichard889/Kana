package dev.jackrichard.konangraphics

expect class KGLContext {
    fun compileShader(init: KGLShader.() -> Unit) : KGLShader
}

expect object KGLGlobals

expect class KGLTexture(source: KGLAsset)
expect class KGLModel(source: KGLAsset)

class KGLAsset (val name: String, val extension: String) {
    fun asTexture() : KGLTexture = KGLTexture(this)
    fun asModel() : KGLModel = KGLModel(this)
}

enum class KGLShaderType { FRAGMENT, VERTEX }
enum class KGLPlatform { ANDROID, IOS }
expect class KGLShaderSource
class KGLShader {
    lateinit var platform: KGLPlatform
    lateinit var source: String
    lateinit var type: KGLShaderType
    lateinit var name: String
    lateinit var compiledSource: KGLShaderSource
}