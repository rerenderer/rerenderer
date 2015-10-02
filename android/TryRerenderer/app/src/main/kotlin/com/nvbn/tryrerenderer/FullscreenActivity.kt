package com.nvbn.tryrerenderer

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import org.jetbrains.anko.*


public class FullscreenActivity : Activity() {
    val TAG = "FULSCREEN_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val interpreter = Interpreter()
        val view = FullscreenView(ctx)
        setContentView(view)

        val interop = Interop(getHost(),
                { script, rootId ->
                    try {
                        view.render(interpreter.call(script, rootId))
                    } catch (e: Stopped) {
                    } catch (e: Exception) {
                        Log.e(TAG, "Interpretation failed", e)
                    }
                },
                ctx)
        view.propagate = { x -> interop.sendEvent(x) }
    }

    fun getHost(): String {
        var host = "http://nvbn-XPS13-9333.local:3449"
        alert("Enter host name:") {
            customView {
                val edit = editText(host)
                positiveButton("Ok") {
                    host = edit.getText().toString()
                }
            }
        }.show()
        return host
    }
}
