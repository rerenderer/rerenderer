(ns rerenderer.lang.core
  (:refer-clojure :exclude [new .. set!])
  (:require [clojure.string :refer [join]]
            [cljs.analyzer :refer [resolve-var]]))

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

(defmacro wrap
  [x]
  (if (symbol? x)
    (let [full-ns (-> &env :ns :name)
          full-name (symbol (str (name full-ns) "/" (name x)))]
      (if (= (resolve-var &env x) {:ns full-ns
                                   :name full-name})
        `(rerenderer.lang.forms/->Ref ~(str ":" (name x)))
        x))
    x))

(defmacro dot-dot
  ([x form] (dot x form))
  ([x form & more] `(dot-dot ~(dot x form) ~@more)))

(defmacro ..
  "Usage:

   ```
   (r/.. canvas (getContext \"2d\"))
   (r/.. image -onLoad (bind (fn [])))
   ```"
  [x form & more]
  `(dot-dot (wrap ~x) ~form ~@more))

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
