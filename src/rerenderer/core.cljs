(ns ^:figwheel-always rerenderer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! sliding-buffer timeout]]
            [cljs.core.match :refer-macros [match]]
            [rerenderer.optimizer :refer [reuse]]
            [rerenderer.stats :refer [init-stats! update-stats!]]
            [rerenderer.platform.core :as p]
            [rerenderer.render.core :refer [render-tree!]]))

(defn ^:no-doc get-render-ch
  [root options]
  (let [ch (chan (sliding-buffer 1))]
    (go-loop []
      (<! (timeout (/ 1000 (get options :fps-limit 25))))
      ;(reset! script [])
      ;(let [started (.getTime (js/Date.))
      ;      [_ root-id] (p/render! (root (<! ch)))]
      ;  (let [[cache current-script root-id] (reuse cache @script root-id)]
      ;    (p/apply-script current-script root-id options)
      ;    (when (:show-stats options)
      ;      (update-stats! (count current-script)
      ;                     (- (.getTime (js/Date.)) started)))

      ;(render-state (<! ch) root options)
      (render-tree! (root (<! ch)) options)
      (recur))
    ch))

(defn init!
  "Initializes new rernderer application, required params:
    - root-view - function for rendering root canvas - (fn [state options])
    - event-handler - function for handling events - (fn [event-ch state-atom options])
    - events - vector of events to subscribe, for examples [:click]
    - state - hash-map with initial state."
  [& {:keys [root-view event-handler events
             state show-stats] :as options}]
  {:pre [(ifn? root-view)
         (map? state)]}
  (let [render-ch (get-render-ch root-view options)
        event-ch (chan)
        state-atom (atom state)]
    (when show-stats
      (init-stats!))

    (doseq [event events]
      (p/listen! event-ch event options))

    (add-watch state-atom :render
               #(go (>! render-ch %4)))

    (go (>! render-ch @state-atom)
        (>! event-ch [:init]))

    (when event-handler
      (event-handler event-ch state-atom options))))
