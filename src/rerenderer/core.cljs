(ns ^:figwheel-always rerenderer.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan >!]]
            [rerenderer.platform.core :refer [listen! information]]
            [rerenderer.render :refer [render!]]))

(defrecord ^:no-doc Game [state-atom initial-state platform-info render-state! event-ch])

(defn init!
  "Initializes new rerenderer application.

  * `root-view` - function for rendering root canvas - `(fn [state options])`;
  * `event-handler` - function for handling events - `(fn [event-ch state-atom options])`;
  * `scale` - scale image to screen - `true/false`;
  * `options` - additional platform/app-dependent options.

  Returns `Game` record."
  [& {:keys [root-view event-handler state] :as options}]
  {:pre [(ifn? root-view)
         (map? state)]}
  (let [event-ch (chan)
        platform-info (information options)
        state-atom (atom (assoc state :platform platform-info))
        render-state! #(render! root-view % options)]
    (listen! event-ch options)

    (add-watch state-atom :render
               #(render-state! %4))

    (render-state! state)
    (go (>! event-ch [:init]))

    (when event-handler
      (event-handler event-ch state-atom options))

    (map->Game {:state-atom state-atom
                :initial-state state
                :platform-info platform-info
                :render-state! render-state!
                :event-ch event-ch})))
