(ns rerenderer.component
  (:require [clojure.string :as string]
            [cljsjs.tinycolor]))

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

(defn prepare-childs
  "Returns flatten list of non-empty childs."
  [childs]
  (->> childs flatten (remove nil?)))

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

(declare ^:no-doc path)

(defn ^:no-doc child-path
  [child]
  (let [{:keys [x y]} (props child)]
    (str (path child) ":("x ", " y ")")))

(def ^{:doc "Returns full reversed components path, from childs to component."} ^:no-doc path
  (memoize
    (fn [component]
      (let [cache-props (dissoc (props component) :x :y)]
        (str (tag component) ":" cache-props ":["
             (string/join ":" (map child-path (childs component)))
             "]")))))

(def ^{:doc "Converts color to rgba, supported formats: `#ff0000`, `rgb(255, 255, 0)`, `argb(255, 0, 0, 0)`, `red`."}
->rgba
  (memoize
    (fn [color]
      (let [{:keys [r g b a]} (-> color
                                  clj->js
                                  js/tinycolor
                                  .toRgb
                                  (js->clj :keywordize-keys true))]
        [r g b (* 255 a)]))))

(defn ->url
  "Get full url from relative or absolute url."
  [src]
  (if (or (string/starts-with? src "http://")
          (string/starts-with? src "https://"))
    src
    (str (.. js/document -location -protocol)
         "//"
         (.. js/document -location -host)
         src)))
