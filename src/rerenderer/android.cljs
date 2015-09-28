(ns rerenderer.android
  ^:figwheel-always
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! chan]]
            [cljs.core.match :refer-macros [match]]
            [cognitect.transit :as t]
            [rerenderer.core :as r :include-macros true]))

(def Bitmap$Config "Bitmap$Config")

(defn transform-types
  [line]
  (println line)
  (match line
    [:new result-var cls args] [":new" (str result-var) (name cls) (vec args)]
    [:get result-var var attr] [":get" (str result-var) (str var) (str attr)]
    [:call result-var var method args] [":call" (str result-var)
                                        (str var) (str method) (vec args)]))

(defmethod r/apply-script :android
  [script root-id _]
  (let [script (mapv transform-types script)
        writer (t/writer :json)
        serialised (t/write writer script)]
    (.send js/android serialised root-id)))

(defmethod r/listen! :android
  [event _]
  (let [ch (chan)]
    ;(.listen js/android (name event)
    ;         #(go (>! ch %)))
    ch))

(defprotocol IAndroid
  (render-android [_ canvas]))

(defmethod r/render! :android
  [component]
  {:pre [(satisfies? r/IComponent component)
         (satisfies? IAndroid component)]}
  (let [[w h] (r/size component)
        colorspace (r/.. 'Bitmap$Config (valueOf "ARGB_8888"))
        bitmap (r/.. 'Bitmap (createBitmap w h colorspace))
        canvas (r/new Canvas bitmap)]
    (render-android component canvas)
    bitmap))

(defmethod r/render-to! :android
  [component canvas]
  {:pre [(satisfies? r/IComponent component)
         (satisfies? IAndroid component)]}
  (let [[x y] (r/position component)
        paint (r/new Paint)]
    (r/.. canvas (drawBitmap (r/render! component) x y paint))))
