package dev.jackrichard.konangraphics

import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Metal.MTLDeviceProtocol
import platform.Metal.MTLFunctionProtocol
import platform.Metal.MTLRenderPipelineDescriptor
import platform.Metal.MTLTextureProtocol
import platform.MetalKit.*
import platform.ModelIO.MDLAsset
import platform.posix.pipe

actual class KGLContext {
    lateinit var delegateView: MTKView
}

@ThreadLocal
actual object KGLGlobals {
    lateinit var device: MTLDeviceProtocol
}

actual class KGLTexture actual constructor(source: KGLAsset) {
    private lateinit var raw: MTLTextureProtocol

    init {
        val loader = MTKTextureLoader(device = KGLGlobals.device)
        loader.newTextureWithName(
            source.name + "." + source.extension,
            scaleFactor = 1.0,
            bundle = null,
            options = mapOf(
                MTKTextureLoaderOptionGenerateMipmaps to true,
                MTKTextureLoaderOptionSRGB to true
            )
        ) { mtlTextureProtocol: MTLTextureProtocol?, _: NSError? ->
            raw = mtlTextureProtocol!!
        }
    }
}

actual class KGLModel actual constructor(source: KGLAsset) {
    private var raw: MTKMesh

    init {
        val file = NSBundle.mainBundle.URLForResource(source.name, source.extension)
        val asset = MDLAsset(uRL = file, vertexDescriptor = null, bufferAllocator = null)
        raw = (MTKMesh.newMeshesFromAsset(asset, KGLGlobals.device, null, null)?.get(1) as List<MTKMesh>).first()
    }
}

actual class KGLShaderSource {
    lateinit var shader: MTLFunctionProtocol
}

actual class KGLPipeline private actual constructor() {
    lateinit var pipeline: MTLRenderPipelineDescriptor

    actual companion object {
        actual fun initNew(): KGLPipeline {
            return KGLPipeline().apply {
                pipeline = MTLRenderPipelineDescriptor()
            }
        }
    }

    actual fun setVertexFunction(shader: KGLShader?) { if (shader != null) { pipeline.setVertexFunction(shader.compiledSource.shader) } }
    actual fun setFragmentFunction(shader: KGLShader?) { if (shader != null) { pipeline.setFragmentFunction(shader.compiledSource.shader) } }

}

actual class KGLShader private actual constructor(val platform: KGLPlatform, val source: String, val type: KGLShaderType, val name: String) {

    actual var compiledSource: KGLShaderSource = KGLShaderSource().apply {
        shader = KGLGlobals.device
            .newLibraryWithSource(source, null, error = null)!!
            .newFunctionWithName(shader.name)!!
    }

    actual companion object {
        actual fun compileShader(
            platform: KGLPlatform,
            type: KGLShaderType,
            name: String,
            source: String
        ): KGLShader? = if (platform == KGLPlatform.IOS) KGLShader(platform, source, type, name) else null
    }

}