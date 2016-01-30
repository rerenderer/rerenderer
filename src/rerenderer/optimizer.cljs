(ns rerenderer.optimizer
  (:require [cljs.core.match :refer-macros [match]]
            [cljs.pprint :refer [pprint]]))

(def nodes-cache (atom {}))

(defn update-cache!
  [node]
  (loop [[node & rest-nodes] [node]]
    (when node
      (swap! nodes-cache assoc (:path node) (:canvas node))
      (recur (concat (:childs node) rest-nodes)))))

(def vars-cache (atom []))

(defn var-ids-from-args
  [args]
  (for [[type value] args
        :when (= :var type)]
    value))

(defn get-var-ids
  [script]
  (set (flatten (for [line script]
                  (match line
                    [:get result-var var & _] [result-var var]
                    [:set var & _] var
                    [:new result-var _ args] [result-var (var-ids-from-args args)]
                    [:call result-var var _ args] [result-var var (var-ids-from-args args)])))))

(defn gc
  [script]
  (let [used (set (concat (get-var-ids script)
                          (vals @nodes-cache)))
        to-gc (remove used @vars-cache)]
    (reset! vars-cache used)
    (concat script (for [var-id to-gc]
                     [:free var-id]))))
