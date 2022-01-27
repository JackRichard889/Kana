package dev.jackrichard.kana

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSize
import platform.Foundation.NSCoder
import platform.Metal.MTLClearColorMake
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.Metal.MTLPixelFormatBGRA8Unorm_sRGB
import platform.Metal.MTLPixelFormatDepth32Float
import platform.MetalKit.MTKView
import platform.MetalKit.MTKViewDelegateProtocol
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.addSubview
import platform.darwin.NSObject

actual typealias KanaView = UIView
actual object KanaBuilder {
    fun buildView(delegate: () -> KanaRenderer): KanaView = UIView().also {
        val view = MTKView(frame = UIScreen.mainScreen.bounds)
        val device = MTLCreateSystemDefaultDevice()!!

        KanaGlobals.device = device
        KanaGlobals.commandQueue = device.newCommandQueue()!!

        view.device = device
        view.delegate = KGLMetalProtocolDelegate(delegate())

        view.clearColor = MTLClearColorMake(1.0, 1.0, 1.0, 1.0)
        view.colorPixelFormat = MTLPixelFormatBGRA8Unorm_sRGB
        view.depthStencilPixelFormat = MTLPixelFormatDepth32Float

        it.addSubview(view)
    }
}

class KGLMetalProtocolDelegate(private val delegate: KanaRenderer) : NSObject(), MTKViewDelegateProtocol {
    override fun drawInMTKView(view: MTKView) {
        val context = KanaContext()
        context.delegateView = view

        delegate.onDrawFrame(context)
    }

    override fun mtkView(view: MTKView, drawableSizeWillChange: CValue<CGSize>) {
        drawableSizeWillChange.useContents {
            delegate.onScreenSized(this.width.toInt() to this.height.toInt())
        }
    }
}