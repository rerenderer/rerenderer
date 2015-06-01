(ns rerenderer.test
  (:require [cljs.test :refer-macros [run-all-tests]]
            [rerenderer.core-test]
            [rerenderer.browser-test]))

(enable-console-print!)

(defn run [] (run-all-tests #"rerenderer.*-test"))
