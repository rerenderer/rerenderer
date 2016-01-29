(ns rerenderer.example.core
  ^:figwheel-always
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<! timeout]]
            [rerenderer.core :refer [init!]]
            [rerenderer.primitives :refer [rectangle]]))

(enable-console-print!)

(defn root
  [state]
  (rectangle {:width 400
              :height 400
              :x (:i state)
              :y 0
              :color [255 0 0 0]}
             (for [n (range 10)]
               (rectangle {:width 400
                           :height (- 400 (* 20 n))
                           :x 0
                           :y 0
                           :color [255 (* 20 n) 0 0]}))))

(init! :root-view root
       :state {:i 0}
       :canvas (.getElementById js/document "canvas")
       :event-handler (fn [_ state-atom _]
                        (go-loop []
                          (swap! state-atom update :i #(-> % inc (mod 255)))
                          (<! (timeout 50))
                          (recur))))
