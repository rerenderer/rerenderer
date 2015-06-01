(ns rerenderer.core-test
  (:require [cljs.test :refer-macros [deftest is]]
            [rerenderer.core :as c :include-macros true]))

(deftest test-dot-dot
  (let [ctx (atom [])]
    (c/.. ctx (clearRect 100 10))
    (c/.. ctx -drawer (draw 10 10 :red))
    (c/.. ctx getDrawer (setColor :white))
    (is (= @ctx [[:call [["clearRect" 100 10]]]
                 [:call [["-drawer"] ["draw" 10 10 :red]]]
                 [:call [["getDrawer"] ["setColor" :white]]]]))))

(deftest test-set!
  (let [ctx (atom [])]
    (c/set! (.. ctx -color) :red)
    (c/set! (.. ctx (getDrawer 2) -x) 100)
    (c/set! (.. ctx -drawer -axis) 90)
    (is (= @ctx [[:set [["-color"]] :red]
                 [:set [["getDrawer" 2] ["-x"]] 100]
                 [:set [["-drawer"] ["-axis"]] 90]]))))

(deftest test-render-state!
  (let [rendered (atom 0)
        ctx (atom [])
        dummy (reify c/IPlatform
                (render! [_ _] (swap! rendered inc)))
        root (fn [ctx _] (c/.. ctx test))]
    (c/render-state! dummy {} root ctx)
    (is (= @rendered 1))
    (c/render-state! dummy {} root ctx)
    (is (= @rendered 1))))
