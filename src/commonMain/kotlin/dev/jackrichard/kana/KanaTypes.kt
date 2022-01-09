package dev.jackrichard.kana

expect class KanaContext {
    fun queueUp(func: KanaCommandBuffer.() -> Unit)
    class KanaCommandBuffer {
        fun linkPipeline(pipeline: KanaPipeline)
    }
}

expect object KanaGlobals

expect class KanaTexture {
    companion object {
        fun genNew(name: String, extension: String) : KanaTexture
    }
}

expect class KanaPipeline private constructor() {
    companion object {
        fun initNew() : KanaPipeline
    }

    fun setVertexFunction(shader: KanaShader?)
    fun setFragmentFunction(shader: KanaShader?)
    fun setVertexDescriptor(descriptor: VertexDescriptor)
}

enum class KanaShaderType { FRAGMENT, VERTEX }
enum class KanaPlatform { ANDROID, IOS }
expect class KanaShaderSource
expect class KanaShader private constructor(platform: KanaPlatform, source: String, type: KanaShaderType, name: String) {
    var compiledSource: KanaShaderSource

    companion object {
        fun compileShader(platform: KanaPlatform, type: KanaShaderType, name: String, source: String): KanaShader?
    }
}