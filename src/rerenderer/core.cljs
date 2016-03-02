(ns ^:figwheel-always rerenderer.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan >!]]
            [rerenderer.platform.core :refer [listen! information]]
            [rerenderer.render :refer [render-component! get-render-ch]]))

(defn init!
  "Initializes new rerenderer application, required params:
    - root-view - function for rendering root canvas - (fn [state options])
    - event-handler - function for handling events - (fn [event-ch state-atom options])
    - **options - additional platform/app-dependent options."
  [& {:keys [root-view event-handler state] :as options}]
  {:pre [(ifn? root-view)
         (map? state)]}
  (let [render-ch (get-render-ch root-view options)
        event-ch (chan)
        platform-info (information options)
        state-atom (atom (assoc state :platform platform-info))]
    (listen! event-ch options)

    (add-watch state-atom :render
               #(go (>! render-ch %4)))

    (go (>! render-ch @state-atom)
        (>! event-ch [:init]))

    (when event-handler
      (event-handler event-ch state-atom options))))
