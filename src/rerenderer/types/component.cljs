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
  (tag [this] "Component's tag, like `rectangle`.")
  (childs [this] "Component's childs if it nested.")
  (props [this] "Component's properties, like `{:width 100}`."))

(defn component->string
  "Translates component to string, thath looks like component usage in views."
  [component]
  (let [indent (string/join (for [_ (-> component tag count range)] " "))
        childs-lines (flatten (map #(string/split-lines (component->string %))
                                   (childs component)))
        childs-text (string/join (str "\n" indent) childs-lines)]

    (str "(" (tag component) " " (props component)
         (if (pos? (count childs-text))
           (str "\n" indent childs-text)
           "") ")")))

(def ^:no-doc calculate-path
  (memoize
    (fn [component]
      (let [cache-props (dissoc (props component) :x :y)]
        (str (tag component) ":" cache-props ":["
             (string/join ":" (map calculate-path (childs component)))
             "]")))))
