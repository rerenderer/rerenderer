(ns rerenderer.browser-test
  (:require [cljs.test :refer-macros [deftest is async]]
            [rerenderer.core :as c :include-macros true]
            [rerenderer.browser :as b]))

;(deftest test-browser-call!
;  (let [calls (atom [])
;        obj #js {:a (fn [x y] (swap! calls conj [:a x y]))
;                 :b #js {:c (fn [_] (swap! calls conj [:b :c]))}}]
;    (b/browser-call! obj [["a" 1 2]])
;    (b/browser-call! obj [["-b"] ["c"]])
;    (is (= @calls [[:a 1 2] [:b :c]]))))
;
;(deftest test-browser-set!
;  (let [obj #js {:x #js {}}]
;    (b/browser-set! obj [["-a"]] 10)
;    (b/browser-set! obj [["-x"] ["-c"]] 15)
;    (is (= 10 (aget obj "a")))
;    (is (= 15 (aget (aget obj "x") "c")))))
;
;(deftest test-browser-render!
;  (let [calls (atom [])
;        context #js {:fnc (fn [& args]
;                            (swap! calls conj args))}
;        canvas #js {:getContext (fn [_] context)}
;        platform (b/browser canvas)
;        ctx (atom [])]
;    (c/.. ctx (fnc 1 2 3))
;    (c/set! (.. ctx -attr) "val")
;    (c/render! platform @ctx)
;    (is (= @calls [[1 2 3]]))
;    (is (= (aget context "attr") "val"))))
