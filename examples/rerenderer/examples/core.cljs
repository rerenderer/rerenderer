(ns ^:figwheel-always rerenderer.examples.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<! timeout]]
            [devtools.core :as devtools]
            [rerenderer.core :as r :include-macros true]
            [rerenderer.browser :refer [IBrowser]]
            [rerenderer.android :refer [IAndroid]]
            [rerenderer.primitives :as p]))

(enable-console-print!)
(devtools/install!)

(defn root
  [{:keys [text-y]}]
  (p/rectangle {:color [255 255 0 0]
                :width 1080
                :height 1920}
               (for [n (range 0 1920 50)
                     :let [color (if (zero? (mod n 100))
                                   [255 0 255 0]
                                   [255 0 0 255])]]
                 (p/rectangle {:x 980
                               :y n
                               :color color
                               :width 50
                               :height 50}))
               (p/text {:color [255 255 255 255]
                        :width 800
                        :height 800
                        :x 200
                        :y text-y
                        :font-size 150} "Hi There!")
               (p/image {:width 200
                         :height 500
                         :src "bird"})))

(def state (atom {:text-y 200}))

(r/init! root state
         {:canvas (.getElementById js/document "canvas-1")})

(go-loop []
  (<! (timeout 40))
  (swap! state update-in [:text-y] #(-> % (+ 2) (mod 1920)))
  (recur))

