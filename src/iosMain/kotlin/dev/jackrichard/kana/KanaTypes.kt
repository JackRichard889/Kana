package dev.jackrichard.kana

import platform.Foundation.NSError
import platform.Metal.*
import platform.MetalKit.MTKTextureLoader
import platform.MetalKit.MTKTextureLoaderOptionGenerateMipmaps
import platform.MetalKit.MTKTextureLoaderOptionSRGB
import platform.MetalKit.MTKView

actual class KanaContext {
    lateinit var delegateView: MTKView

    actual fun queueUp(func: KanaCommandBuffer.() -> Unit) {
        val buffer = KanaGlobals.commandQueue.commandBuffer()
        val renderPassDescriptor = delegateView.currentRenderPassDescriptor!!

        renderPassDescriptor.colorAttachments.objectAtIndexedSubscript(0).clearColor = MTLClearColorMake(0.0, 0.0, 0.0, 1.0)
        val commandEncoder = buffer!!.renderCommandEncoderWithDescriptor(renderPassDescriptor)!!
        // TODO: set other attributes on commandEncoder here

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
        private var pipelineState: MTLRenderPipelineStateProtocol? = null
        actual fun linkPipeline(pipeline: KanaPipeline) {
            KanaGlobals.device.newRenderPipelineStateWithDescriptor(pipeline.pipeline) { mtlRenderPipelineStateProtocol: MTLRenderPipelineStateProtocol?, nsError: NSError? ->
                if (nsError != null) {
                    throw Exception("Could not create pipeline state!\n${nsError.localizedDescription}")
                }
                this.pipelineState = mtlRenderPipelineStateProtocol
            }
        }
    }
}

@ThreadLocal
actual object KanaGlobals {
    lateinit var device: MTLDeviceProtocol
    lateinit var commandQueue: MTLCommandQueueProtocol
}

actual class KanaTexture private constructor(name: String, ext: String) {
    private lateinit var texture: MTLTextureProtocol

    init {
        val loader = MTKTextureLoader(device = KanaGlobals.device)
        loader.newTextureWithName(
            "$name.$ext",
            scaleFactor = 1.0,
            bundle = null,
            options = mapOf(
                MTKTextureLoaderOptionGenerateMipmaps to true,
                MTKTextureLoaderOptionSRGB to true
            )
        ) { mtlTextureProtocol: MTLTextureProtocol?, _: NSError? ->
            texture = mtlTextureProtocol!!
        }
    }

    actual companion object {
        actual fun genNew(name: String, extension: String) : KanaTexture = KanaTexture(name, extension)
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

actual class KanaShaderSource { lateinit var shader: MTLFunctionProtocol }

actual class KanaPipeline private actual constructor() {
    lateinit var pipeline: MTLRenderPipelineDescriptor

    actual companion object {
        actual fun initNew(): KanaPipeline {
            return KanaPipeline().apply {
                pipeline = MTLRenderPipelineDescriptor()
            }
        }
    }

    actual fun setVertexFunction(shader: Pair<KanaShader?, KanaShader?>) { pipeline.setVertexFunction((if (shader.first == null) shader.second else shader.first)!!.compiledSource.shader) }
    actual fun setFragmentFunction(shader: Pair<KanaShader?, KanaShader?>) { pipeline.setFragmentFunction((if (shader.first == null) shader.second else shader.first)!!.compiledSource.shader) }
    actual fun setVertexDescriptor(descriptor: VertexDescriptor) {
        val mtlDescriptor = MTLVertexDescriptor()
        descriptor.elements
            .zip(0 until descriptor.elements.size)
            .forEach {
                mtlDescriptor.attributes.objectAtIndexedSubscript(it.second.toULong()).format =
                    when (it.first.type.simpleName) {
                        "Vec2" -> MTLVertexFormatFloat2
                        "Vec3" -> MTLVertexFormatFloat3
                        "Vec4" -> MTLVertexFormatFloat4
                        else -> throw Exception("Unknown data type: ${it.first.type.simpleName}!")
                    }
            }
        pipeline.vertexDescriptor = mtlDescriptor
    }
}

actual class KanaShader private actual constructor(val platform: KanaPlatform, val source: String, val type: KanaShaderType, val name: String) {

    actual var compiledSource: KanaShaderSource = KanaShaderSource().apply {
        shader = KanaGlobals.device
            .newLibraryWithSource(source, null, error = null)!!
            .newFunctionWithName(shader.name)!!
    }

    actual companion object {
        actual fun compileShader(
            platform: KanaPlatform,
            type: KanaShaderType,
            name: String,
            source: String
        ): KanaShader? = if (platform == KanaPlatform.IOS) KanaShader(platform, source, type, name) else null
    }

}