(ns ^:figwheel-always rerenderer.examples.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [timeout <!]]
            [rerenderer.examples.bird :as b]
            [rerenderer.examples.android :as a]
            [rerenderer.examples.simple :as s]))

(enable-console-print!)

(condp = (.. js/document -location -hash)
  "#android" (a/init!)
  "#simple" (s/init! (.getElementById js/document "canvas-1"))
  (b/init! (.getElementById js/document "canvas-1")))
