(ns rerenderer.platform.utils)

; We implement from/to-json because data.json doesn't support clojurescript
(defn from-json
  [data]
  (js->clj (.parse js/JSON data)
           :keywordize-keys true))

(defn to-json
  [data]
  (.stringify js/JSON (clj->js data)))
