(ns rerenderer.render
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [chan <! sliding-buffer timeout]]
            [rerenderer.platform.core :refer [render]]
            [rerenderer.component :refer [childs]]))

(defn render!
  "Returns channel that waits for states."
  [root state options]
  (try
    (render (root state) options)
    (catch :default e (.error js/console "Rendering failed" e))))
