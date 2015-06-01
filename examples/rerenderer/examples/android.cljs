(ns rerenderer.examples.android
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [timeout <!]]
            [rerenderer.core :as r :include-macros true]
            [rerenderer.android :as a]))

(defn root
  [ctx {:keys [paint time]}]
  (r/.. ctx -canvas (drawRGB 250 0 0))
  (r/.. ctx -paint (setARGB 250 150 0 150))
  (r/.. ctx -paint (setTextSize 100))
  (r/.. ctx -canvas (drawText "It's working!" 100 1000 paint))
  (r/.. ctx -paint (setARGB 250 250 250 250))
  (r/.. ctx -paint (setTextSize 30))
  (r/.. ctx -canvas (drawText (str time) 100 1500 paint)))

(defn init!
  []
  (let [platform (a/android js/android)
        state (atom {:paint (.paint js/android)
                     :time (js/Date.)})]
    (r/init! platform state root)
    (go-loop []
      (swap! state assoc :time (js/Date.))
      (<! (timeout 300))
      (recur))))
