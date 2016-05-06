(ns ^:figwheel-always rerenderer.platform.android.core-test
  (:require [cljs.test :refer-macros [deftest is]]
            [cljs.core.async :refer [chan]]
            [rerenderer.test-utils :refer-macros [with-platform]]
            [rerenderer.primitives :refer [rectangle]]
            [rerenderer.platform.android.core :refer [IAndroid serialize-component]]
            [rerenderer.platform.android.bus :refer [render! information]]
            [rerenderer.platform.android.events :refer [bind-event!]]
            [rerenderer.platform.core :as p]))

(deftest test-serialize-component
  (let [tree (rectangle {:width 100 :height 200 :x 0 :y 0 :color "red"}
               (rectangle {:width 10 :height 20 :x 30 :y 15 :color "green"}))]
    (is (= (serialize-component tree)
           ["bundled.rectangle" {:width 100 :height 200 :x 0
                                 :y 0 :color [255 0 0 255]}
            [["bundled.rectangle" {:width 10 :height 20 :x 30
                                   :y 15 :color [0 128 0 255]} []
              "rectangle:{:width 10, :height 20, :color [0 128 0 255]}:[]"]]
            "rectangle:{:width 100, :height 200, :color [255 0 0 255]}:[rectangle:{:width 10, :height 20, :color [0 128 0 255]}:[]:(30, 15)]"]))))

(deftest test-render
  (with-platform :android
    (let [tree (rectangle {:width 10 :height 20 :x 0 :y 0 :color "black"})]
      (with-redefs [render! (fn [serialised _]
                              (is (= serialised ["bundled.rectangle" {:width 10
                                                                      :height 20
                                                                      :x 0
                                                                      :y 0
                                                                      :color [0 0 0 255]}
                                                 [] "rectangle:{:width 10, :height 20, :color [0 0 0 255]}:[]"])))]
        (p/render tree {})))))

(deftest test-listen!
  (with-platform :android
    (let [ch (chan)]
      (with-redefs [bind-event! (fn [ch-] (is (= ch ch-)))]
        (p/listen! ch {})))))

(deftest test-information
  (with-platform :android
    (is (= (p/information nil) @information))))
