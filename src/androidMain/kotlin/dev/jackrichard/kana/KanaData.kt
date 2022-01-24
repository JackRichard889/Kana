package dev.jackrichard.kana

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

actual class BufferedData(val buf: FloatBuffer, actual val size: Int)
actual fun FloatArray.buffered() : BufferedData {
    val fArray = this
    return BufferedData(
        buf = ByteBuffer.allocateDirect(this.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(fArray)
                position(0)
            }
        },
        size = this.size * 4
    )
}