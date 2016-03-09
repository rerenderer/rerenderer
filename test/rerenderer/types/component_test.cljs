(ns ^:figwheel-always rerenderer.types.component-test
  (:require [cljs.test :refer-macros [deftest is]]
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
  (is (= (c/calculate-path tree)
         tree-path)))
