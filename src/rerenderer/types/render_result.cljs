(ns rerenderer.types.render-result
  (:require [rerenderer.platform.core :refer [render]]
            [rerenderer.types.component :refer [calculate-path]]))

(defrecord RenderResult [script canvas])

(def cache (atom {}))

(defn Component->RenderResult
  [component]
  (let [path (calculate-path component)
        cached (@cache path)]
    (if cached
      (->RenderResult [] cached)
      (render component))))

(defn sanitize-cache!
  [node]
  (let [paths (loop [[node & rest-nodes] [node]
                     result []]
                (if node
                  (recur (concat (:childs node) rest-nodes)
                         (conj result (:path node)))))
        used? (set paths)]
    (swap! cache #(into {} (for [[k v] %
                                 :when (used? k)]
                             [k v])))))

(defn get-cached
  []
  (map second (vals @cache)))
