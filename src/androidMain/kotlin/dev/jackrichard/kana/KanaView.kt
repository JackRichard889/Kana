package dev.jackrichard.kana

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

actual typealias KanaView = GLSurfaceView
actual object KanaBuilder {
    fun buildView(ctx: Context, delegate: () -> KanaRenderer): KanaView =
        GLSurfaceView(ctx.also { KanaGlobals.context = it }).also {
            it.setEGLContextClientVersion(3)
            it.setRenderer(KGLGLESProtocolDelegate(ctx, delegate))
        }
}

class KGLGLESProtocolDelegate(val context: Context, private val builder: () -> KanaRenderer?) : GLSurfaceView.Renderer {
    var delegate: KanaRenderer? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        delegate = builder()!!.also {
            GLES32.glClearColor(
                it.clearColor.r.toFloat(),
                it.clearColor.g.toFloat(),
                it.clearColor.b.toFloat(),
                it.clearColor.a.toFloat()
            )
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
        delegate!!.onScreenSized(width to height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)
        delegate!!.onDrawFrame(KanaContext().apply { delegateView = gl!! })
    }
}