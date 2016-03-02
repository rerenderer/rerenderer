(ns ^:figwheel-always rerenderer.debug-test
  (:require [cljs.test :refer-macros [deftest is]]
            [rerenderer.core :refer [->Game]]
            [rerenderer.debug :as d]))

(deftest test-rerender!
  (let [state-atom (atom {})
        game (->Game state-atom nil nil nil nil)
        flag (atom false)]
    (add-watch state-atom :rendered #(reset! flag true))
    (d/rerender! game)
    (is (true? @flag))))

(deftest test-swap-state!
  (let [state-atom (atom {:x 1})
        game (->Game state-atom nil nil nil nil)]
    (d/swap-state! game assoc :y 2)
    (is (= @state-atom {:x 1 :y 2}))))

(deftest test-reset-state!
  (let [state-atom (atom {:x 1})
        game (->Game state-atom nil nil nil nil)]
    (d/reset-state! game {:y 2})
    (is (= @state-atom {:y 2}))))

(deftest test-reset-state-to-initial!
  (let [state-atom (atom {:x 1})
        initial-state {:test true}
        game (->Game state-atom initial-state nil nil nil)]
    (d/reset-state-to-initial! game)
    (is (= @state-atom initial-state))))
