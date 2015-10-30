(ns rerenderer.platform.android
  ^:figwheel-always
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! chan]]
            [cljs.core.match :refer-macros [match]]
            [cognitect.transit :as t]
            [rerenderer.interop :as r :include-macros true]
            [rerenderer.platform.core :as platform]
            [rerenderer.core :refer [IComponent size position]]))

(def Bitmap$Config "Bitmap$Config")

(defn transform-args
  [args]
  (vec (for [arg args]
         (match arg
           [:var var] [":var" (str var)]
           [:val val] [":val" val]))))

(defn transform-types
  [line]
  (match line
    [:new result-var cls args] [":new" (str result-var) (name cls) (transform-args args)]
    [:get result-var var attr] [":get" (str result-var) (str var) (str attr)]
    [:call result-var var method args] [":call" (str result-var)
                                        (str var) (str method) (transform-args args)]
    [:free var] [":free" (str var)]))

(when (aget js/window "android")
  (reset! platform/platform :android))

(defmethod platform/apply-script :android
  [script root-id _]
  (let [script (mapv transform-types script)
        writer (t/writer :json)
        serialised (t/write writer script)]
    (.send js/android serialised (str root-id))))

(defmethod platform/listen! :android
  [event _]
  (let [ch (chan)]
    ;(.listen js/android (name event)
    ;         #(go (>! ch %)))
    ch))

(defprotocol IAndroid
  (render-android [_ canvas]))

(defmethod platform/render! :android
  [component]
  {:pre [(satisfies? IComponent component)
         (satisfies? IAndroid component)]}
  (let [[w h] (size component)
        colorspace (r/.. 'Bitmap$Config (valueOf "ARGB_8888"))
        bitmap (r/.. 'Bitmap (createBitmap w h colorspace))
        canvas (r/new Canvas bitmap)]
    (render-android component canvas)
    bitmap))

(defmethod platform/render-to! :android
  [component canvas]
  {:pre [(satisfies? IComponent component)
         (satisfies? IAndroid component)]}
  (let [[x y] (position component)
        paint (r/new Paint)]
    (r/.. canvas (drawBitmap (platform/render! component) x y paint))))
