(ns rerenderer.example.core
  ^:figwheel-always
  (:require [rerenderer.core :refer [init!]]
            [rerenderer.primitives :refer [rectangle]]))

(enable-console-print!)

(defn root
  [state]
  [rectangle {:width 400
              :height 400
              :x 0
              :y 0
              :color [255 0 0 0]}
   (for [n (range 10)]
     [rectangle {:width 400
                 :height (- 400 (* 20 n))
                 :x 0
                 :y 0
                 :color [255 (* 20 n) 0 0]}])])

(init! :root-view root
       :state {}
       :canvas (.getElementById js/document "canvas"))

(println "sad!!!")
