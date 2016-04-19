(ns ^:figwheel-always rerenderer.types.node-test
  (:require [cljs.test :refer-macros [deftest is]]
            [rerenderer.test-utils :refer [make-component]]
            [rerenderer.types.node :refer [Component->Node ->Node]]
            [rerenderer.types.render-result :refer [Component->RenderResult ->RenderResult]]))

(def component (make-component "rect" {:x 1 :y 2}
                 (make-component "oval" {:x 3 :y 4}
                   (make-component "line" {:x 5 :y 12}))))

(deftest test-Component->Node
  (with-redefs [Component->RenderResult #(->RenderResult :stub :stub)]
    (is (= (Component->Node component)
           (->Node [(->Node [(->Node [] :stub :stub 5 12)]
                            :stub :stub 3 4)]
                   :stub :stub 1 2)))))
