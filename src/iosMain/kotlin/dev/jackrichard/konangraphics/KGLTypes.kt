package dev.jackrichard.konangraphics

import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Metal.MTLDeviceProtocol
import platform.Metal.MTLFunctionProtocol
import platform.Metal.MTLTextureProtocol
import platform.MetalKit.*
import platform.ModelIO.MDLAsset

actual class KGLContext {
    lateinit var delegateView: MTKView

    actual fun compileShader(source: String, name: String, type: KGLShaderType) : KGLShader {
        val shader = KGLShader()
        shader.shader = delegateView.device!!
            .newLibraryWithSource(
                source,
                null,
                error = null)!!
            .newFunctionWithName(name)!!
        return shader
    }
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

actual class KGLFont actual constructor(source: KGLAsset) {
    // TODO: implement fonts for iOS
}

actual class KGLShader {
    lateinit var shader: MTLFunctionProtocol
}