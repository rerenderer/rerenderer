package com.nvbn.tryrerenderer

import android.app.Activity
import android.os.Bundle
import org.jetbrains.anko.button
import org.jetbrains.anko.ctx
import org.jetbrains.anko.frameLayout


public class FullscreenActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val interpreter = Interpreter()
        val interop = Interop("http://192.168.0.108:3449",
                { script -> interpreter.call(script) }, ctx)

        frameLayout {
            FullscreenView(ctx)
        }
    }

}
