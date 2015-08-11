# rerenderer

Simple platform agnostic react-like library for drawing on canvas,
handling events and playing sounds.

Supported platforms:

- Browser (full support);
- Android (only primitive drawing)

## How it works?

When state (atom) changes `rerenderer` calls a rendering function,
inside the function we work with shadow canvas (like shadow dom in React).
And applies changes to real canvas only when shadow canvas has difference
with shadow canvas of the previous call of the rendering function.
 
And as a real canvas we can use browser canvas, android canvas
(not fully implemented) or even iOS canvas (not implemented).

## Usage in browser

Renders rectangle that changes colors on click:

```clojure
(ns ^:figwheel-always rerenderer.examples.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<!]]
            [rerenderer.core :as r :include-macros true]
            [rerenderer.browser :refer [IBrowser]]))

(defn rect
  [{:keys [size color]}]
  (let [[w h] size]
    (reify
      r/IComponent
      (size [_] [w h])
      IBrowser
      (render-browser [_ ctx]
        (r/set! (r/.. ctx -fillStyle) color)
        (r/.. ctx (fillRect 0 0 w h))))))

(let [options {:canvas (.getElementById js/document "canvas-1")}
      state (atom {:size [600 650]
                   :color "red"})
      click-ch (r/listen! :click options)]
  (r/init! :browser rect state options)
  (go-loop [colors ["green" "yellow" "red"]]
    (<! click-ch)
    (swap! state assoc :color (first colors))
    (recur (conj (vec (rest colors)) (first colors)))))    

```

## TODO: Usage on android
