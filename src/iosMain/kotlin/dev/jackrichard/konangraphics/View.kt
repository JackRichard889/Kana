package dev.jackrichard.konangraphics

import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGSize
import platform.Foundation.NSCoder
import platform.Metal.MTLClearColorMake
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.Metal.MTLPixelFormatBGRA8Unorm_sRGB
import platform.Metal.MTLPixelFormatDepth32Float
import platform.MetalKit.MTKView
import platform.MetalKit.MTKViewDelegateProtocol
import platform.UIKit.UIViewController
import platform.UIKit.addSubview
import platform.darwin.NSObject

actual class KGLView @OverrideInit constructor(coder: NSCoder) : UIViewController(coder) {
    override fun viewDidLoad() {
        super.viewDidLoad()

        val view = MTKView()
        val device = MTLCreateSystemDefaultDevice()!!

        KGLGlobals.device = device

        view.device = device
        view.delegate = KGLMetalProtocolDelegate()
        view.clearColor = MTLClearColorMake(1.0, 1.0, 1.0, 1.0)
        view.colorPixelFormat = MTLPixelFormatBGRA8Unorm_sRGB
        view.depthStencilPixelFormat = MTLPixelFormatDepth32Float

        this.view.addSubview(view)
    }
}

class KGLMetalProtocolDelegate : NSObject, MTKViewDelegateProtocol {
    constructor() {

    }

    override fun drawInMTKView(view: MTKView) {

        TODO("Not yet implemented")
    }

    override fun mtkView(view: MTKView, drawableSizeWillChange: CValue<CGSize>) {
        TODO("Not yet implemented")
    }
}