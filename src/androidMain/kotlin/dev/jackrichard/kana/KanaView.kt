package dev.jackrichard.kana

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

actual class KanaView constructor(ctx: Context, builder: () -> KanaRenderer?) : GLSurfaceView(ctx) {
    constructor(ctx: Context) : this(ctx, { null })

    init {
        KanaGlobals.context = ctx
        setEGLContextClientVersion(3)
        setRenderer(KGLGLESProtocolDelegate(ctx, builder))
    }
}

class KGLGLESProtocolDelegate(val context: Context, private val builder: () -> KanaRenderer?) : GLSurfaceView.Renderer {
    var delegate: KanaRenderer? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        delegate = builder()
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