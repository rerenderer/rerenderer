(ns ^:figwheel-always rerenderer.examples.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [timeout <!]]
            [rerenderer.examples.bird :as b]))

(enable-console-print!)

;(b/init! (.getElementById js/document "canvas-1"))
(println "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

(defn send!
  [who method & args]
  (let [args (or args [])
        who (name who)
        method (name method)]
    (.send js/android who method (.stringify js/JSON (clj->js args)))))

;(.send js/android "canvas" "drawRGB" (.stringify js/JSON #js [250 250 0]))

(def started (atom 0))

(send! :canvas :drawRGB 250 250 250)

(defn reload
  []
  (let [now (js/Date.)]
    (reset! started now)
    (go-loop [i 0
              current-run now]
      (send! :canvas :drawRGB 250 0 0)
      (send! :paint :setARGB 250 250 10 250)
      (send! :canvas :drawRect 10 10 900 1000 (.paint js/android))
      (send! :paint :setARGB 250 250 250 10)
      (send! :paint :setTextSize 100)
      (send! :canvas :drawText "Clojurescript!" 100.0 1000.0 (.paint js/android))
      (send! :paint :setARGB 250 0 0 0 0)
      (send! :canvas :drawText (str current-run) 100.0 1500.0 (.paint js/android))
      ;(send! :paint :reset)
      (when (= @started current-run)
        (recur (inc i) current-run)))))

(reload)

;(.send js/android "paint" "setARGB" (.stringify js/JSON #js [255 255 255 255]))

;(.send js/and)
