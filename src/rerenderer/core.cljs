(ns rerenderer.core
  (:require [cljs.core.match :refer-macros [match]]))

(defprotocol IPlatform
  (render! [this ctx])
  (listen! [this event ch])
  (image [this src])
  (sound [this src])
  (play! [this sound]))

(defn render-state!
  [platform ctx root state options]
  (let [last-draw @ctx]
    (reset! ctx [])
    (root ctx state options)
    (when-not (= last-draw @ctx)
      (render! platform @ctx))))

(defn init!
  [platform root state options]
  (let [ctx (atom [])]
    (add-watch state :drawer #(render-state! platform ctx root %4 options))
    (render-state! platform ctx root @state options)))
