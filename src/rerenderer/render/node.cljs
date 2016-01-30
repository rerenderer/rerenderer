(ns rerenderer.render.node
  (:require [clojure.string :as string]
            [rerenderer.render.component :as cmp]
            [rerenderer.platform.core :refer [render ->RenderResult]]
            [rerenderer.optimizer :refer [nodes-cache]]))

(defrecord Node [childs script canvas x y])

(def calculate-path
  (memoize
    (fn [component]
      (string/join ":" (merge [(cmp/tag component)
                               (cmp/props component)]
                              (map :path (cmp/childs component)))))))

(defn get-render-result ; TODO !!!!
  [component]
  (let [path (calculate-path component)
        cached (@nodes-cache path)]
    (if cached
      (->RenderResult [] cached)
      (render component))))

(defn Component->Node
  [component]
  (let [{:keys [script canvas]} (get-render-result component)
        {:keys [x y]} (cmp/props component)
        path (calculate-path component)]
    (map->Node {:childs (mapv Component->Node (cmp/childs component))
                :script script
                :canvas canvas
                :x x
                :y y})))
