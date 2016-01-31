(ns ^:figwheel-always rerenderer.types.render-result-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [rerenderer.test-utils :refer [make-component genref] :refer-macros [with-platform]]
            [rerenderer.lang.forms :refer [->Ref]]
            [rerenderer.platform.core :as p]
            [rerenderer.types.node :refer [->Node]]
            [rerenderer.types.render-result :as r]))

(def component (make-component "oval" {:x 1 :y 2}))

(def node
  (->Node [(->Node [] :stub :stub 10 20)]
          :stub :stub 30 40))

(defmethod p/render ::test
  [_]
  (r/->RenderResult :stub (genref)))

(deftest test-Component->RenderResult
  (with-platform ::test
    (testing "Get first time"
      (reset! r/cache {})
      (let [result (r/Component->RenderResult component)]
        (is (instance? r/RenderResult result))))
    (testing "Get from cache"
      (reset! r/cache {})
      (let [result-1 (r/Component->RenderResult component)
            result-2 (r/Component->RenderResult component)]
        (is (= (:canvas result-1) (:canvas result-2)))
        (is (not= (:script result-1) []))
        (is (= (:script result-2) []))))))

(deftest test-sanitize-cache!
  (reset! r/cache {})
  (with-platform ::test
    (r/Component->RenderResult component)
    (r/sanitize-cache! node)
    (is (= @r/cache {}))))
