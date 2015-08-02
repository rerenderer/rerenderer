(ns rerenderer.core
  (:refer-clojure :exclude [.. set!])
  #?(:cljs (:require [cljs.core.match :refer-macros [match]])))

(defn path-to-vec
  [path]
  (mapv #(cond
          (symbol? %) [(str %)]
          (list? %) (apply vector (str (first %)) (rest %)))
        path))

#?(:clj (defmacro call!
          [ctx & path]
          (let [path [:call (path-to-vec path)]]
            `(swap! ~ctx conj ~path))))

#?(:clj (defmacro set!
          [path value]
          (let [ctx (second path)
                path [:set (path-to-vec (drop 2 path)) value]]
            `(swap! ~ctx conj ~path))))

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
