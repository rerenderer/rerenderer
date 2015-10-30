(ns rerenderer.interop
  "Macros from this namespace should be used for interacting with objects
  in renderening functions. They should replace clojure's `..`, `new` and `set!`.

  For using this namespace you should require it with `:as`, not with `:refer`:

  ```
  (ns example
    (:require [rerenderer.interop :as r :include-macros true]))

  (let [canvas (r/new 'Canvas 100 200)]
    (r/set! (r/.. canvas -height) 300)
    (r/.. canvass (drawRect 100 200 300 400)))
  ```"
  (:refer-clojure :exclude [new .. set!])
  (:require #?(:cljs [cljs.core.match :refer-macros [match]]
               :clj [clojure.core.match :refer [match]])
                    [clojure.string :refer [join]]))

(def ^:no-doc script (atom []))

(defn ^:no-doc get-var
  []
  (gensym))

(defn ^:no-doc prepare-arg
  [arg]
  (match arg
    [:var _] arg
    [:val _] arg
    _ [:val arg]))

(defn ^:no-doc unpack-var
  [var]
  (match var
    [:var x] x
    _ var))

; [:new result-var class [arguments]]
; each argument can be [:var var] or [:val val]
(defn ^:no-doc rnew
  [cls & args]
  (let [id (get-var)]
    (swap! script conj [:new id cls (mapv prepare-arg args)])
    [:var id]))

; [:set var attr value]
; value can be [:var var] or [:val val]
(defn ^:no-doc rset!
  [var attr value]
  (swap! script conj [:set (unpack-var var) attr (prepare-arg value)]))

; [:get result-var var attr]
(defn ^:no-doc rget
  [var attr]
  (let [id (get-var)]
    (swap! script conj [:get id (unpack-var var) attr])
    [:var id]))

; [:call result-var var method [arguments]]
; each argument can be [:var var] or [:val val]
(defn ^:no-doc rcall!
  [var method & args]
  (let [id (get-var)]
    (swap! script conj [:call id (unpack-var var)
                        method (mapv prepare-arg args)])
    [:var id]))

(defmacro new
  "Works like `new` or `class.`, usage:

  ```
  (r/new 'Bitmap)
  (r/new 'Rectangle 100 100 200 200)
  ```"
  [cls & args]
  `(rnew ~(keyword (name cls)) ~@args))

(defn ^:no-doc attr-to-str
  [attr]
  (-> attr name rest join))

(defn ^:no-doc dot
  [x form]
  (if (symbol? form)
    (if (= (first (name form)) \-)
      `(rget ~x ~(attr-to-str form))
      `(rcall! ~x ~(name form)))
    `(rcall! ~x ~(name (first form)) ~@(rest form))))

(defmacro ..
  "Usage:

   ```
   (r/.. canvas (getContext \"2d\"))
   (r/.. image -onLoad (bind (fn [])))
   ```"
  ([x form] (dot x form))
  ([x form & more] `(.. ~(dot x form) ~@more)))

(defmacro set!
  "Usage:

  ```
  (r/set! (r/.. canvas -height) 200)
  ```"
  [path value]
  (let [attr (-> path last name rest join)
        path (-> path butlast rest)
        obj (if (> (count path) 2)
              `(rerenderer.interop/.. ~@path)
              (first path))]
    `(rset! ~obj ~attr ~value)))
