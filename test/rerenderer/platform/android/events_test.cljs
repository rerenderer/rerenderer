(ns ^:figwheel-always rerenderer.platform.android.events-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [deftest is are async]]
            [cljs.core.async :refer [chan <!]]
            [rerenderer.platform.android.events :refer [translate-event bind-event!]]
            [rerenderer.platform.utils :refer [to-json]]))

(deftest test-translate-event
  (are [event translated] (= (translate-event event) translated)
    {:event "click" :x 10 :y 20} [:click {:x 10 :y 20}]
    {:event "keyup" :keycode 23} [:keyup {:keycode 23}]
    {:event "keydown" :keycode 40} [:keydown {:keycode 40}]))

(deftest test-bind-event!
  (async done
    (let [ch (chan)]
      (bind-event! ch)
      (.androidEventsCallback js/window (to-json {:event "click" :x 10 :y 20}))
      (.androidEventsCallback js/window (to-json {:event "keyup" :keycode 23}))
      (go (is (= (<! ch) [:click {:x 10 :y 20}]))
          (is (= (<! ch) [:keyup {:keycode 23}]))
          (done)))))
