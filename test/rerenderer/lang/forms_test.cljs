(ns ^:figwheel-always rerenderer.lang.forms-test
  (:require [cljs.test :refer-macros [deftest is]]
            [rerenderer.lang.forms :as f]))

(deftest test-Ref-serialize
  (is (= (f/serialize (f/->Ref 1))
         [:ref "1"])))

(deftest test-Val-serialize
  (is (= (f/serialize (f/->Val 30))
         [:val 30])))

(deftest test-New-serialize
  (is (= (f/serialize (f/->New (f/->Ref 2) "Canvas" [(f/->Val 20) (f/->Ref 3)]))
         [:new [:ref "2"] "Canvas" [[:val 20] [:ref "3"]]])))

(deftest test-Set-serialize
  (is (= (f/serialize (f/->Set (f/->Ref 3) "x" (f/->Val 40)))
         [:set [:ref "3"] "x" [:val 40]])))

(deftest test-Get-serialize
  (is (= (f/serialize (f/->Get (f/->Ref 4) (f/->Ref 5) "y"))
         [:get [:ref "4"] [:ref "5"] "y"])))

(deftest test-Call-serialize
  (is (= (f/serialize (f/->Call (f/->Ref :x) (f/->Ref :y)
                                "getTag" [(f/->Val :a) (f/->Ref "z")]))
         [:call [:ref ":x"] [:ref ":y"] "getTag" [[:val :a] [:ref "z"]]])))

(deftest test-Free-serialize
  (is (= (f/serialize (f/->Free (f/->Ref 'x)))
         [:free [:ref "x"]])))
