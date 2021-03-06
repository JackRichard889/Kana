package dev.jackrichard.kana

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils
import dev.jackrichard.kana.specifics.GLESMesh
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10

actual class KanaContext {
    lateinit var delegateView: GL10

    actual fun queueUp(pipeline: KanaPipeline, func: KanaCommandBuffer.() -> Unit) {
        val kglBuffer = KanaCommandBuffer().also {
            it.pipeline = pipeline.also { pipel ->
                GLES32.glUseProgram(pipel.program)
                pipel.initFromDescriptor(pipel.vertexDescriptor!!)
            }
        }

        kglBuffer.func()
        kglBuffer.deinit()
    }

    actual class KanaCommandBuffer internal constructor() {
        var pipeline: KanaPipeline? = null

        actual inline fun <reified T : KanaUniforms> sendUniforms(function: (T) -> Unit) {
            val t = T::class.java.newInstance()
            t.also(function)
            t.sendUniforms(pipeline!!.program)
        }
        actual fun sendBuffer(buffer: BufferedData) { GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, buffer.size, buffer.buf, GLES32.GL_STATIC_DRAW) }
        actual fun drawPrimitives(start: Int, end: Int, order: BufferedData?) {
            if (order != null) {
                GLES32.glDrawElements(GLES32.GL_TRIANGLES, end - start, GLES32.GL_UNSIGNED_SHORT, order.buf)
            } else {
                GLES32.glDrawArrays(GLES32.GL_TRIANGLES, start, end)
            }
        }
        actual fun drawMeshModel(mesh: Kana3DModel) {
            GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, mesh.positions.size, mesh.positions.buf, GLES32.GL_STATIC_DRAW)
            GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, mesh.positions.size / 12)
        }

        fun deinit() { pipeline!!.deInitFromDescriptor() }
    }
}

@SuppressLint("StaticFieldLeak")
actual object KanaGlobals {
    lateinit var context: Context
}

actual class KanaTexture private constructor(name: String, ext: String, directory: String, options: KanaTextureOptions.() -> Unit) {
    private val textureID = IntArray(1)

    init {
        GLES32.glGenTextures(1, textureID, 0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureID[0])

        KanaTextureOptions().also(options).also {
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER,
                    when (it.minFilter) {
                        KanaTextureOptions.KanaTextureParameter.LINEAR -> GLES32.GL_LINEAR
                        KanaTextureOptions.KanaTextureParameter.NEAREST -> GLES32.GL_NEAREST
                    }
                )
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER,
                    when (it.magFilter) {
                        KanaTextureOptions.KanaTextureParameter.LINEAR -> GLES32.GL_LINEAR
                        KanaTextureOptions.KanaTextureParameter.NEAREST -> GLES32.GL_NEAREST
                    }
                )
        }

        val inps = KanaGlobals.context.resources.assets.open("${if (directory.isNotBlank()) "/$directory" else "" }$name.$ext")
        val bitmap = BitmapFactory.decodeStream(inps)

        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
    }

    actual companion object {
        actual fun make(name: String, extension: String, directory: String, options: KanaTextureOptions.() -> Unit) = KanaTexture(name, extension, directory, options)
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
    var program: Int = 0

    actual var vertexShader: Pair<KanaShader?, KanaShader?> = null to null
        set(value) {
            field = value

            GLES32.glAttachShader(program, (value.first ?: value.second)!!.compiledSource.shader)
        }
    actual var fragmentShader: Pair<KanaShader?, KanaShader?> = null to null
        set(value) {
            field = value

            GLES32.glAttachShader(program, (value.first ?: value.second)!!.compiledSource.shader)
        }
    actual var vertexDescriptor: VertexDescriptor? = null

    fun initFromDescriptor(value: VertexDescriptor) {
        fun calcOffset(indexFrom: Int) : Int {
            if (indexFrom < 0) { return 0 }
            return value.elements.subList(0, indexFrom).sumOf { it.size * when (it.type.simpleName) {
                "Vec2", "Vec3", "Vec4" -> 4
                else -> 0
            } }
        }

        val descriptorSize: Int = value.elements.sumOf {
            it.size * when (it.type.simpleName) {
                "Vec2", "Vec3", "Vec4" -> 4
                else -> 0
            }
        }

        value.elements.zip(0 until value.elements.size).forEach {
            val identifier = GLES32.glGetAttribLocation(program, it.first.name)

            GLES32.glEnableVertexAttribArray(identifier)
            GLES32.glVertexAttribPointer(
                identifier,
                it.first.size,
                when (it.first.type.simpleName) {
                    "Vec2" -> GLES32.GL_FLOAT
                    "Vec3" -> GLES32.GL_FLOAT
                    "Vec4" -> GLES32.GL_FLOAT
                    else -> 0
                },
                false,
                descriptorSize,
                calcOffset(it.second - 1)
            )
        }
    }

    actual companion object {
        actual fun create(func: KanaPipeline.() -> Unit): KanaPipeline =
            KanaPipeline()
                .also { it.program = GLES32.glCreateProgram() }
                .also(func)
                .also { GLES32.glLinkProgram(it.program) }
                .also {
                    val vao = IntBuffer.allocate(1)
                    GLES32.glGenVertexArrays(1, vao)
                    GLES32.glBindVertexArray(vao[0])

                    val buf = IntBuffer.allocate(1)
                    GLES32.glGenBuffers(1, buf)
                    GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, buf[0])
                }
    }

    fun deInitFromDescriptor() {
        vertexDescriptor!!.elements.forEach {
            val identifier = GLES32.glGetAttribLocation(program, it.name)
            GLES32.glDisableVertexAttribArray(identifier)
        }
    }
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

fun KanaUniforms.sendUniforms(program: Int) {
    uniforms.forEach { uniform ->
        val loc = GLES32.glGetUniformLocation(program, uniform.name)
        when (uniform.value!!::class.simpleName) {
            "Vec2" -> GLES32.glUniform2fv(loc, 1, uniform.value!!.asArray(), 0)
            "Vec3" -> GLES32.glUniform3fv(loc, 1, uniform.value!!.asArray(), 0)
            "Vec4" -> GLES32.glUniform4fv(loc, 1, uniform.value!!.asArray(), 0)
            "Mat2" -> GLES32.glUniformMatrix2fv(loc, 1, false, uniform.value!!.asArray(), 0)
            "Mat3" -> GLES32.glUniformMatrix3fv(loc, 1, false, uniform.value!!.asArray(), 0)
            "Mat4" -> GLES32.glUniformMatrix4fv(loc, 1, false, uniform.value!!.asArray(), 0)
        }
    }
}

actual class Kana3DModel private constructor(val mesh: GLESMesh) {
    val normals: BufferedData = mesh.normals.buffered()
    val textureCoordinates: BufferedData = mesh.textureCoordinates.buffered()
    val positions: BufferedData = mesh.positions.buffered()

    actual companion object {
        actual fun make(name: String, extension: String, directory: String): Kana3DModel =
            Kana3DModel(GLESMesh(KanaGlobals.context, "$directory$name.$extension"))
    }
}