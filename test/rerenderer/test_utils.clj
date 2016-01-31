(ns rerenderer.test-utils
  (:require [rerenderer.lang.core :as r]))

(defmacro script-of
  [& body]
  `(r/recording script#
     ~@body
     @script#))
