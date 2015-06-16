(ns rerenderer.examples.android
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [timeout <!]]
            [rerenderer.core :as r :include-macros true]
            [rerenderer.android :as a]))

(defn root
  [ctx {:keys [paint time cx cy]}]
  (r/call! ctx -canvas (drawRGB 250 0 0))
  (r/call! ctx -paint (setARGB 250 150 0 150))
  (r/call! ctx -paint (setTextSize 100))
  (r/call! ctx -canvas (drawText "It's working!" 100 1000 paint))
  (r/call! ctx -paint (setARGB 250 250 250 250))
  (r/call! ctx -paint (setTextSize 30))
  (r/call! ctx -canvas (drawText (str time) 100 1500 paint))
  (r/call! ctx -paint (setARGB 150 0 250 0))
  (r/call! ctx -canvas (drawCircle cx cy 100 paint)))

(defn init!
  []
  (let [platform (a/android js/android)
        state (atom {:paint (.paint js/android)
                     :time (js/Date.)
                     :cx 0
                     :cy 0})]
    (r/init! platform root state {})
    (go-loop []
      (swap! state update-in [:cx] #(-> % (+ 10) (mod 1080)))
      (swap! state update-in [:cy] #(-> % (+ 10) (mod 1920)))
      (<! (timeout 10))
      (recur))
    (go-loop []
      (swap! state assoc :time (js/Date.))
      (<! (timeout 300))
      (recur))))
