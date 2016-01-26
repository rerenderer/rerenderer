(ns rerenderer.render.node
  (:require [clojure.string :as string]
            [rerenderer.render.component :refer [tag]]))

(defprotocol ^:no-doc INode
  (path [this])
  (childs [this])
  (component [this])
  (props [this]))

(def calculate-path
  (memoize
    (fn [node]
      (string/join ":" (merge [(-> node component tag)
                               (props node)]
                              (map path (childs node)))))))

(defn flatten-childs
  [child-nodes]
  (loop [[child-node & child-nodes] child-nodes
         result []]
    (if (nil? child-node)
      result
      (if (iterable? (first child-node))
        (recur (concat child-node child-nodes) result)
        (recur child-nodes (conj result child-node))))))


(defn ->node
  [[component-fn props & child-nodes]]
  (let [component (component-fn props)
        child-nodes (map ->node (flatten-childs child-nodes))]
    (reify
      INode
      (path [this] (calculate-path this))
      (childs [_] child-nodes)
      (component [_] component)
      (props [_] props)
      Object
      (toString [_] (str "[" (tag component) " " props
                         (string/join "\n" (map str child-nodes))
                         "]")))))
