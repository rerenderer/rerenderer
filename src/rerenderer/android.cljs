(ns rerenderer.android
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! chan]]
            [cognitect.transit :as t]
            [rerenderer.core :as r :include-macros true]))

(defmethod r/apply-script :android
  [script root-id _]
  (let [writer (t/writer :json)
        serialised (t/write writer script)]
    (.render js/android serialised root-id)))

(defmethod r/listen! :android
  [event _]
  (let [ch (chan)]
    (.listen js/android (name event)
             #(go (>! ch %)))
    ch))

(defmethod r/make-canvas! :android
  [w h]
  (r/new Bitmap w h "ALPHA_8"))

(defprotocol IAndroid
  (render-android [_ canvas]))

(defmethod r/component->canvas :android
  [component canvas]
  (when-not (satisfies? IAndroid component)
    (throw (js/Error. "Should implement IAndroid!")))
  (let [canvas (r/new Canvas canvas)]
    (render-android component canvas)))
