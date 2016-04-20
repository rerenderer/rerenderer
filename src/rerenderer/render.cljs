(ns rerenderer.render
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [chan <! sliding-buffer timeout]]
            [rerenderer.platform.core :refer [render]]
            [rerenderer.types.component :refer [childs]]
    ;[rerenderer.types.node :refer [Component->Node]]
    ;[rerenderer.types.render-result :refer [sanitize-cache!]]
    ;[rerenderer.lang.gc :refer [gc]]
    ;[rerenderer.lang.forms :refer [serialize]]
            ))

;(defn- render-childs
;  "Renders node childs to node canvas."
;  [node]
;  (mapcat #(render-to % node) (:childs node)))
;



;(defn render-all
;  [parent-canvas component]
;  (loop [[[parent-canvas component] & rest-pairs] [[parent-canvas component]]]
;    (when component
;      (let [canvas (render parent-canvas component)
;            new-pairs (for [child (childs component)]
;                        [canvas child])]
;        (recur (concat rest-pairs new-pairs))))))

;(defn render-all
;  "Renders component and send script to platfrom side."
;  [parent-canvas component]
;  (let [rendered (render parent-canvas component)]
;    (for [child (childs component)]
;      (render-all rendered child))))

(defn get-render-ch
  "Returns channel that waits for states."
  [root options]
  (let [ch (chan (sliding-buffer 1))]
    (go-loop []
      (<! (timeout (/ 1000 (get options :fps-limit 25))))
      (try
        (render (root (<! ch) options))
        (catch :default e (.error js/console "Rendering failed" e)))
      (recur))
    ch))
