(ns ^:figwheel-always rerenderer.examples.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<!]]
            [devtools.core :as devtools]
            [rerenderer.core :as r :include-macros true]
            [rerenderer.browser :refer [IBrowser]]))

(enable-console-print!)
(devtools/install!)

(defn smile
  []
  (reify
    r/IComponent
    (size [_] [200 200])
    IBrowser
    (render-browser [_ ctx]
      (doto ctx
        (r/.. beginPath)
        (r/.. (arc 75 75 50 0 (* 2 (.-PI js/Math)) true))
        (r/.. (moveTo 110 75))
        (r/.. (arc 75 75 35 0 (.-PI js/Math) false))
        (r/.. (moveTo 65 65))
        (r/.. (arc 60 67 5 0 (* 2 (.-PI js/Math)) true))
        (r/.. (moveTo 95 65))
        (r/.. (arc 90 65 6 0 (* 2 (.-PI js/Math)) true))
        (r/.. stroke)))))

(defn rect
  [{:keys [size color]}]
  (let [[w h] size]
    (reify
      r/IComponent
      (size [_] [w h])
      IBrowser
      (render-browser [_ ctx]
        (r/set! (r/.. ctx -fillStyle) color)
        (r/.. ctx (fillRect 0 0 w h))
        (r/.. ctx (drawImage (r/render smile) 0 0))))))

(let [options {:canvas (.getElementById js/document "canvas-1")}
      state (atom {:size [600 650]
                   :color "red"})]
  (r/init! :browser rect state options)
  (let [click-ch (r/listen! :click options)]
    (go-loop [colors ["green" "yellow" "red"]]
      (<! click-ch)
      (swap! state assoc :color (first colors))
      (recur (conj (vec (rest colors)) (first colors))))))
