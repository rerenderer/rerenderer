(ns rerenderer.lang.gc
  (:require [cljs.core.match :refer-macros [match]]
            [rerenderer.lang.forms :refer [->Free]]
            [rerenderer.lang.utils :refer [get-all-refs]]))

(def ^:private refs-cache (atom []))

(defn gc
  "Add `Free` instructions for old refs to script."
  [script]
  (let [used (set (get-all-refs script))
        to-gc (remove used @refs-cache)]
    (reset! refs-cache used)
    (concat script (map ->Free to-gc))))
