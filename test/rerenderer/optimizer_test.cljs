(ns ^:figwheel-always rerenderer.optimizer-test
  (:require [cljs.test :refer-macros [deftest is testing are]]
            [rerenderer.optimizer :as o]))

(deftest test-expand-var
  (are [tree arg result] (= (o/expand-var tree arg) result)
    {:x :test} [:var :x] [:val :test]
    {} [:val :test] [:val :test]))

(deftest test-add-leaf
  (are [tree line result] (= (o/add-leaf tree line) result)
    {:x :test} [:new :z 'Canvas [[:var :x] [:val :y]]] {:x :test
                                                        :z '[:new Canvas ([:val :test] [:val :y])]}
    {:x []} [:set :x :color :red] {:x [[:set :color :red]]}
    {:x :test} [:get :y :x :color] {:x :test
                                    :y [:get :test :color]}
    {:x []
     :y []} [:call :z :x :getColor [[:var :y] [:val :test]]] {:x [[:call :getColor [[:val []] [:val :test]]]]
                                                              :y []
                                                              :z [:call [] :getColor [[:val []] [:val :test]]]}))

(def script [[:new :x 'Canvas [[:val 300] [:vall 600]]]
             [:set :x :color [:var :red]]
             [:call :y :x :getContext []]
             [:get :z :x :size]
             [:call :a :x :setSize [[:var :z] [:val 12]]]])

(deftest test-build-tree
  (is (= (o/build-tree script)
         {:x [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []] [:call :setSize [[:val [:get [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []]] :size]] [:val 12]]]]
          :y [:call [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]]] :getContext []]
          :z [:get [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []]] :size]
          :a [:call [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []]] :setSize [[:val [:get [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []]] :size]] [:val 12]]]})))

(deftest test-ordered-vars
  (is (= (o/ordered-vars script) [:x :y :z :a])))

(deftest test-update-cache
  (is (= (o/get-new-cache (o/build-tree script) (o/ordered-vars script))
         {[:call [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []]] :setSize [[:val [:get [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []]] :size]] [:val 12]]] :a
          [:get [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []]] :size] :z
          [:call [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]]] :getContext []] :y
          [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []] [:call :setSize [[:val [:get [:new 'Canvas [[:val 300] [:vall 600]] [:set :color [:var :red]] [:call :getContext []]] :size]] [:val 12]]]] :x})))

(deftest test-can-be-removed
  (are [line created cache result] (= (o/can-be-removed? line
                                                         (constantly created)
                                                         (constantly cache)
                                                         identity)
                                      result)
    [:new :x 'Canvas []] true true false
    [:new :x 'Canvas []] false true true
    [:new :x 'Canvas []] true false false
    [:new :x 'Canvas []] false false false))
