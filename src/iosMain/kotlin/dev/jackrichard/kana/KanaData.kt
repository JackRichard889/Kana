package dev.jackrichard.kana

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.toCValues
import platform.Metal.MTLBufferProtocol
import platform.Metal.MTLResourceOptionCPUCacheModeWriteCombined

actual class BufferedData(val buf: MTLBufferProtocol)
actual fun FloatArray.buffered() : BufferedData = BufferedData(buf =
    KanaGlobals.device.newBufferWithBytes(
        this.toCValues().getPointer(MemScope()),
        (this.size * 4).toULong(),
        MTLResourceOptionCPUCacheModeWriteCombined
    )!!
)