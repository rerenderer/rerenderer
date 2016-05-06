(ns rerenderer.primitives-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [rerenderer.test-utils :refer [browser-pixel-color browser-canvas]]
            [rerenderer.platform.android.core :refer [android-primitive]]
            [rerenderer.platform.browser.core :refer [render-browser]]
            [rerenderer.component :as c]
            [rerenderer.primitives :as p]))

(deftest test-rectangle
  (let [rect (p/rectangle {:width 100
                           :height 200
                           :color :red})]
    (testing "Properties"
      (is (= (c/props rect)) {:width 100
                              :height 200
                              :color [255 255 0 0]
                              :x 0
                              :y 0}))
    (testing "Browser"
      (let [canvas (browser-canvas 800 600)
            ctx (.getContext canvas "2d")]
        (render-browser rect ctx)
        (is (= (browser-pixel-color canvas 20 30) [255 0 0]))))
    (testing "Android"
      (is (= (android-primitive rect) "bundled.rectangle")))))

(deftest test-text
  (let [text (p/text {:width 200
                      :height 30
                      :value "TEST TEXT"
                      :font-size 16
                      :color :red})]
    (testing "Properties"
      (is (= (c/props text) {:width 200
                             :height 30
                             :value "TEST TEXT"
                             :color [255 0 0 255]
                             :font-size 16
                             :x 0
                             :y 0})))
    (testing "Browser"
      (let [canvas (browser-canvas 800 600)
            ctx (.getContext canvas "2d")]
        (render-browser text ctx)
        (is (= (browser-pixel-color canvas 1 1) [0 0 0]))
        (is (= (browser-pixel-color canvas 1 5) [255 0 0]))))
    (testing "Android"
      (is (= (android-primitive text) "bundled.text")))))

(deftest test-image
  (let [img (p/image {:width 800
                      :height 600
                      :src "/test.png"})]
    (testing "Properties"
      (is (= (c/props img) {:width 800
                            :height 600
                            :src "http://localhost:3449/test.png"
                            :x 0
                            :y 0
                            :sx 0
                            :sy 0})))
    (testing "Browser"
      (let [canvas (browser-canvas 800 600)
            ctx (.getContext canvas "2d")]
        (render-browser img ctx)
        (.appendChild js/document.body canvas)
        (is (= (browser-pixel-color canvas 1 1) [72 0 255]))))
    (testing "Android"
      (is (= (android-primitive img) "bundled.image")))))
