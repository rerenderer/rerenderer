(ns ^:figwheel-always rerenderer.primitives
  (:require [rerenderer.browser :refer [IBrowser]]
            [rerenderer.android :refer [IAndroid]]
            [rerenderer.core :as r :include-macros true]))

(defprotocol IPrimitive
  (position [_]))

(defmulti render-childs #(deref r/platform))

(defmethod render-childs :browser
  [ctx childs]
  (doseq [child (flatten childs)
          :let [[x y] (position child)
                _ (println x y "!!!")]]
    (r/.. ctx (drawImage (r/render child) x y))))

(defmethod render-childs :android
  [canvas childs]
  (let [paint (r/new Paint)]
    (doseq [child (flatten childs)
            :let [[x y] (position child)]]
      (r/.. canvas (drawBitmap (r/render child) x y paint)))))

(defn rectangle
  [{:keys [width height color x y]
    :or {width 0
         height 0
         color [255 0 0 0]
         x 0
         y 0}}
   & childs]
  (reify
    r/IComponent
    (size [_] [width height])
    IPrimitive
    (position [_] [x y])
    IBrowser
    (render-browser [_ ctx]
      (let [[a r g b] color
            color (str "rgba(" r ", " g ", " b ", " a ")")]
        (r/set! (r/.. ctx -fillStyle) color))
      (r/.. ctx (fillRect 0 0 width height))
      (render-childs ctx childs))
    IAndroid
    (render-android [_ canvas]
      (let [paint (r/new Paint)
            [a r g b] color]
        (r/.. paint (setARGB a r g b))
        (r/.. canvas (drawRect 0 0 width height paint)))
      (render-childs canvas childs))))
