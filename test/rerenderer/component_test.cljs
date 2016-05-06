(ns ^:figwheel-always rerenderer.component-test
  (:require [cljs.test :refer-macros [deftest is are]]
            [rerenderer.test-utils :refer [make-component]]
            [rerenderer.component :as c]))

(def tree (make-component "rect" {:x 1 :y 2}
            (make-component "oval" {:z 4 :color "red" :x 10 :y 20}
              (make-component "text" {:value "test" :x 30 :y 40}))
            (make-component "link" {:href :test :x 50 :y 60})))

(def tree-text
  (str "(rect {:x 1, :y 2}\n"
       "    (oval {:z 4, :color \"red\", :x 10, :y 20}\n"
       "        (text {:value \"test\", :x 30, :y 40}))\n"
       "    (link {:href :test, :x 50, :y 60}))"))

(def tree-path
  (str "rect:{}:[oval:{:z 4, :color \"red\"}:"
       "[text:{:value \"test\"}:[]:(30, 40)]:"
       "(10, 20):link:{:href :test}:[]:(50, 60)]"))

(deftest test-component->string
  (is (= (c/component->string tree) tree-text)))

(deftest test-path
  (is (= (c/path tree)
         tree-path)))

(deftest test-prepare-childs
  (is (= (c/prepare-childs [nil :a [nil :b] [:c [nil]]])
         [:a :b :c])))

(deftest test->rgba
  (are [before after] (= (c/->rgba before) after)
    "#ff00ff" [255 0 255 255]
    "red" [255 0 0 255]
    "rgb(0,255,0)" [0 255 0 255]))

(deftest test->url
  (are [before after] (= (c/->url before) after)
    "/img.jpg" "http://localhost:3449/img.jpg"
    "https://test.com/img.png" "https://test.com/img.png"))
