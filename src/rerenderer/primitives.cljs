(ns ^:figwheel-always rerenderer.primitives
  (:require [rerenderer.browser :refer [IBrowser]]
            [rerenderer.android :refer [IAndroid]]
            [rerenderer.core :as r :include-macros true]))

(defn render-childs
  [parent childs]
  (doseq [child (flatten childs)]
    (r/render-to! child parent)))

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

(defn text
  [{:keys [width height font-size color x y]
    :or {width 0
         height 0
         font-size 0
         color [255 0 0 0]
         x 0
         y 0}}
   value & childs]
  (reify
    r/IComponent
    (size [_] [width height])
    (position [_] [x y])
    IBrowser
    (render-browser [_ ctx]
      (let [[a r g b] color
            color (str "rgba(" r ", " g ", " b ", " a ")")]
        (r/set! (r/.. ctx -fillStyle) color)
        (r/set! (r/.. ctx -font) (str font-size "px sans")))
      (r/.. ctx (fillText value 0 font-size))
      (render-childs ctx childs))
    IAndroid
    (render-android [_ canvas]
      (let [paint (r/new Paint)
            [a r g b] color]
        (r/.. paint (setARGB a r g b))
        (r/.. paint (setTextSize font-size))
        (r/.. canvas (drawText value x y paint)))
      (render-childs canvas childs))))
