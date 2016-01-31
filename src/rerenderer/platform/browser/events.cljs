(ns rerenderer.platform.browser.events
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>!]]))

(defn translate-event
  "Translate js event to local representation."
  [event data]
  (condp = event
    "click" [:click {:x (.-clientX data)
                     :y (.-clientY data)}]
    "keydown" [:keydown {:keycode (.-keyCode data)}]
    "keyup" [:keyup {:keycode (.-keyCode data)}]
    [event data]))

(defn bind-events!
  "Binds all events to channel."
  [ch canvas]
  (doseq [event-name ["click" "keydown" "keyup"]]
    (.addEventListener canvas event-name
                       (fn [event]
                         (.preventDefault event)
                         (go (>! ch (translate-event event-name event)))
                         false))))
