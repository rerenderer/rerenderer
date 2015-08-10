(ns rerenderer.examples.simple)
  ;(:require-macros [cljs.core.async.macros :refer [go-loop]])
  ;(:require [cljs.core.async :refer [chan <!]]
  ;          [rerenderer.core :as r :include-macros true]
  ;          [rerenderer.browser :refer [browser]]))

;(defn root
;  [ctx {:keys [color]} {:keys [colors]}]
;  (r/set! (r/.. ctx -fillStyle) (get colors color))
;  (r/call! ctx (fillRect 50 50 100 100)))
;
;(defn handle-clicks!
;  [platform state {:keys [colors]}]
;  (let [clicks (chan)]
;    (r/listen! platform "click" clicks)
;    (go-loop []
;      (<! clicks)
;      (swap! state update-in [:color]
;             #(-> % inc (mod (count colors))))
;      (recur))))
;
;(defn init!
;  [canvas]
;  (let [platform (browser canvas)
;        state (atom {:color 0})
;        options {:colors ["red" "green" "blue"]}]
;    (r/init! platform root state options)
;    (handle-clicks! platform state options)))
