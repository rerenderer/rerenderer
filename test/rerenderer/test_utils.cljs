(ns rerenderer.test-utils
  (:require [rerenderer.component :refer [IComponent]]))

(defn make-component
  [tag props & childs]
  (reify
    IComponent
    (tag [_] tag)
    (childs [_] childs)
    (props [_] props)))

(defn browser-pixel-color
  [canvas x y]
  (let [data (.. canvas (getContext "2d") (getImageData x y 1 1) -data)]
    (mapv #(aget data %) (range 3))))

(defn browser-canvas
  [w h]
  (let [canvas (.createElement js/document "canvas")]
    (set! (.-width canvas) w)
    (set! (.-height canvas) h)
    canvas))
