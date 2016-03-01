(ns ^:figwheel-always rerenderer.platform.utils-test
  (:require [cljs.test :refer-macros [deftest is]]
            [rerenderer.platform.utils :refer [to-json from-json]]))

(deftest test-from-json
  (is (= (from-json "{\"y\": 20, \"x\": 10, \"event\": \"click\"}")
         {:event "click"
          :x 10
          :y 20})))

(deftest test-to-json
  (is (= (to-json [:new [:ref "x"] "Canvas" [10 20]])
         "[\"new\",[\"ref\",\"x\"],\"Canvas\",[10,20]]")))
