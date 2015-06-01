(ns ^:figwheel-always rerenderer.examples.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [timeout <!]]
            [rerenderer.examples.bird :as b]
            [rerenderer.examples.android :as a]))

(enable-console-print!)

(condp = (.. js/document -location -hash)
  "#android" (a/init!)
  (b/init! (.getElementById js/document "canvas-1")))
