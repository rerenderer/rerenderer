(ns ^:figwheel-always rerenderer.lang.core-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [rerenderer.lang.forms :as f]
            [rerenderer.lang.core :include-macros true :as r]))

(deftest test-new
  (testing "With val args"
    (r/recording script
      (let [result (r/new Canvas 10 20)]
        (is (instance? f/Ref result))
        (is (= @script
               [(f/->New result "Canvas" [(f/->Val 10) (f/->Val 20)])])))))
  (testing "With mixed args"
    (r/recording script
      (let [obj (f/->Ref (gensym))
            result (r/new Canvas obj 2)]
        (is (instance? f/Ref result))
        (is (= @script
               [(f/->New result "Canvas" [obj (f/->Val 2)])])))))
  (testing "Without args"
    (r/recording script
      (let [result (r/new Canvas)]
        (is (instance? f/Ref result))
        (is (= @script
               [(f/->New result "Canvas" [])]))))))

(deftest test-dot-dot-macro
  (testing "First-level attriubte"
    (r/recording script
      (let [obj (f/->Ref (gensym))
            result (r/.. obj -x)]
        (is (instance? f/Ref result))
        (is (= @script
               [(f/->Get result obj "x")])))))
  (testing "First-level method"
    (r/recording script
      (let [obj (f/->Ref (gensym))
            arg (f/->Ref (gensym))
            result (r/.. obj (getX 1 arg))]
        (is (instance? f/Ref result))
        (is (= @script
               [(f/->Call result obj "getX" [(f/->Val 1) arg])])))))
  (testing "First-level method without args"
    (r/recording script
      (let [obj (f/->Ref (gensym))
            result (r/.. obj getY)]
        (is (instance? f/Ref result))
        (is (= @script
               [(f/->Call result obj "getY" [])])))))
  (testing "Multi-leve mixed"
    (r/recording script
      (let [obj (f/->Ref (gensym))
            arg (f/->Ref (gensym))
            result (r/.. obj (getAt 15 arg) -box getOffset -left)
            refs (mapv :result-ref @script)]
        (is (instance? f/Ref result))
        (is (= @script
               [(f/->Call (first refs) obj "getAt" [(f/->Val 15) arg])
                (f/->Get (second refs) (first refs) "box")
                (f/->Call (get refs 2) (second refs) "getOffset" [])
                (f/->Get result (get refs 2) "left")])))))
  (testing "Static multi-level mixed"
    (r/recording script
      (let [arg (f/->Ref (gensym))
            result (r/.. Canvas (getAt 15 arg) -box getOffset -left)
            refs (mapv :result-ref @script)]
        (is (instance? f/Ref result))
        (is (= @script
               [(f/->Call (first refs) (f/->Ref ":Canvas") "getAt" [(f/->Val 15) arg])
                (f/->Get (second refs) (first refs) "box")
                (f/->Call (get refs 2) (second refs) "getOffset" [])
                (f/->Get result (get refs 2) "left")]))))))

(deftest test-set!
  (testing "First-level val"
    (r/recording script
      (let [obj (f/->Ref (gensym))]
        (r/set! (r/.. obj -x) 23)
        (is (= @script
               [(f/->Set obj "x" (f/->Val 23))])))))
  (testing "Multi-level ref"
    (r/recording script
      (let [obj (f/->Ref (gensym))
            arg (f/->Ref (gensym))
            ref (f/->Ref (gensym))]
        (r/set! (r/.. obj (getHandler 12 arg) getFirst -point -y) ref)
        (let [refs (mapv :result-ref @script)]
          (is (= @script
                 [(f/->Call (first refs) obj "getHandler" [(f/->Val 12) arg])
                  (f/->Call (second refs) (first refs) "getFirst" [])
                  (f/->Get (get refs 2) (second refs) "point")
                  (f/->Set (get refs 2) "y" ref)])))))))

(def v (f/->Ref "test!"))

(deftest test-wrap
  (is (= (r/wrap Canvas) (f/->Ref ":Canvas")))
  (is (= (r/wrap (f/->Ref "test")) (f/->Ref "test")))
  (let [x (f/->Ref "y")]
    (is (= (r/wrap x) (f/->Ref "y"))))
  (is (= (r/wrap v) (f/->Ref "test!"))))
