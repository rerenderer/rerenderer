(ns ^:figwheel-always rerenderer.examples.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<!]]
            [devtools.core :as devtools]
            [rerenderer.core :as r :include-macros true]
            [rerenderer.browser :refer [IBrowser]]
            [rerenderer.android :refer [IAndroid]]
            [rerenderer.primitives :as p]))

(enable-console-print!)
(devtools/install!)

;(defn smile
;  []
;  (reify
;    r/IComponent
;    (size [_] [200 200])
;    IBrowser
;    (render-browser [_ ctx]
;      (doto ctx
;        (r/.. beginPath)
;        (r/.. (arc 75 75 50 0 (* 2 (.-PI js/Math)) true))
;        (r/.. (moveTo 110 75))
;        (r/.. (arc 75 75 35 0 (.-PI js/Math) false))
;        (r/.. (moveTo 65 65))
;        (r/.. (arc 60 67 5 0 (* 2 (.-PI js/Math)) true))
;        (r/.. (moveTo 95 65))
;        (r/.. (arc 90 65 6 0 (* 2 (.-PI js/Math)) true))
;        (r/.. stroke)))))
;
;(defn rect
;  [{:keys [size color]}]
;  (let [[w h] size]
;    (reify
;      r/IComponent
;      (size [_] [w h])
;      IBrowser
;      (render-browser [_ ctx]
;        (r/set! (r/.. ctx -fillStyle) color)
;        (r/.. ctx (fillRect 0 0 w h))
;        (r/.. ctx (drawImage (r/render smile) 0 0)))
;      IAndroid
;      (render-android [_ canvas]
;        (let [paint (r/new Paint)]
;          (doseq [w (range 0 1080 50)
;                  h (range 0 1920 50)]
;            (if (zero? (mod (+ w h) 100))
;              (r/.. paint (setARGB 255 255 0 0))
;              (r/.. paint (setARGB 255 0 0 0)))
;            (r/.. canvas (drawRect w h (+ 50 w) (+ 50 h) paint)))
;          (r/.. paint (setARGB 255 0 255 0))
;          (r/.. paint (setTextSize 80))
;          (r/.. canvas (drawText "Generate with ClojureScript" 50 600 paint))
;          (r/.. paint (setARGB 255 0 255 255))
;          (r/.. canvas (drawText "Rendered with Kotlin" 100 900 paint))
;
;          (println paint canvas "!!!!" js/android))))))
;
;(let [options {:canvas (.getElementById js/document "canvas-1")}
;      state (atom {:size [1080 1920]
;                   :color "red"})]
;  (r/init! :android rect state options)
;  (let [click-ch (r/listen! :click options)]
;    (go-loop [colors ["green" "yellow" "red"]]
;      (<! click-ch)
;      (swap! state assoc :color (first colors))
;      (recur (conj (vec (rest colors)) (first colors))))))

(defn root
  [state]
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
                               :height 50}))))

(defn get-platform
  []
  (if (aget js/window "android")
    :android
    :browser))

(r/init! (get-platform) root (atom {})
         {:canvas (.getElementById js/document "canvas-1")})
