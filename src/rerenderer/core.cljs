(ns rerenderer.core
  (:require [cljs.core.match :refer-macros [match]]))

(defprotocol IPlatform
  (render! [this ctx])
  (listen! [this event ch])
  (image [this src])
  (sound [this src])
  (play! [this sound]))

(defn render-state!
  [platform state root ctx]
  (let [last-draw @ctx]
    (reset! ctx [])
    (root ctx state)
    (when-not (= last-draw @ctx)
      (render! platform @ctx))))

(defn init!
  [platform state root]
  (let [ctx (atom [])]
    (add-watch state :drawer #(render-state! platform %4 root ctx))
    (render-state! platform @state root ctx)))
