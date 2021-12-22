package dev.jackrichard.konangraphics

expect class KGLContext {

}

expect object KGLGlobals

expect class KGLTexture(source: KGLAsset)
expect class KGLModel(source: KGLAsset)

class KGLAsset (val name: String, val extension: String) {
    fun asTexture() : KGLTexture = KGLTexture(this)
    fun asModel() : KGLModel = KGLModel(this)
}

expect class KGLPipeline private constructor() {
    companion object {
        fun initNew() : KGLPipeline
    }

    fun setVertexFunction(shader: KGLShader?)
    fun setFragmentFunction(shader: KGLShader?)
}

enum class KGLShaderType { FRAGMENT, VERTEX }
enum class KGLPlatform { ANDROID, IOS }
expect class KGLShaderSource
expect class KGLShader private constructor(platform: KGLPlatform, source: String, type: KGLShaderType, name: String) {
    var compiledSource: KGLShaderSource

    companion object {
        fun compileShader(platform: KGLPlatform, type: KGLShaderType, name: String, source: String): KGLShader?
    }
}