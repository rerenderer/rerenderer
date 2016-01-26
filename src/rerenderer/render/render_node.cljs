(ns rerenderer.render.render-node
  (:require [rerenderer.interop :as interop]
            [rerenderer.platform.core :as platform]
            [rerenderer.render.node :as node]))

(defprotocol IRenderNode
  (script [this])
  (canvas [this])
  (path [this])
  (childs [this]))

(defn ->render-node
  ([node] (->render-node node nil))
  ([node parent]
   (reset! interop/script [])
   (let [node-canvas (if parent
                       (platform/render-to! node (canvas parent))
                       (platform/render! node))
         node-script @interop/script]
     (println parent node-canvas)
     (reify
       IRenderNode
       (script [_] node-script)
       (canvas [_] node-canvas)
       (path [_] (node/path node))
       (childs [this] (map #(->render-node % this) (node/childs node)))))))
