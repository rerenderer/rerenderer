(ns rerenderer.v2
  ^:figwheel-always
  (:require [clojure.string :as string]))

(enable-console-print!)

(defprotocol INode
  (path [this])
  (childs [this])
  (component [this])
  (props [this]))

(defprotocol IComponent
  (tag [this]))

(def calculate-path
  (memoize
    (fn [node]
      (string/join ":" (merge [(-> node component tag)
                               (props node)]
                              (map path (childs node)))))))

(defn vec->node
  [[component-fn props childs]]
  (let [component (component-fn childs)
        childs (map (flatten childs) childs)]
    (reify
      INode
      (path [this] (calculate-path this))
      (childs [_] childs)
      (component [_] component)
      (props [_] props)
      Object
      (toString [this] (str "[" (-> this component tag)
                            (string/join "\n" (->> this childs (map str)))
                            "]")))))

(defrecord Rect [props]
  IComponent
  (tag [_] "rect")
  Object
  (toString [this] (str "[" (tag this) " " props "]")))
