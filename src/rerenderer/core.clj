(ns rerenderer.core
  (:refer-clojure :exclude [.. set!]))

(defn path-to-vec
  [path]
  (mapv #(cond
          (symbol? %) [(str %)]
          (list? %) (apply vector (str (first %)) (rest %)))
        path))

(defmacro ..
  [ctx & path]
  (let [path [:call (path-to-vec path)]]
    `(swap! ~ctx conj ~path)))

(defmacro set!
  [path value]
  (let [ctx (second path)
        path [:set (path-to-vec (drop 2 path)) value]]
    `(swap! ~ctx conj ~path)))
