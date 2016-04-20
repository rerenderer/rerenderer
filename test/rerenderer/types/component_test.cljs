(ns ^:figwheel-always rerenderer.types.component-test
  (:require [cljs.test :refer-macros [deftest is are]]
            [rerenderer.test-utils :refer [make-component]]
            [rerenderer.types.component :as c]))

(def tree (make-component "rect" {:x 1 :y 2}
            (make-component "oval" {:z 4 :color "red"}
              (make-component "text" {:value "test"}))
            (make-component "link" {:href :test})))

(def tree-text
  (str "(rect {:x 1, :y 2}\n",
       "    (oval {:z 4, :color \"red\"}\n",
       "        (text {:value \"test\"}))\n",
       "    (link {:href :test}))"))

(def tree-path
  (str "rect:{}:["
       "oval:{:z 4, :color \"red\"}:["
       "text:{:value \"test\"}:[]"
       "]:link:{:href :test}:[]]"))

(deftest test-component->string
  (is (= (c/component->string tree) tree-text)))

(deftest test-calculate-path
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
