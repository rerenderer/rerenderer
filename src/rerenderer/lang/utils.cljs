(ns rerenderer.lang.utils
  (:require [rerenderer.lang.forms :refer [Ref]]))

(defn get-all-refs
  "Returns all refs from script."
  [script]
  (->> (for [instruction script
             field [:result-ref :ref :args]]
         (get instruction field))
       flatten
       (filter #(instance? Ref %))
       set))
