package dev.jackrichard.konangraphics

import platform.Metal.MTLFunctionProtocol
import platform.MetalKit.MTKView

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

actual class KGLShader {
    lateinit var shader: MTLFunctionProtocol
}