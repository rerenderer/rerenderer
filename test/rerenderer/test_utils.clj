(ns rerenderer.test-utils
  (:require [cljs.core.match :refer [match]]
            [cljs.test :refer [assert-expr]]
            [rerenderer.lang.core :as r]))

(defmacro script-of
  [& body]
  `(r/recording script#
     ~@body
     @script#))

(defmacro with-platform
  "Sets platform to `platform`"
  [platform & body]
  `(let [initial# @rerenderer.platform.core/platform]
     (reset! rerenderer.platform.core/platform ~platform)
     (let [result# (do ~@body)]
       (reset! rerenderer.platform.core/platform initial#)
       result#)))

(defmacro match?
  [x pattern]
  `(match ~x
     ~pattern true
     _# false))

(defmethod assert-expr 'match? [_ msg form]
  (let [[_ x pattern] form]
    `(if ~form
       (cljs.test/do-report {:type :pass
                             :message ~msg
                             :expected '~form
                             :actual nil})
       (cljs.test/do-report {:type :fail
                             :message ~msg
                             :expected '~pattern
                             :actual ~x}))))


