package dev.jackrichard.kana

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.view.View
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

actual class KanaView constructor(ctx: Context, renderer: KanaRenderer?) : View(ctx) {
    var glView: GLSurfaceView = GLSurfaceView(ctx)

    constructor(ctx: Context) : this(ctx, null)

    init {
        glView.setEGLContextClientVersion(3)
        KanaGlobals.context = ctx
        glView.setRenderer(KGLGLESProtocolDelegate(ctx, renderer!!))
    }
}

class KGLGLESProtocolDelegate(val context: Context, private val delegate: KanaRenderer) : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        gl!!.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        gl.glClearDepthf(1.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)
        gl.glShadeModel(GL10.GL_SMOOTH)
        gl.glDisable(GL10.GL_DITHER)

        delegate.onInitialized()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
        delegate.onScreenSized(width to height)
    }

    override fun onDrawFrame(gl: GL10?) {
        val context = KanaContext()
        context.delegateView = gl!!

        delegate.onDrawFrame(context)
    }
}