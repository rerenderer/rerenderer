package com.nvbn.tryrerenderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import org.jetbrains.anko.custom.addView
import org.jetbrains.anko.surfaceView

//import com.nvbn.tryrerenderer.util.Reflection

import org.json.JSONArray

import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue


class FullscreenView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
//    inner class Call(var who: String, var method: String, var args: Array<Any>) {
//
//        public fun call(canvas: Canvas, paint: Paint) {
//            for (i in args.indices) {
//                if (args[i] == mPaintUUIID) {
//                    args[i] = paint
//                }
//
//                //                if (args[i].getClass().equals(double.class)) {
//                //                    args[i] = new Float((double) args[i]);
//                //                }
//                //                Log.d("!!!!!!!!" + args[i].toString(), args[1].getClass().toString());
//            }
//
//            if (who == "paint") {
//                Reflection.call(paint, method, args)
//            } else if (who == "canvas") {
//                Reflection.call(canvas, method, args)
//            } else {
//                Log.e("Can't call non exists ", who)
//            }
//        }
//    }
//
//    inner class RerendererThread(var mSurfaceHolder: SurfaceHolder, var mContext: Context, var mHandler: Handler) : Thread() {
//        var mPaint: Paint
//
//        init {
//            mPaint = Paint()
//        }
//
//        override fun run() {
//            while (true) {
//                var canvas: Canvas? = null
//                try {
//                    val calls = queue.take()
//                    canvas = mSurfaceHolder.lockCanvas(null)
//                    synchronized (mSurfaceHolder) {
//                        for (call in calls)
//                            call.call(canvas, mPaint)
//                    }
//                } catch (e: InterruptedException) {
//                } finally {
//                    if (canvas != null) mSurfaceHolder.unlockCanvasAndPost(canvas)
//                }
//            }
//        }
//    }
//
//    inner class JSInterface {
//        //
//        //        @JavascriptInterface
//        //        public void send(String who, String method, String args) {
//        //            try {
//        //                JSONArray arr = new JSONArray(args);
//        //                Object[] argsArr = new Object[arr.length()];
//        //                for (Integer i = 0; i < arr.length(); i++) {
//        //                    argsArr[i] = arr.get(i);
//        //                }
//        //                queue.put(new Call(who, method, argsArr));
//        //                Log.d("Called: ", "!!" + who + method + argsArr.toString());
//        //            } catch (Exception e) {
//        //                Log.e("Can't get call", e.toString());
//        //            }
//        //        }
//
//        JavascriptInterface
//        public fun send(calls: String) {
//            try {
//                val callsJSON = JSONArray(calls)
//                val callsArr = arrayOfNulls<Call>(callsJSON.length())
//                for (i in 0..callsJSON.length() - 1) {
//                    val callData = callsJSON.getJSONArray(i)
//                    val args = callData.getJSONArray(2)
//                    val argsArr = arrayOfNulls<Any>(args.length())
//
//                    for (j in 0..args.length() - 1)
//                        argsArr[j] = args.get(j)
//
//                    callsArr[i] = Call(callData.getString(0), callData.getString(1), argsArr)
//                }
//                queue.put(callsArr)
//            } catch (e: Exception) {
//                Log.e("Can't get call", e.toString())
//            }
//
//        }
//
//        JavascriptInterface
//        public fun paint(): String {
//            return mPaintUUIID
//        }
//    }

//    var mThread: RerendererThread
//    var mWebView: WebView
//    var queue = LinkedBlockingQueue<Array<Call>>(10)
//    var mPaintUUIID = UUID.randomUUID().toString()

    init {
//        mWebView = WebView(context)
//        mWebView.setWillNotDraw(true)
//        val webSettings = mWebView.getSettings()
//        webSettings.setAppCacheEnabled(false)
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE)
//        webSettings.setJavaScriptEnabled(true)
//        webSettings.setDefaultTextEncodingName("utf-8")
//        mWebView.loadUrl("http://192.168.0.107:3449/#android")
//        mWebView.addJavascriptInterface(JSInterface(), "android")
//        val holder = getHolder()
//        holder.addCallback(this)
//        mThread = RerendererThread(holder, context, object : Handler() {
//            override fun handleMessage(m: Message) {
//            }
//        })

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
//        mThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
}
