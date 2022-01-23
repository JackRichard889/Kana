package dev.jackrichard.kana

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES32
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10

actual class KanaContext {
    lateinit var delegateView: GL10

    actual fun queueUp(func: KanaCommandBuffer.() -> Unit) {
        val kglBuffer = KanaCommandBuffer()
        kglBuffer.func()
        kglBuffer.deinit()
    }

    actual class KanaCommandBuffer internal constructor() {
        private var pipeline: KanaPipeline? = null
        actual fun linkPipeline(pipeline: KanaPipeline) {
            this.pipeline = pipeline.also {
                GLES32.glUseProgram(it.program)
                it.initFromDescriptor(it.vertexDescriptor!!)
            }
        }

        actual fun sendBuffer(buffer: BufferedData) { GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, buffer.buf.capacity(), buffer.buf, GLES32.GL_STATIC_DRAW) }
        actual fun drawPrimitives(start: Int, end: Int) { GLES32.glDrawArrays(GLES32.GL_TRIANGLES, start, end) }
        fun deinit() { pipeline!!.deInitFromDescriptor() }
    }
}

@SuppressLint("StaticFieldLeak")
actual object KanaGlobals {
    lateinit var context: Context
}

actual class KanaTexture private constructor(name: String, ext: String) {
    init {

    }

    actual companion object {
        actual fun genNew(name: String, extension: String) = KanaTexture(name, extension)
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

            GLES32.glAttachShader(program, (if (value.first == null) value.second else value.first)!!.compiledSource.shader)
        }
    actual var fragmentShader: Pair<KanaShader?, KanaShader?> = null to null
        set(value) {
            field = value

            GLES32.glAttachShader(program, (if (value.first == null) value.second else value.first)!!.compiledSource.shader)
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
                    val buf = IntBuffer.allocate(1)
                    GLES32.glGenBuffers(1, buf)
                    GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, buf[0])

                    val vao = IntBuffer.allocate(1)
                    GLES32.glGenVertexArrays(1, vao)
                    GLES32.glBindVertexArray(vao[0])
                }
    }

    fun deInitFromDescriptor() {
        // TODO: this needs to be called somewhere
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