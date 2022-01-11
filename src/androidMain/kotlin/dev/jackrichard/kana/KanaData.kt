package dev.jackrichard.kana

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

actual class BufferedData(buf: FloatBuffer)
actual fun FloatArray.buffered() : BufferedData = BufferedData(buf =
    ByteBuffer.allocateDirect(this.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(this)
            position(0)
        }
    }
)