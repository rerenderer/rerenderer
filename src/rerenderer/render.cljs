(ns rerenderer.render
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [chan <! sliding-buffer timeout]]
            [rerenderer.platform.core :refer [render]]
            [rerenderer.component :refer [childs]]))

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
