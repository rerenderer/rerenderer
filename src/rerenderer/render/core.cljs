(ns rerenderer.render.core
  (:require [rerenderer.platform.core :refer [apply-script! render-to]]
            [rerenderer.interop :refer [script]]
            [rerenderer.render.node :refer [Component->Node]]))

(def cache (atom {}))

;(defn get-full-script
;  [root]
;  (loop [[render-node & rest-render-nodes] (render-node/childs root)
;         result (render-node/script root)]
;    (if render-node
;      (if (cache (render-node/path render-node))
;        (recur rest-render-nodes )
;        (recur (concat (render-node/childs render-node) rest-render-nodes)
;               (concat result (render-node/script render-node))))
;      result)))
;
;(defn render-tree!
;  [tree options]
;  (let [node (->node tree)
;        render-node (render-node/->render-node node)
;        script (get-full-script render-node)
;        [_ canvas-id] (render-node/canvas render-node)]
;    (apply-script script canvas-id options)))

(defn render-childs
  [node]
  (mapcat #(render-to % node) (:childs node)))

(defn render-node
  [node]
  (loop [[node & rest-nodes] [node]
         render-inside []
         render-on []]
    (if node
      (recur (concat rest-nodes (:childs node))
             (concat render-inside (:script node))
             (concat render-on (render-childs node)))
      (concat render-inside render-on))))

(defn render-component!
  [component options]
  (let [node (Component->Node component)
        script (render-node node)
        canvas (-> node :canvas last)]
    (apply-script! script canvas options)))
