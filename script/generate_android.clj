(ns generate-android
  (:require [clojure.string :refer [split trim join] :as string]
            [clojure.java.io :refer [file writer]]
            [net.cgrand.enlive-html :as html]
            [clj-http.client :as http])
  (:import (java.io StringReader)))

(def types {"int" "Int"
            "float" "Float"
            "long" "Long"
            "double" "Double"
            "char" "Char"
            "boolean" "Boolean"
            "short" "Short"
            "int[]" "IntArray"
            "float[]" "FloatArray"
            "long[]" "LongArray"
            "double[]" "DoubleArray"
            "char[]" "CharArray"
            "short[]" "ShortArray"
            "Object" "Any"})

(def numeric ["Byte" "Short" "Int" "Long" "Float" "Double"])

(defn get-links
  [content]
  (as-> content $
        (html/select $ [:div.jd-descr])
        (second $)
        (html/select $ [:a])
        (html/select $ [(html/but (html/attr-contains :href "#"))])
        (html/select $ [(html/attr-starts :href "/reference/android/graphics/")])
        (map #(get-in % [:attrs :href]) $)
        (set $)))

(defn make-url
  [url]
  (str "https://developer.android.com" url))

(defn fetch
  [url]
  (let [full-url (make-url url)
        {:keys [body]} (http/get full-url)
        content (html/html-resource (StringReader. body))]
    [content (get-links content)]))

(defn fetch-all
  [start-url]
  (loop [urls [start-url]
         contents {}]
    (let [[url & urls] urls]
      (if url
        (let [[content new-urls] (fetch url)
              contents (assoc contents url content)
              urls (->> (concat new-urls urls)
                        (remove contents)
                        set)]
          (recur (vec urls) contents))
        (vals contents)))))

(defn get-api-trs
  [content id]
  (as-> content $
        (html/select $ [id])
        (first $)
        (html/select $ [:tr.api])
        (remove #(re-find #"apilevel-23" (get-in % [:attrs :class])) $)))

(defn prepare-type
  [type]
  (let [type (last (split type #" "))]
    (get types type type)))

(defn get-constants
  [content]
  (for [tr (get-api-trs content :#constants)
        :let [[type-td name-td & _] (html/select tr [:td])]]
    {:type (-> type-td :content first prepare-type)
     :name (-> name-td :content first :content first)}))

(defn parse-args
  [nobr]
  (let [args-els (drop-while #(not (and (string? %) (.startsWith % "(")))
                             (:content nobr))
        args-str (reduce #(str %1 (if (string? %2)
                                    %2
                                    (-> %2 :content first)))
                         args-els)
        trimmed (subs args-str 1 (dec (count args-str)))]
    (if (pos? (count trimmed))
      (->> (split trimmed #", ")
           (map #(split % #" "))
           (map (fn [[type name]] {:name name
                                   :type (prepare-type type)})))
      [])))

(defn parse-name
  [td]
  (let [[name-a] (html/select td [:span.sympad :a])]
    (-> name-a :content first)))

(defn get-constructors
  [content]
  (for [tr (get-api-trs content :#pubctors)
        :let [[nobr] (html/select tr [:td.jd-linkcol :nobr])]]
    {:name (parse-name nobr)
     :args (parse-args nobr)}))

(defn get-methods
  [content]
  (for [tr (get-api-trs content :#pubmethods)
        :let [[type-td descr-td] (html/select tr [:td])
              [nobr] (html/select descr-td [:nobr])
              type (-> type-td :content first :content first trim)]]
    {:name (parse-name nobr)
     :args (parse-args nobr)
     :type (prepare-type type)
     :static? (re-find #"static" type)}))

(defn get-name
  [content]
  (-> (html/select content [:h1])
      first
      :content
      first))

(defn get-imports
  [content]
  (as-> content $
        (html/select $ [:div#jd-content])
        (first $)
        (html/select $ [:a])
        (html/select $ [(html/but (html/attr-contains :href "#"))])
        (html/select $ [(html/attr-starts :href "/reference/")])
        (map #(get-in % [:attrs :href]) $)
        (filter #(not (re-find #"/(\w+)\.(\w+)\.html" %)) $)
        (map #(string/replace % "/reference/" "") $)
        (map #(string/replace % ".html" "") $)
        (map #(string/replace % "/" ".") $)
        (remove #(re-find #"java\.lang" %) $)))

(defn parse-class
  [content]
  (let [name (get-name content)
        imports (if (re-find #"\." name)
                  (get-imports content)
                  (conj (get-imports content)
                        (format "android.graphics.%s" name)))]
    {:constants (get-constants content)
     :constructors (get-constructors content)
     :methods (get-methods content)
     :imports imports
     :name name
     :interop-name (string/replace name "." "\\$")}))

(defn parse-all
  [start-url]
  (->> (fetch-all start-url)
       (map parse-class)))

(defn make-imports
  [parsed]
  (->> parsed
       (map :imports)
       flatten
       set
       (map #(format "import %s" %))))

(defn kt-arg
  [n {:keys [type]}]
  (if (some #{type} numeric)
    (format "anyTo%s(data.args.get(%d))" type n)
    (format "data.args.get(%d) as %s" n type)))

(defn make-constructors
  [{:keys [constructors interop-name]}]
  (for [{:keys [name args]} constructors
        :let [kt-args (map-indexed kt-arg args)]]
    (format "(data.cls == \"%s\" && data.args.count() == %d) -> %s(%s)"
            interop-name (count args) name (join ", " kt-args))))

(defn make-methods
  [{:keys [name interop-name methods]}]
  (for [{:keys [args static?] :as method} methods
        :let [kt-args (map-indexed kt-arg args)
              method (:name method)]]
    (if static?
      (format "(data.objVar == \"%s\" && data.method == \"%s\" && data.args.count() == %d) -> %s.%s(%s)"
              interop-name method (count args) name method (join ", " kt-args))
      (format "(data.objVar is %s && data.method == \"%s\" && data.args.count() == %d) -> data.objVar.%s(%s)"
              name method (count args) method (join ", " kt-args)))))

(defn make-constans
  [{:keys [constants name interop-name]}]
  (for [const constants
        :let [const (:name const)]]
    (format "(data.objVar == \"%s\" && data.attr == \"%s\") -> %s.%s"
            interop-name const name const)))

(defn get-numbers-convertor
  [to]
  (for [from numeric
        :when (not= from to)]
    (format "is %s -> x.to%s()" from to)))

(defn get-numbers-convertors
  []
  (for [to numeric]
    (format "fun anyTo%s(x: Any?): %s = when (x) {
    %s
    else -> x as %s
}" to to (join "\n    " (get-numbers-convertor to)) to)))

(defn render
  [imports constructors methods constants convertors]
  (format "package com.nvbn.tryrerenderer

%s


%s

fun doNew(vars: Map<String, Any?>, data: New): Any = when {
    %s
    else -> throw Exception(\"Can't make instance of ${data.cls}\")
}

fun doCall(vars: Map<String, Any?>, data: Call): Any = when {
    %s
    else -> throw Exception(\"Can't call ${data.method}\")
}

fun doGet(vars: Map<String, Any?>, data: Get): Any = when {
    %s
    else -> throw Exception(\"Can't get non-constant ${data.attr}\")
}
"
          (join "\n" imports) (join "\n\n" convertors)
          (join "\n    " constructors) (join "\n    " methods)
          (join "\n    " constants)))

(defn get-output-path
  []
  (-> (meta #'render)
      :file
      file
      .getParentFile
      .getParent
      (str "/android/TryRerenderer/app/src/main/kotlin/com/nvbn/tryrerenderer/gen.kt")))

(defn -main
  [& args]
  (let [url "/reference/android/graphics/Canvas.html"
        parsed (parse-all url)
        imports (make-imports parsed)
        constructors (flatten (mapv make-constructors parsed))
        methods (flatten (mapv make-methods parsed))
        constants (flatten (mapv make-constans parsed))
        convertors (get-numbers-convertors)
        rendered (render imports constructors methods constants
                         convertors)
        path (get-output-path)]
    (with-open [out (writer path)]
      (.write out rendered))))
