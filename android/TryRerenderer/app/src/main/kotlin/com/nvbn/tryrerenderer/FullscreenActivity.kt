package com.nvbn.tryrerenderer

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import org.jetbrains.anko.button
import org.jetbrains.anko.ctx
import org.jetbrains.anko.frameLayout


public class FullscreenActivity : Activity() {
    val TAG = "FULSCREEN_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val interpreter = Interpreter()
        val view = FullscreenView(ctx)
        setContentView(view)

        val interop = Interop("http://192.168.0.108:3449",
                { script, rootId ->
                    try {
                        view.render(interpreter.call(script, rootId))
                    } catch (e: Exception) {
                        Log.e(TAG, "Interpretation failed", e)
                    }
                },
                ctx)
    }

}
