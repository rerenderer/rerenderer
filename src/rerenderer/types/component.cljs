(ns rerenderer.types.component
  (:require [clojure.string :as string]))

(defprotocol IComponent
  "For creating component you should implement IComponent, platform-specific protocls
   (IAndroid, etc) and Object with toString, like:

   ```
   (reify
     IComponent
     (tag [_] \"rectangle\")
     (childs [_] list-of-childs)
     (props [_] map-of-props)
     Object
     (toString [this] (component->string this))
     IBrowser
     ...
     IAndroid
     ...)
   ```"
  (tag [this])
  (childs [this])
  (props [this]))

(defn component->string
  [primitive]
  (str "(" (tag primitive) " " (props primitive)
       (string/join "\n" (map component->string (childs primitive)))
       ")"))

(def calculate-path
  (memoize
    (fn [component]
      (string/join ":" (merge [(tag component)
                               (props component)]
                              (map :path (childs component)))))))
