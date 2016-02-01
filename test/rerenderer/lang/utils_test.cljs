(ns ^:figwheel-always rerenderer.lang.utils-test
  (:require [cljs.test :refer-macros [deftest is]]
            [rerenderer.lang.utils :refer [get-all-refs]]
            [rerenderer.lang.forms :as f]))

(deftest test-get-all-refs
  (let [script [(f/->New (f/->Ref "a") (f/->Static "Canvas") [(f/->Val 10) (f/->Ref "b")])
                (f/->New (f/->Ref "a") (f/->Ref "z") [(f/->Val 10) (f/->Ref "b")])
                (f/->Get (f/->Ref "c") (f/->Ref "b") "test")
                (f/->Call (f/->Ref "d") (f/->Ref "c") "test" [(f/->Ref "b") (f/->Val "x")])]]
    (is (= (get-all-refs script)
           #{(f/->Ref "a") (f/->Ref "b") (f/->Ref "z") (f/->Ref "c") (f/->Ref "d")}))))
