(ns rerenderer.render
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [chan <! sliding-buffer timeout]]
            [rerenderer.platform.core :refer [apply-script! render-to]]
            [rerenderer.types.node :refer [Component->Node]]
            [rerenderer.types.render-result :refer [sanitize-cache!]]
            [rerenderer.lang.gc :refer [gc]]
            [rerenderer.lang.forms :refer [serialize]]))

(defn- render-childs
  "Renders node childs to node canvas."
  [node]
  (mapcat #(render-to % node) (:childs node)))

(defn- render-node
  "Render node and all childs recursevly."
  [node]
  (loop [[node & rest-nodes] [node]
         render-inside []
         render-on []]
    (if node
      (recur (concat rest-nodes (:childs node))
             (concat render-inside (:script node))
             (concat render-on (render-childs node)))
      (concat render-inside render-on))))

(defn- render-component!
  "Renders component and send script to platfrom side."
  [component options]
  (let [node (Component->Node component)
        script (gc (render-node node))
        canvas (-> node :canvas)]
    (sanitize-cache! node)
    (apply-script! (map serialize script)
                   (serialize canvas) options)))


(defn get-render-ch
  "Returns channel that waits for states."
  [root options]
  (let [ch (chan (sliding-buffer 1))]
    (go-loop []
      (<! (timeout (/ 1000 (get options :fps-limit 25))))
      (render-component! (root (<! ch)) options)
      (recur))
    ch))
