(ns ^:figwheel-always rerenderer.platform.android.bus-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [rerenderer.test-utils :refer-macros [script-of with-platform]]
            [rerenderer.lang.core :include-macros true :as r]
            [rerenderer.lang.forms :refer [serialize]]
            [rerenderer.lang.utils :refer [get-all-refs]]
            [rerenderer.platform.utils :refer [to-json from-json]]
            [rerenderer.platform.android.bus :as b]))

(deftest test-interprete!
  (let [serialised-data (atom nil)
        script (script-of (r/new Bitmap))
        [ref] (vec (get-all-refs script))
        script (mapv serialize script)]
    (set! (.-android js/window) #js {:interprete #(reset! serialised-data %)})
    (with-platform :android
      (b/interprete! script [:ref (:id ref)])
      (is (= @serialised-data
             (to-json {:script [[:new [:ref (:id ref)] "Bitmap" []]]
                         :root [:ref (:id ref)]}))))))

(deftest test-on-event!
  (let [events (atom [])
        listener #(swap! events conj %)]
    (b/on-event! listener)
    (.androidEventsCallback js/window (to-json {:event "click" :x 10 :y 20}))
    (.androidEventsCallback js/window (to-json {:event "keyup" :keycode 23}))
    (is (= @events [{:event "click" :x 10 :y 20}
                    {:event "keyup" :keycode 23}]))))

(deftest test-available?
  (testing "When not available"
    (set! (.-android js/window) nil)
    (is (not (b/available?))))
  (testing "When available"
    (set! (.-android js/window) #js {})
    (is (b/available?))))
