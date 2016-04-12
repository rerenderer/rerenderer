(ns rerenderer.lang.core
  (:refer-clojure :exclude [new .. set!])
  (:require [clojure.string :refer [join]]
            [cljs.analyzer :refer [resolve-var]]))

(defmacro ^:no-doc wrap
  [x]
  (if (symbol? x)
    (let [full-ns (-> &env :ns :name)
          full-name (symbol (str (name full-ns) "/" (name x)))]
      (if (= (resolve-var &env x) {:ns full-ns
                                   :name full-name})
        `(rerenderer.lang.forms/->Static ~(name x))
        x))
    x))

(defmacro new
  "Works like clojure `new` macro.

  Should be used only inside components render methods.

  Example:

  ```
  (r/new Bitmap)
  (r/new Rectangle 100 100 200 200)
  ```"
  [cls & args]
  `(rnew (wrap ~cls) ~(vec args)))

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

(defmacro ^:no-doc dot-dot
  ([x form] (dot x form))
  ([x form & more] `(dot-dot ~(dot x form) ~@more)))

(defmacro ..
  "Works like clojure `..` macro.

  Should be used only inside components render methods.

  Example:

   ```
   (r/.. canvas (getContext \"2d\"))
   (r/.. image -onLoad (bind (fn [])))
   ```"
  [x form & more]
  `(dot-dot (wrap ~x) ~form ~@more))

(defmacro set!
  "Works like clojure `set!` macro.

  Should be used only inside components render methods.

  Example:

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

(defmacro ^:no-doc recording
  "Records script in `script-var` atom."
  [script-var & body]
  `(do (reset! script [])
       (let [~script-var script
             result# (do ~@body)]
         (reset! script [])
         result#)))
