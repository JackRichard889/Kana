package dev.jackrichard.kana

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.toCValues
import platform.Metal.MTLBufferProtocol
import platform.Metal.MTLResourceOptionCPUCacheModeWriteCombined

actual class BufferedData(val buf: MTLBufferProtocol, actual val size: Int)

actual fun FloatArray.buffered() : BufferedData = BufferedData(
    buf = KanaGlobals.device.newBufferWithBytes(
        this.toCValues().getPointer(MemScope()),
        (this.size * 4).toULong(),
        MTLResourceOptionCPUCacheModeWriteCombined
    )!!,
    size = this.size * 4
)

actual fun ShortArray.buffered() : BufferedData = BufferedData(
    buf = KanaGlobals.device.newBufferWithBytes(
        this.toCValues().getPointer(MemScope()),
        (this.size * 2).toULong(),
        MTLResourceOptionCPUCacheModeWriteCombined
    )!!,
    size = this.size * 2
)