package dev.jackrichard.kana

expect class KanaContext {
    fun queueUp(pipeline: KanaPipeline, func: KanaCommandBuffer.() -> Unit)
    class KanaCommandBuffer {
        fun sendBuffer(buffer: BufferedData)
        fun drawPrimitives(start: Int, end: Int, order: BufferedData? = null)
    }
}

expect object KanaGlobals

class KanaTextureOptions (
    var minFilter: KanaTextureParameter = KanaTextureParameter.LINEAR,
    var magFilter: KanaTextureParameter = KanaTextureParameter.LINEAR
) {
    enum class KanaTextureParameter { NEAREST, LINEAR }
}

expect class KanaTexture {
    companion object {
        fun make(name: String, extension: String, directory: String = "", options: KanaTextureOptions.() -> Unit = {}) : KanaTexture
    }
}

expect class KanaPipeline private constructor() {
    var vertexDescriptor: VertexDescriptor?
    var vertexShader: Pair<KanaShader?, KanaShader?>
    var fragmentShader: Pair<KanaShader?, KanaShader?>

    companion object {
        fun create(func: KanaPipeline.() -> Unit) : KanaPipeline
    }
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