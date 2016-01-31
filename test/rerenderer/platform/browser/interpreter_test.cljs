(ns ^:figwheel-always rerenderer.platform.browser.interpreter-test
  (:require [cljs.test :refer-macros [deftest testing is are]]
            [rerenderer.platform.browser.interpreter :as i]))

(deftest test-extract-var
  (are [refs var result] (= (i/extract-var refs var) result)
    {"y" 12} [:ref "y"] 12
    {} [:val 50] 50))

(deftest test-create-instance
  (let [refs (i/create-instance {} [:ref "x"] "Canvas" [])]
    (is (= (.-tagName (refs "x")) "CANVAS"))))

(deftest test-set-attr
  (let [refs {"x" #js {}
              "z" #js {}}]
    (testing "Set value"
      (is (= refs (i/set-attr refs [:ref "x"] "y" [:val 50])))
      (is (= (.-y (refs "x")) 50)))
    (testing "Set reference"
      (is (= refs (i/set-attr refs [:ref "z"] "y" [:ref "x"])))
      (is (= (.-y (refs "z")) (refs "x"))))))

(deftest test-get-attr
  (testing "Get from ref"
    (let [refs {"x" #js {"a" 12}}
          changed-ref (i/get-attr refs [:ref "y"] [:ref "x"] "a")]
      (is (= (changed-ref "y") 12))))
  (testing "Get from value"
    (let [refs (i/get-attr {} [:ref "v"] [:val "test"] "length")]
      (is (= (refs "v") 4)))))

(deftest test-call-method
  (testing "Call ref method"
    (let [refs {"x" #js {"m" (fn [a b] (+ a b))}
                "a" 20}
          changed-refs (i/call-method refs [:ref "y"] [:ref "x"] "m" [[:val 15] [:ref "a"]])]
      (is (= (changed-refs "y") 35))))
  (testing "Call value method"
    (let [refs (i/call-method {} [:ref "a"] [:val #js [1 2]] "concat" [[:val 3]])]
      (is (= (js->clj (refs "a")) [1 2 3])))))

(deftest test-free
  (is (= (i/free {"x" 1} [:ref "x"]) {})))

(deftest test-interprete-instruction
  (testing ":new"
    (let [refs (i/interprete-instruction {}
                 [:new [:ref "x"] "Canvas" []])]
      (is (= (.-tagName (refs "x")) "CANVAS"))))
  (testing ":set"
    (let [refs {"x" #js {}}
          refs (i/interprete-instruction refs
                 [:set [:ref "x"] "test" [:val 23]])]
      (is (= (.-test (refs "x")) 23))))
  (testing ":get"
    (let [refs (i/interprete-instruction {"a" #js {"test" "test"}}
                 [:get [:ref "b"] [:ref "a"] "test"])]
      (is (= (refs "b") "test"))))
  (testing ":call"
    (let [refs (i/interprete-instruction {"a" #js {"test" (fn [a b] (+ a b))}
                                          "b" 55}
                 [:call [:ref "c"] [:ref "a"] "test" [[:val 10] [:ref "b"]]])]
      (is (= (refs "c") 65)))))

(deftest test-interprete
  (reset! i/refs-cache {"a" 10
                        "b" #js {"method" (fn [a b] (+ a b))}})
  (i/interprete! [[:new [:ref "x"] "Canvas" []]
                  [:set [:ref "b"] "z" [:ref "a"]]
                  [:get [:ref "m"] [:ref "b"] "z"]
                  [:call [:ref "z"] [:ref "b"] "method" [[:val 2] [:ref "a"]]]
                  [:free [:ref "a"]]])
  (is (nil? (@i/refs-cache "a")))
  (is (= (.-tagName (@i/refs-cache "x")) "CANVAS"))
  (is (= (.-z (@i/refs-cache "b")) 10))
  (is (= (@i/refs-cache "m") 10))
  (is (= (@i/refs-cache "z") 12)))
