(ns rerenderer.lang.core
  (:refer-clojure :exclude [new .. set!])
  (:require [clojure.string :refer [join]]))

(defmacro new
  "Works like `new` or `class.`, usage:

  ```
  (r/new 'Bitmap)
  (r/new 'Rectangle 100 100 200 200)
  ```"
  [cls & args]
  `(rnew ~(name cls) ~(vec args)))

(defn ^:no-doc attr-to-str
  [attr]
  (-> attr name rest join))


(defn ^:no-doc dot
  [x form]
  (if (symbol? form)
    (if (= (first (name form)) \-)
      `(rget ~x ~(attr-to-str form))
      `(rcall! ~x ~(name form) []))
    `(rcall! ~x ~(name (first form)) ~(vec (rest form)))))

(defmacro ..
  "Usage:

   ```
   (r/.. canvas (getContext \"2d\"))
   (r/.. image -onLoad (bind (fn [])))
   ```"
  ([x form] (dot x form))
  ([x form & more] `(rerenderer.lang.core/.. ~(dot x form) ~@more)))

(defmacro set!
  "Usage:

  ```
  (r/set! (r/.. canvas -height) 200)
  ```"
  [path value]
  (let [attr (-> path last name rest join)
        path (-> path butlast rest)
        obj (if (> (count path) 2)
              `(rerenderer.lang.core/.. ~@path)
              (first path))]
    `(rset! ~obj ~attr ~value)))

(defmacro recording
  "Records script in `script-var` atom."
  [script-var & body]
  `(do (reset! script [])
       (let [~script-var script
             result# (do ~@body)]
         (reset! script [])
         result#)))
