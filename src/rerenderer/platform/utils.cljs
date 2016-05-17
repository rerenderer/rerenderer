(ns rerenderer.platform.utils)

; We implement from/to-json because data.json doesn't support clojurescript
(defn from-json
  "Deserializes json to cljs data structure."
  [data]
  (js->clj (.parse js/JSON data)
           :keywordize-keys true))

(defn to-json
  "Serializes cljs data structure to json."
  [data]
  (.stringify js/JSON (clj->js data)))
