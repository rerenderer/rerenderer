(ns ^:figwheel-always rerenderer.lang.gc-test
  (:require-macros [rerenderer.test-utils :refer [script-of]])
  (:require [cljs.test :refer-macros [deftest is testing]]
            [rerenderer.lang.core :include-macros true :as r]
            [rerenderer.lang.forms :as f]
            [rerenderer.lang.gc :as gc]
            [rerenderer.lang.utils :refer [get-all-refs]]))

(deftest test-gc
  (reset! gc/refs-cache [])
  (let [script (script-of (r/new Canvas [20 30]))
        script-2 (script-of (r/new Canvas [30 40]))]
    (testing "First run"
      (is (= script (gc/gc script))))
    (testing "Second run with same"
      (is (= script (gc/gc script))))
    (testing "Second run with different"
      (let [gced (gc/gc script-2)
            refs (get-all-refs script)]
        (is (= (filter #(instance? f/Free %) gced)
               (mapv f/->Free refs)))
        (is (= (remove #(instance? f/Free %) gced)
               script-2))))))
