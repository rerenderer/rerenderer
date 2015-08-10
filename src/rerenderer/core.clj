(ns rerenderer.core
  (:refer-clojure :exclude [new .. set!])
  (:require [clojure.string :refer [join]]))

(defmacro new
  [cls & args]
  `(rerenderer.core/rnew ~(keyword (name cls)) ~@args))

(defn attr-to-str
  [attr]
  (-> attr name rest join))

(defn dot
  [x form]
  (if (symbol? form)
    (if (= (first (name form)) "-")
      `(rerenderer.core/rget ~x ~(attr-to-str form))
      `(rerenderer.core/rcall! ~x ~(name form)))
    `(rerenderer.core/rcall! ~x ~(name (first form)) ~@(rest form))))

(defmacro ..
  "Usage:

    ```
    (r/.. canvas (getContext \"2d\"))
    (r/.. image -onLoad (bind (fn [])))
    ```"
  ([x form] (dot x form))
  ([x form & more] `(rerenderer.core/.. ~(dot x form) ~@more)))

(defmacro set!
  [path value]
  (let [attr (-> path last name rest join)
        path (-> path butlast rest)
        obj (if (> (count path) 2)
              `(rerenderer.core/.. ~@path)
              (first path))]
    `(rerenderer.core/rset! ~obj ~attr ~value)))
