package dev.jackrichard.konangraphics

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import org.reflections.Reflections
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

actual open class KGLView : Activity() {
    lateinit var glView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glView = GLSurfaceView(this)

        KGLGlobals.context = this

        val mainDelegate: KGLDelegate = Reflections().getTypesAnnotatedWith(KGLMainView::class.java)!!.first() as KGLDelegate
        glView.setRenderer(KGLGLESProtocolDelegate(this, mainDelegate))
        this.setContentView(glView)
    }
}

class KGLGLESProtocolDelegate(val context: Context, private val delegate: KGLDelegate) : GLSurfaceView.Renderer {
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
        delegate.onScreenResizes(width to height)
    }

    override fun onDrawFrame(gl: GL10?) {
        val context = KGLContext()
        context.delegateView = gl!!

        delegate.onDrawFrame(context)
    }
}