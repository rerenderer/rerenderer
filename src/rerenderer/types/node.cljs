(ns rerenderer.types.node
  (:require [rerenderer.types.component :refer [props childs calculate-path]]
            [rerenderer.types.render-result :refer [Component->RenderResult]]
            [rerenderer.platform.core :refer [render]]))

(defrecord Node [childs script canvas x y])

(defn Component->Node
  "Creates node from component."
  [component]
  (if component
    (let [{:keys [script canvas]} (Component->RenderResult component)
          {:keys [x y]} (props component)
          non-empty-childs (->> component childs flatten (remove nil?))]
      (map->Node {:childs (mapv Component->Node non-empty-childs)
                  :script script
                  :canvas canvas
                  :x x
                  :y y}))))
