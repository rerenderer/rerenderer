(ns rerenderer.render.node
  (:require [clojure.string :as string]
            [rerenderer.render.component :as cmp]
            [rerenderer.platform.core :refer [render]]))

(defrecord Node [path childs script canvas x y])

(def calculate-path
  (memoize
    (fn [component]
      (string/join ":" (merge [(cmp/tag component)
                               (cmp/props component)]
                              (map :path (cmp/childs component)))))))

(defn Component->Node
  [component]
  (let [{:keys [script canvas]} (render component)
        {:keys [x y]} (cmp/props component)]
    (map->Node {:path (calculate-path component)
                :childs (mapv Component->Node (cmp/childs component))
                :script script
                :canvas canvas
                :x x
                :y y})))
