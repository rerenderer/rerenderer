(ns ^:figwheel-always rerenderer.platform.android.core-test
  (:require [cljs.test :refer-macros [deftest is]]
            [cljs.core.async :refer [chan]]
            [rerenderer.test-utils :refer-macros [with-platform script-of match?] :refer [genref]]
            [rerenderer.lang.core :include-macros true :as r]
            [rerenderer.lang.forms :refer [serialize Ref ->Ref]]
            [rerenderer.types.component :refer [IComponent]]
            [rerenderer.types.render-result :refer [RenderResult]]
            [rerenderer.types.node :refer [->Node]]
            [rerenderer.platform.android.core :refer [IAndroid]]
            [rerenderer.platform.android.bus :refer [interprete! information]]
            [rerenderer.platform.android.events :refer [bind-event!]]
            [rerenderer.platform.core :as p]))

(deftest test-apply-script!
  (with-platform :android
    (let [script (script-of (r/new Bitmap [1 2]))
          root (genref)]
      (with-redefs [interprete! (fn [script- root-]
                                  (is (= (mapv serialize script) script-))
                                  (is (= (serialize root) root-)))]
        (p/apply-script! (mapv serialize script) (serialize root))))))

(deftest test-listen!
  (with-platform :android
    (let [ch (chan)]
      (with-redefs [bind-event! (fn [ch-] (is (= ch ch-)))]
        (p/listen! ch {})))))

(deftest test-render
  (with-platform :android
    (let [component (reify
                      IComponent
                      (tag [_] "test")
                      (childs [_] [])
                      (props [_] {:x 10 :y 10 :width 20 :height 20})
                      IAndroid
                      (render-android [_ canvas]
                        (let [paint (r/new Paint)]
                          (r/.. paint (setARGB 255 255 0 0))
                          (r/.. canvas (drawRect 0 0 10 10 paint)))))
          result (p/render component)
          ; It's simple to test serialized version of big script
          script (mapv serialize (:script result))]
      (is (instance? RenderResult result))
      (is (instance? Ref (:canvas result)))
      (is (match? script
            [[:get [:ref _] [:static "android"] "graphics"]
             [:get [:ref _] [:ref _] "Bitmap$Config"]
             [:get [:ref _] [:ref _] "ARGB_8888"]
             [:get [:ref _] [:static "android"] "graphics"]
             [:get [:ref _] [:ref _] "Bitmap"]
             [:call [:ref _] [:ref _] "createBitmap" [[:val 20] [:val 20] [:ref _]]]
             [:get [:ref _] [:static "android"] "graphics"]
             [:get [:ref _] [:ref _] "Canvas"]
             [:new [:ref _] [:ref _] [[:ref _]]]
             [:new [:ref _] [:static "Paint"] []]
             [:call [:ref _] [:ref _] "setARGB" [[:val 255] [:val 255] [:val 0] [:val 0]]]
             [:call [:ref _] [:ref _] "drawRect" [[:val 0] [:val 0] [:val 10] [:val 10] [:ref _]]]])))))

(deftest test-render-to
  (with-platform :android
    (let [child-node (->Node [] (script-of (r/new Bitmap))
                             (->Ref "x") 10 20)
          parent-node (->Node [child-node] (script-of (r/new Bitmap))
                              (->Ref "y") 20 30)
          ; It's simple to test serialized version of big script
          script (mapv serialize (p/render-to child-node parent-node))]
      (is (match? script
            [[:get [:ref _] [:static "android"] "graphics"]
            [:get [:ref _] [:ref _] "Paint"]
            [:new [:ref _] [:ref _] []]
            [:get [:ref _] [:static "android"] "graphics"]
            [:get [:ref _] [:ref _] "Canvas"]
            [:new [:ref _] [:ref _] [[:ref _]]]
            [:call [:ref _] [:ref _] "drawBitmap" [[:ref _] [:val 10] [:val 20] [:ref _]]]])))))

(deftest test-information
  (with-platform :android
    (is (= (p/information nil) @information))))
