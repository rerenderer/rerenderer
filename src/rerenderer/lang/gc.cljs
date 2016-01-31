(ns rerenderer.lang.gc
  (:require [cljs.core.match :refer-macros [match]]
            [rerenderer.types.render-result :refer [get-cached]]
            [rerenderer.lang.forms :refer [->Free Ref]]))

(def ^:private refs-cache (atom []))

(defn- get-all-refs
  [script]
  (->> (for [instruction script
             field [:result-ref :ref :args]]
         (get instruction field))
       flatten
       (filter #(instance? Ref %))
       set))

(defn gc
  [script]
  (let [used (set (concat (get-all-refs script)
                          (get-cached)))
        to-gc (remove used @refs-cache)]
    (reset! refs-cache used)
    (concat script (map ->Free to-gc))))
