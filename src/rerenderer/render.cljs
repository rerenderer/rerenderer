(ns rerenderer.render
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [chan <! sliding-buffer timeout]]
            [rerenderer.platform.core :refer [apply-script! render-to]]
            [rerenderer.types.node :refer [Component->Node]]
            [rerenderer.types.render-result :refer [sanitize-cache!]]
            [rerenderer.lang.gc :refer [gc]]))

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
        script (gc (render-node node))
        canvas (-> node :canvas last)]
    (sanitize-cache! node)
    (apply-script! script canvas options)))


(defn get-render-ch
  [root options]
  (let [ch (chan (sliding-buffer 1))]
    (go-loop []
      (<! (timeout (/ 1000 (get options :fps-limit 25))))
      (render-component! (root (<! ch)) options)
      (recur))
    ch))
