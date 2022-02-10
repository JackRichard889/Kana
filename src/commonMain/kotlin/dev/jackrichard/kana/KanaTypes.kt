package dev.jackrichard.kana

abstract class KanaUniforms {
    internal val uniforms: MutableList<KanaUniformRef<*>> = mutableListOf()
    class KanaUniformRef<T : KanaDataType>(val name: String) {
        var value: T? = null
        fun set(v: T) { value = v }
    }

    infix fun vec2(name: String) : KanaUniformRef<Vec2> = KanaUniformRef<Vec2>(name).also { uniforms.add(it) }
    infix fun vec3(name: String) : KanaUniformRef<Vec3> = KanaUniformRef<Vec3>(name).also { uniforms.add(it) }
    infix fun vec4(name: String) : KanaUniformRef<Vec4> = KanaUniformRef<Vec4>(name).also { uniforms.add(it) }
    infix fun mat2(name: String) : KanaUniformRef<Mat2> = KanaUniformRef<Mat2>(name).also { uniforms.add(it) }
    infix fun mat3(name: String) : KanaUniformRef<Mat3> = KanaUniformRef<Mat3>(name).also { uniforms.add(it) }
    infix fun mat4(name: String) : KanaUniformRef<Mat4> = KanaUniformRef<Mat4>(name).also { uniforms.add(it) }
}

expect class KanaContext {
    fun queueUp(pipeline: KanaPipeline, func: KanaCommandBuffer.() -> Unit)
    class KanaCommandBuffer {
        inline fun <reified T : KanaUniforms> sendUniforms(function: (T) -> Unit)
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

expect class Kana3DModel {
    companion object {
        fun make(name: String, extension: String, directory: String = "") : Kana3DModel
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

class KanaColor(val r: Double, val g: Double, val b: Double, val a: Double)
fun color(r: Double, g: Double, b: Double, a: Double) : KanaColor = KanaColor(r, g, b, a)