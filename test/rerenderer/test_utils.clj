(ns rerenderer.test-utils
  (:require [rerenderer.lang.core :as r]))

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
