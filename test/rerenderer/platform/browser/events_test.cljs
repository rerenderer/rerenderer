(ns ^:figwheel-always rerenderer.platform.browser.events-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [deftest is are async testing]]
            [cljs.core.async :refer [<! chan]]
            [rerenderer.platform.browser.events :as e]))

(deftest test-translate-event
  (are [event data translated] (= (e/translate-event event data)
                                  translated)
    "click" #js {:clientX 10
                 :clientY 20} [:click {:x 10 :y 20}]
    "keydown" #js {:keyCode 30} [:keydown {:keycode 30}]
    "keyup" #js {:keyCode 32} [:keyup {:keycode 32}]))

(deftest test-bind-events!
  (async done
    (let [ch (chan)
          canvas (.createElement js/document "canvas")
          emit! (fn [event] (.dispatchEvent canvas event))]
      (go (e/bind-events! ch canvas)
          (testing "Click"
            (emit! (js/MouseEvent. "click" #js {:clientX 5
                                                :clientY 10}))
            (is (= (<! ch) [:click {:x 5 :y 10}])))
          (testing "Key down"
            ; We can't create KeyboardEvent with keyCode:
            (emit! (doto (js/Event. "keydown")
                     (aset "keyCode" 30)))
            (is (= (<! ch) [:keydown {:keycode 30}])))
          (testing "Key up"
            ; We can't create KeyboardEvent with keyCode:
            (emit! (doto (js/Event. "keyup")
                     (aset "keyCode" 40)))
            (is (= (<! ch) [:keyup {:keycode 40}])))
          (done)))))
