(ns rerenderer.optimizer
  (:require [cljs.core.match :refer-macros [match]]
            [cljs.pprint :refer [pprint]]))

(defn expand-var
  "Converts vars to vals."
  [tree arg]
  (match arg
    [:var x] [:val (tree x)]
    _ arg))

(defn add-leaf
  "Adds new leaf to tree."
  [tree line]
  (let [prepaer-args (fn [args]
                       (mapv #(expand-var tree %) args))]
    (match line
      [:new result-var cls args]
      (assoc tree
        result-var [:new cls (prepaer-args args)])
      [:set var attr value]
      (update tree var conj [:set attr value])
      [:get result-var var attr]
      (assoc tree
        result-var [:get (get tree var) attr])
      [:call result-var var method args]
      (-> tree
          (assoc result-var [:call (get tree var) method
                             (prepaer-args args)])
          (update var conj [:call method (prepaer-args args)])))))

(defn build-tree
  "Builds tree for identifying unique items."
  [script]
  (reduce add-leaf {} script))

(defn ordered-vars
  "Returns list of vars by creation order."
  [script]
  (let [creational? #{:new :get :call}]
    (for [[method result-var] script
          :when (creational? method)]
      result-var)))

(defn get-new-cache
  "Updates cache with only new entries."
  [tree order]
  (->> order
       reverse
       (map #(vector (tree %) %))
       (into {})))

(defn can-be-removed?
  [[_ var & _] created cache tree]
  (and (cache tree var) (not (created var))))

(defn replace-with-cached
  [script created cache tree]
  (let [try-cache #(if (created %)
                    %
                    (get cache (tree %) %))
        try-cache-args #(for [arg %]
                         (match arg
                           [:var x] [:var (try-cache x)]
                           arg arg))]
    (for [line script
          :when (not (can-be-removed? line created cache tree))]
      (match line
        [:get result-var var attr]
        [:get result-var (try-cache var) attr]

        [:call result-var var method args]
        [:call result-var (try-cache var) method (try-cache-args args)]

        [:new result-var cls args]
        [:new result-var cls (try-cache-args args)]

        line line))))

(defn get-created
  [cache new-cache]
  (set (for [[k v] new-cache
             :when (not (get cache k))]
         v)))

(defn reuse
  [cache script root-id]
  (let [tree (build-tree script)
        new-cache (get-new-cache tree (ordered-vars script))
        created (get-created cache new-cache)
        cache (merge new-cache cache)
        root-id (get cache (get tree root-id) root-id)]
    [cache (replace-with-cached script created cache tree) root-id]))
