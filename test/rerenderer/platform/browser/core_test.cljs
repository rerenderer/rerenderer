(ns ^:figwheel-always rerenderer.platform.browser.core-test
  (:require [cljs.test :refer-macros [deftest is]]
            [cljs.core.async :refer [chan]]
            [rerenderer.test-utils :refer-macros [with-platform script-of match?]]
            [rerenderer.lang.core :as r :include-macros true]
            [rerenderer.lang.forms :refer [serialize Ref ->Ref]]
            [rerenderer.types.component :refer [IComponent]]
            [rerenderer.types.render-result :refer [RenderResult]]
            [rerenderer.types.node :refer [->Node]]
            [rerenderer.platform.browser.core :refer [IBrowser]]
            [rerenderer.platform.browser.events :refer [bind-events!]]
            [rerenderer.platform.core :as p]))

(deftest test-allpy-script!
  (with-platform :browser
    (r/recording script
      (let [canvas (.createElement js/document "canvas")
            root (r/.. document (createElement "Canvas"))
            ctx (r/.. root (getContext "2d"))]
        (r/set! (r/.. ctx -fillStyle) "rgb(255,0,0)")
        (r/.. ctx (fillRect 0 0 10 10))
        (p/apply-script! (mapv serialize @script)
                         (serialize root)
                         {:canvas canvas})
        ; Check color of pixel:
        (let [data (.. canvas (getContext "2d") (getImageData 0 0 1 1) -data)
              color (mapv #(aget data %) (range 3))]
          (is (= color [255 0 0])))))))

(deftest test-listen!
  (with-platform :browser
    (let [ch (chan)
          canvas (.createElement js/document "canvas")]
      (with-redefs [bind-events! (fn [ch- canvas-]
                                   (is (= ch ch-))
                                   (is (= canvas canvas-)))]
        (p/listen! ch {:canvas canvas})))))

(deftest test-render
  (with-platform :browser
    (let [component (reify
                      IComponent
                      (tag [_] "test")
                      (childs [_] [])
                      (props [_] {:x 10 :y 10 :width 30 :height 40})
                      IBrowser
                      (render-browser [_ ctx]
                        (r/set! (r/.. ctx -fillStyle) "red")))
          result (p/render component)
          ; It's simple to test serialized version of big script
          script (mapv serialize (:script result))]
      (is (instance? RenderResult result))
      (is (instance? Ref (:canvas result)))
      (is (match? script
            [[:call [:ref _] [:static "document"] "createElement" [[:val "canvas"]]]
             [:call [:ref _] [:ref _] "getContext" [[:val "2d"]]]
             [:set [:ref _] "width" [:val 30]]
             [:set [:ref _] "height" [:val 40]]
             [:set [:ref _] "fillStyle" [:val "red"]]])))))


(deftest test-render-to
  (with-platform :browser
    (let [child-node (->Node [] (script-of (r/new Canvas))
                             (->Ref "x") 10 20)
          parent-node (->Node [child-node] (script-of (r/new Canvas))
                              (->Ref "y") 20 30)
          ; It's simple to test serialized version of big script
          script (mapv serialize (p/render-to child-node parent-node))]
      (is (match? script
            [[:call [:ref _] [:ref "y"] "getContext" [[:val "2d"]]]
             [:call [:ref _] [:ref _] "drawImage" [[:ref "x"] [:val 10] [:val 20]]]])))))

(deftest test-information
  (with-platform :browser
    (let [canvas (.createElement js/document "canvas")]
      (set! (.-width canvas) 200)
      (set! (.-height canvas) 100)
      (is (= (p/information {:canvas canvas})
             {:width 200
              :height 100
              :input #{:mouse :keyboard}})))))
