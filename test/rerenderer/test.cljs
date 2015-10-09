(ns ^:figwheel-always rerenderer.test
  (:require [cljs.test :refer-macros [run-all-tests]]
            ;[rerenderer.core-test]
            ;[rerenderer.browser-test]
            [rerenderer.optimizer-test]))

(enable-console-print!)

(run-all-tests #"rerenderer.*-test")
