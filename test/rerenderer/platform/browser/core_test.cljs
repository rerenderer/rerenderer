(ns ^:figwheel-always rerenderer.platform.browser.core-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [cljs.core.async :refer [chan]]
            [rerenderer.test-utils :refer-macros [with-platform match?]
             :refer [browser-pixel-color]]
            [rerenderer.component :refer [IComponent]]
            [rerenderer.primitives :refer [rectangle]]
            [rerenderer.platform.browser.core :refer [IBrowser]]
            [rerenderer.platform.browser.events :refer [bind-events!]]
            [rerenderer.platform.core :as p]))

(deftest test-render
  (with-platform :browser
    (let [tree (rectangle {:color "red" :width 100 :height 100})
          canvas (.createElement js/document "canvas")]
      (p/render tree {:canvas canvas})
      (is (= (browser-pixel-color canvas 5 5) [255 0 0])))))

(deftest test-listen!
  (with-platform :browser
    (let [ch (chan)
          canvas (.createElement js/document "canvas")]
      (with-redefs [bind-events! (fn [ch- canvas-]
                                   (is (= ch ch-))
                                   (is (= canvas canvas-)))]
        (p/listen! ch {:canvas canvas})))))

(deftest test-information
  (with-platform :browser
    (let [canvas (.createElement js/document "canvas")]
      (set! (.-width canvas) 200)
      (set! (.-height canvas) 100)
      (is (= (p/information {:canvas canvas})
             {:width 200
              :height 100
              :input #{:mouse :keyboard}})))))
