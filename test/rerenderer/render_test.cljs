(ns rerenderer.render-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [cljs.core.match :refer-macros [match]]
            [rerenderer.test-utils :refer-macros [with-platform script-of]
             :refer [make-component genref]]
            [rerenderer.types.node :refer [->Node]]
            [rerenderer.types.render-result :refer [->RenderResult cache]]
            [rerenderer.types.component :as cmp]
            [rerenderer.lang.core :include-macros true :as l]
            [rerenderer.lang.forms :as f]
            [rerenderer.lang.gc :as gc]
            [rerenderer.platform.core :as p]
            [rerenderer.render :as r]))

(def scripts [(script-of (l/new Canvas 3 4))
              (script-of (l/new Canvas 1 2))])

(def canvases [(genref) (genref)])

(def remote (genref))

(def node (->Node [(->Node [] (first scripts) (first canvases) 1 2)]
                  (second scripts) (second canvases) 3 4))

(def component (make-component "rect" {:x 1 :y 2}
                 (make-component "oval" {:x 3 :y 4})))

(defmethod p/render-to ::test
  [child parent]
  (script-of (l/.. (:canvas parent) (render (:canvas child)))))

(defmethod p/render ::test
  [component]
  (let [tag (cmp/tag component)]
    (->RenderResult (script-of (l/.. remote (render tag)))
                    (genref))))

(defmethod p/apply-script! ::test
  [script root-ref _]
  [script root-ref])

(deftest test-render-node
  (with-platform ::test
    (let [script (r/render-node node)
          refs (vec (gc/get-all-refs script))]
      (is (= script
             [(f/->New (get refs 0) "Canvas" [(f/->Val 1) (f/->Val 2)])
              (f/->New (get refs 1) "Canvas" [(f/->Val 3) (f/->Val 4)])
              (f/->Call (get refs 2) (second canvases) "render" [(first canvases)])])))))

(deftest test-render-component!
  (with-platform ::test
    (reset! gc/refs-cache [])
    (reset! cache {})
    (let [[ser-script root-ref] (r/render-component! component {})
          ser-script (vec ser-script)]
      (is (match ser-script
            [[:call [:ref _] [:ref _] "render" [[:val "rect"]]]
             [:call [:ref _] [:ref _] "render" [[:val "oval"]]]
             [:call [:ref _] [:ref _] "render" [[:ref _]]]] true
            _ false))
      (is (match root-ref
            [:ref _] true
            _ false)))))
