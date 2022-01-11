package dev.jackrichard.kana

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.toCValues
import platform.Metal.MTLBufferProtocol
import platform.Metal.MTLResourceOptionCPUCacheModeWriteCombined

typealias BufferedData = MTLBufferProtocol

actual fun FloatArray.buffered() : BufferedData =
    KanaGlobals.device.newBufferWithBytes(this.toCValues().getPointer(MemScope()), (this.size * 4).toULong(), MTLResourceOptionCPUCacheModeWriteCombined)!!