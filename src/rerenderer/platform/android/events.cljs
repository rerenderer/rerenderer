(ns rerenderer.platform.android.events
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>!]]
            [rerenderer.platform.android.bus :refer [on-event!]]))

(defn translate-event
  "Translates event from intermediate representation."
  [event]
  (condp = (:event event)
    "click" [:click {:x (:x event)
                     :y (:y event)}]
    "keyup" [:keyup {:keycode (:keycode event)}]
    "keydown" [:keydown {:keycode (:keycode event)}]))

(defn bind-event!
  "Binds all event to channel."
  [ch]
  (on-event! #(go (>! ch (translate-event %)))))
