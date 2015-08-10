(ns rerenderer.android)
;  (:require [cljs.core.match :refer-macros [match]]
;            [clojure.string :refer [replace-first]]
;            [rerenderer.core :refer [IPlatform]]))
;
;(defn send!
;  [proxy calls]
;  (.send proxy (.stringify js/JSON (clj->js calls))))
;
;(defn prepare-call
;  [[[[who] [method & args]]]]
;  [(replace-first who "-" "") method (or args)])
;
;(defn android
;  [proxy]
;  (let [platform-id (str (gensym))]
;    (set! (.. proxy -platformId) platform-id)
;    (reify IPlatform
;      (render! [_ ctx]
;        (when (= platform-id (.. proxy -platformId))
;          (send! proxy
;                 (for [[action & params] ctx
;                       :when (= action :call)]
;                   (prepare-call params))))))))
