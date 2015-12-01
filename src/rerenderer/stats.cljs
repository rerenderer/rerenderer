(ns rerenderer.stats
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<! timeout]]))

(def stats-state (atom {}))

(def frame 1000) ; miliseconds

(defn avg
  [items]
  (if (zero? (count items))
    0
    (/ (reduce + items) (count items))))

(defn- update-stats
  [stats lines took]
  (-> stats
      (update :lines conj lines)
      (update :took conj took)))

(defn update-stats!
  [lines took]
  (swap! stats-state update-stats lines took))

(defn print-stats!
  []
  (println "AVG lines:" (-> stats-state deref :lines avg)
           "AVG took:" (-> stats-state deref :took avg)))

(defn init-stats!
  "Enables debug stats prints every `frame` miliseconds."
  []
  (let [reset-state! #(reset! stats-state {:lines []
                                           :took []})]
    (reset-state!)
    (go-loop []
      (<! (timeout frame))
      (print-stats!)
      (reset-state!)
      (recur))))
