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

;(defn root
;  [{:keys [text-y]}]
;  (p/rectangle {:color [255 255 0 0]
;                :width 1080
;                :height 1920}
;               (for [n (range 0 1920 50)
;                     :let [color (if (zero? (mod n 100))
;                                   [255 0 255 0]
;                                   [255 0 0 255])]]
;                 (p/rectangle {:x 980
;                               :y n
;                               :color color
;                               :width 50
;                               :height 50}))
;               (p/text {:color [255 255 255 255]
;                        :width 800
;                        :height 800
;                        :x 200
;                        :y text-y
;                        :font-size 150} "123 123 123 ")
;               (p/image {:width 200
;                         :height 500
;                         :src "bird"})
;               ))

;(defn root
;  [{:keys [font-size]}]
;  (p/rectangle {:color [255 25 0 0]
;                :width 500
;                :height 500
;                :x 0
;                :y 0}
;    (p/text {:color [255 255 255 255]
;             :x 0
;             :y 0
;             :width 500
;             :height 300
;             :font-size font-size}
;            "nested")
;    (p/rectangle {:color [255 255 0 0]
;                  :width 100
;                  :height 100
;                  :x 200
;                  :y 20}
;      (p/text {:color [255 255 255 0]
;               :x 0
;               :y 0
;               :width 300
;               :height 300
;               :font-size 50}
;              "test"))
;    (p/rectangle {:color [255 255 0 0]
;                  :width 100
;                  :height 100
;                  :x 400
;                  :y 20}
;      (p/text {:color [255 255 255 0]
;               :x 0
;               :y 0
;               :width 300
;               :height 300
;               :font-size 50}
;              "test"))))

(defn root
  [{:keys [width]}]
  (p/rectangle {:color [255 0 0 0]
                :x 0 :y 0
                :width width
                :height 500}
    (p/rectangle {:color [255 255 0 0]
                  :width 100
                  :height 100
                  :x 200
                  :y 20})
    (p/rectangle {:color [255 255 0 0]
                  :width 100
                  :height 100
                  :x 200
                  :y 200})))

(def state (atom {:text-y 200}))

(r/init! root state
         {:canvas (.getElementById js/document "canvas-1")})
;
(go-loop []
  (<! (timeout 100))
  (swap! state update-in [:width] #(-> % (+ 2) (mod 1000)))
  (recur))
