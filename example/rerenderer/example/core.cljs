(ns rerenderer.example.core
  ^:figwheel-always
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<! timeout]]
            [cljs.core.match :refer-macros [match]]
            [rerenderer.core :refer [init!]]
            [rerenderer.primitives :refer [rectangle]]))

(enable-console-print!)

(defn root
  [state]
  (rectangle {:width  400
              :height 400
              :x      0
              :y      0
              :color  [255 0 (:i state) 0]}
             (for [n (range 10)]
               (rectangle {:width  400
                           :height (- 400 (* 20 n))
                           :x      0
                           :y      0
                           :color  [255 (* 20 n) (:i state) 0]}))

             (rectangle {:width  20
                         :height 200
                         :x      (:i state)
                         :y      (:i state)
                         :color  [10 255 255 255]})))

(defn event-handler
  [event-ch state-atom options]
  (go-loop []
    (let [event (<! event-ch)]
      (println event))
    (recur))

  (go-loop []
    (swap! state-atom update :i #(-> % (+ 20) (mod 255)))
    (<! (timeout 100))
    (recur)))

(defonce game (init! :root-view #'root
                     :state {:i 0}
                     :canvas (.getElementById js/document "canvas")
                     :event-handler #'event-handler))
