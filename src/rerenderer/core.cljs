(ns ^:figwheel-always rerenderer.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan >!]]
            [rerenderer.stats :refer [init-stats! update-stats!]]
            [rerenderer.platform.core :refer [listen!]]
            [rerenderer.render :refer [render-component! get-render-ch]]))

(defn init!
  "Initializes new rernderer application, required params:
    - root-view - function for rendering root canvas - (fn [state options])
    - event-handler - function for handling events - (fn [event-ch state-atom options])
    - state - hash-map with initial state."
  [& {:keys [root-view event-handler
             state show-stats] :as options}]
  {:pre [(ifn? root-view)
         (map? state)]}
  (let [render-ch (get-render-ch root-view options)
        event-ch (chan)
        state-atom (atom state)]
    (when show-stats
      (init-stats!))

    (listen! event-ch options)

    (add-watch state-atom :render
               #(go (>! render-ch %4)))

    (go (>! render-ch @state-atom)
        (>! event-ch [:init]))

    (when event-handler
      (event-handler event-ch state-atom options))))
