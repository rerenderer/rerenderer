package com.nvbn.tryrerenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.nvbn.tryrerenderer.util.Reflection;

import org.json.JSONArray;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;


class RerendererView extends SurfaceView implements SurfaceHolder.Callback {
    class Call {
        String who;
        String method;
        Object[] args;

        public Call(String who, String method, Object[] args) {
            this.who = who;
            this.method = method;
            this.args = args;
        }

        public void call(Canvas canvas, Paint paint) {
            for (Integer i = 0; i < args.length; i++) {
                if (args[i].equals(mPaintUUIID)) {
                    args[i] = paint;
                }

//                if (args[i].getClass().equals(double.class)) {
//                    args[i] = new Float((double) args[i]);
//                }
//                Log.d("!!!!!!!!" + args[i].toString(), args[1].getClass().toString());
            }

            if (who.equals("paint")) {
                Reflection.call(paint, method, args);
            } else if (who.equals("canvas")) {
                Reflection.call(canvas, method, args);
            } else {
                Log.e("Can't call non exists ", who);
            }
        }
    }

    class RerendererThread extends Thread {
        Paint mPaint;
        SurfaceHolder mSurfaceHolder;
        Context mContext;
        Handler mHandler;

        public RerendererThread(SurfaceHolder surfaceHolder, Context context,
                                Handler handler) {
            mPaint = new Paint();
            mSurfaceHolder = surfaceHolder;
            mContext = context;
            mHandler = handler;
        }

        @Override
        public void run() {
            while (true) {
                Canvas canvas = null;
                try {
                    Call[] calls = queue.take();
                    canvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        for (Call call : calls)
                            call.call(canvas, mPaint);
                    }
                } catch (InterruptedException e) {
                } finally {
                    if (canvas != null) mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    class JSInterface {
//
//        @JavascriptInterface
//        public void send(String who, String method, String args) {
//            try {
//                JSONArray arr = new JSONArray(args);
//                Object[] argsArr = new Object[arr.length()];
//                for (Integer i = 0; i < arr.length(); i++) {
//                    argsArr[i] = arr.get(i);
//                }
//                queue.put(new Call(who, method, argsArr));
//                Log.d("Called: ", "!!" + who + method + argsArr.toString());
//            } catch (Exception e) {
//                Log.e("Can't get call", e.toString());
//            }
//        }

        @JavascriptInterface
        public void send(String calls) {
            try {
                JSONArray callsJSON = new JSONArray(calls);
                Call[] callsArr = new Call[callsJSON.length()];
                for (Integer i = 0; i < callsJSON.length(); i++) {
                    JSONArray callData = callsJSON.getJSONArray(i);
                    JSONArray args = callData.getJSONArray(2);
                    Object[] argsArr = new Object[args.length()];

                    for (Integer j = 0; j < args.length(); j++)
                        argsArr[j] = args.get(j);

                    callsArr[i] = new Call(
                            callData.getString(0),
                            callData.getString(1),
                            argsArr);
                }
                queue.put(callsArr);
            } catch (Exception e) {
                Log.e("Can't get call", e.toString());
            }
        }

        @JavascriptInterface
        public String paint() {
            return mPaintUUIID;
        }
    }

    RerendererThread mThread;
    WebView mWebView;
    LinkedBlockingQueue<Call[]> queue = new LinkedBlockingQueue<Call[]>(10);
    String mPaintUUIID = UUID.randomUUID().toString();

    public RerendererView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWebView = new WebView(context);
        mWebView.setWillNotDraw(true);
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        mWebView.loadUrl("http://192.168.0.107:3449/#android");
        mWebView.addJavascriptInterface(new JSInterface(), "android");
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mThread = new RerendererThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mThread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
