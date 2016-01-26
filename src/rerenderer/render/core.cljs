(ns rerenderer.render.core
  (:require [rerenderer.platform.core :refer [apply-script]]
            [rerenderer.render.node :refer [->node]]
            [rerenderer.render.render-node :as render-node]))

;(def cache (atom {}))

(defn get-full-script
  [root]
  (loop [[render-node & rest-render-nodes] (render-node/childs root)
         result (render-node/script root)]
    (if render-node
      (recur (concat (render-node/childs render-node) rest-render-nodes)
             (concat result (render-node/script render-node)))
      result)))

(defn render-tree!
  [tree options]
  (let [node (->node tree)
        render-node (render-node/->render-node node)
        script (get-full-script render-node)
        [_ canvas-id] (render-node/canvas render-node)]
    (apply-script script canvas-id options)))
