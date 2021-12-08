package dev.jackrichard.konangraphics

import kotlinx.cinterop.pointed
import kotlinx.cinterop.rawValue
import platform.CoreFoundation.CFBundleGetMainBundle
import platform.Foundation.NSError
import platform.Metal.MTLDeviceProtocol
import platform.Metal.MTLFunctionProtocol
import platform.Metal.MTLTextureProtocol
import platform.MetalKit.*
import kotlin.native.concurrent.freeze

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
    private lateinit var raw: MTKMesh

    // TODO: implement models for iOS
    init {

    }
}

actual class KGLFont actual constructor(source: KGLAsset) {
    // TODO: implement fonts for iOS
}

actual class KGLShader {
    lateinit var shader: MTLFunctionProtocol
}