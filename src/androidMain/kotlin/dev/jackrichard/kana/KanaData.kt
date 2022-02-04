package dev.jackrichard.kana

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class BufferedData(val buf: Buffer, actual val size: Int)

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

actual fun ShortArray.buffered() : BufferedData {
    val sArray = this
    return BufferedData(
        buf = ByteBuffer.allocateDirect(this.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(sArray)
                position(0)
            }
        },
        size = this.size * 2
    )
}