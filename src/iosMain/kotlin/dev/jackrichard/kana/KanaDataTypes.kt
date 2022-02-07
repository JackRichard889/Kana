package dev.jackrichard.kana

import platform.Foundation.NSError
import platform.Metal.*
import platform.MetalKit.MTKTextureLoader
import platform.MetalKit.MTKTextureLoaderOptionGenerateMipmaps
import platform.MetalKit.MTKTextureLoaderOptionSRGB
import platform.MetalKit.MTKView

actual class KanaContext {
    lateinit var delegateView: MTKView

    actual fun queueUp(pipeline: KanaPipeline, func: KanaCommandBuffer.() -> Unit) {
        val buffer = KanaGlobals.commandQueue.commandBuffer()
        val renderPassDescriptor = delegateView.currentRenderPassDescriptor!!

        renderPassDescriptor.colorAttachments.objectAtIndexedSubscript(0).clearColor = MTLClearColorMake(0.0, 0.0, 0.0, 1.0)
        val commandEncoder = buffer!!.renderCommandEncoderWithDescriptor(renderPassDescriptor)!!
        KanaGlobals.device.newRenderPipelineStateWithDescriptor(pipeline.pipeline) { mtlRenderPipelineStateProtocol: MTLRenderPipelineStateProtocol?, nsError: NSError? ->
            if (nsError != null) {
                throw Exception("Could not create pipeline state!\n${nsError.localizedDescription}")
            }
            commandEncoder.setRenderPipelineState(mtlRenderPipelineStateProtocol!!)
        }

        val kglBuffer = KanaCommandBuffer(renderEncoder = commandEncoder)
        kglBuffer.func()
        commandEncoder.endEncoding()

        val drawable = delegateView.currentDrawable!!
        buffer.presentDrawable(drawable)
        buffer.commit()
    }

    actual class KanaCommandBuffer internal constructor(
        private val renderEncoder: MTLRenderCommandEncoderProtocol
    ) {
        actual inline fun <T : KanaUniforms> sendUniforms(function: (T) -> Unit) {

        }

        actual fun sendBuffer(buffer: BufferedData) {
            renderEncoder.setVertexBuffer(buffer.buf, 0, 0)
        }

        actual fun drawPrimitives(start: Int, end: Int, order: BufferedData?) {
            if (order != null) {
                renderEncoder.drawIndexedPrimitives(MTLPrimitiveTypeTriangle, (end - start).toULong(), MTLIndexTypeUInt16, order.buf, 0)
            } else {
                renderEncoder.drawPrimitives(MTLPrimitiveTypeTriangle, start.toULong(), end.toULong(), 1)
            }
        }
    }
}

@ThreadLocal
actual object KanaGlobals {
    lateinit var device: MTLDeviceProtocol
    lateinit var commandQueue: MTLCommandQueueProtocol
}

actual class KanaTexture private constructor(name: String, ext: String, directory: String, options: KanaTextureOptions.() -> Unit) {
    var texture: MTLTextureProtocol
    val samplerState: MTLSamplerStateProtocol =
        KanaGlobals.device.newSamplerStateWithDescriptor(
            MTLSamplerDescriptor().also {
                    it.normalizedCoordinates = true
                    it.mipFilter = MTLSamplerMipFilterLinear

                    val state = KanaTextureOptions().also(options)
                    it.minFilter =
                        when (state.minFilter) {
                            KanaTextureOptions.KanaTextureParameter.LINEAR -> MTLSamplerMinMagFilterLinear
                            KanaTextureOptions.KanaTextureParameter.NEAREST -> MTLSamplerMinMagFilterNearest
                        }
                    it.magFilter =
                        when (state.magFilter) {
                            KanaTextureOptions.KanaTextureParameter.LINEAR -> MTLSamplerMinMagFilterLinear
                            KanaTextureOptions.KanaTextureParameter.NEAREST -> MTLSamplerMinMagFilterNearest
                        }
                }
        )!!

    init {
        val loader = MTKTextureLoader(device = KanaGlobals.device)
        texture = loader.newTextureWithName(
            "$directory/$name.$ext",
            scaleFactor = 1.0,
            bundle = null,
            options = mapOf(
                MTKTextureLoaderOptionGenerateMipmaps to true,
                MTKTextureLoaderOptionSRGB to true
            ),
            error = null
        )!!
    }

    actual companion object {
        actual fun make(name: String, extension: String, directory: String, options: KanaTextureOptions.() -> Unit) : KanaTexture = KanaTexture(name, extension, directory, options)
    }
}

/*actual class KGLModel actual constructor(source: KGLAsset) {
    private var raw: MTKMesh

    init {
        val file = NSBundle.mainBundle.URLForResource(source.name, source.extension)
        val asset = MDLAsset(uRL = file, vertexDescriptor = null, bufferAllocator = null)
        raw = (MTKMesh.newMeshesFromAsset(asset, KGLGlobals.device, null, null)?.get(1) as List<MTKMesh>).first()
    }
}*/

actual class KanaShaderSource constructor(val shader: MTLFunctionProtocol)

actual class KanaPipeline private actual constructor() {
    var pipeline: MTLRenderPipelineDescriptor = MTLRenderPipelineDescriptor()
    actual var vertexShader: Pair<KanaShader?, KanaShader?> = null to null
        set(value) {
            field = value

            pipeline.setVertexFunction((value.first ?: value.second)!!.compiledSource.shader)
        }
    actual var fragmentShader: Pair<KanaShader?, KanaShader?> = null to null
        set(value) {
            field = value
            pipeline.setFragmentFunction((value.first ?: value.second)!!.compiledSource.shader)
        }

    actual var vertexDescriptor: VertexDescriptor? = null
        set(value) {
            field = value

            if (value == null) { return }

            fun calcOffset(indexFrom: Int) : Int {
                if (indexFrom < 0) { return 0 }
                return value.elements.subList(0, indexFrom).sumOf { it.size }
            }

            val mtlDescriptor = MTLVertexDescriptor()
            value.elements
                .zip(0 until value.elements.size)
                .forEach {
                    mtlDescriptor.attributes.objectAtIndexedSubscript(it.second.toULong()).format =
                        when (it.first.type.simpleName) {
                            "Vec2" -> MTLVertexFormatFloat2
                            "Vec3" -> MTLVertexFormatFloat3
                            "Vec4" -> MTLVertexFormatFloat4
                            else -> throw Exception("Unsupported data type: ${it.first.type.simpleName}!")
                        }
                    mtlDescriptor.attributes.objectAtIndexedSubscript(it.second.toULong()).bufferIndex = 0u
                    mtlDescriptor.attributes.objectAtIndexedSubscript(it.second.toULong()).offset = calcOffset(it.second).toULong()
                }
            mtlDescriptor.layouts.objectAtIndexedSubscript(0).stride = value.elements.sumOf { it.size }.toULong()

            pipeline.vertexDescriptor = mtlDescriptor
        }

    actual companion object {
        actual fun create(func: KanaPipeline.() -> Unit): KanaPipeline =
            KanaPipeline()
                .also(func)
    }
}

actual class KanaShader private actual constructor(val platform: KanaPlatform, source: String, val type: KanaShaderType, name: String) {

    actual var compiledSource: KanaShaderSource =
        KanaShaderSource(KanaGlobals.device.newLibraryWithSource(source, MTLCompileOptions.new(), error = null)!!.newFunctionWithName(name)!!)

    actual companion object {
        actual fun compileShader(
            platform: KanaPlatform,
            type: KanaShaderType,
            name: String,
            source: String
        ): KanaShader? = if (platform == KanaPlatform.IOS) KanaShader(platform, source, type, name) else null
    }

}

fun KanaUniforms.sendUniforms() {

}