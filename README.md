# rerenderer

[Documentation.](https://rerenderer.github.io/rerenderer/)

Simple platform agnostic react-like library for drawing on canvas,
handling events and playing sounds.

Supported platforms:

- Browser;
- Android.

## How it works?

When state (atom) changes `rerenderer` calls a rendering function,
inside the function we work with shadow canvas (like shadow dom in React).
And applies changes to real canvas only when shadow canvas has difference
with shadow canvas of the previous call of the rendering function.
 
And as a real canvas we can use browser canvas, android canvas
or even iOS canvas (not implemented).

## Usage

Create new project from template:

```bash
lein new rerenderer-game my-super-game
```

Renders rectangle that changes colors on click:

```clojure
(ns my-super-game.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.match :refer-macros [match]]
            [cljs.core.async :refer [<!]]
            [rerenderer.core :refer [init!]]
            [rerenderer.primitives :as p]))
    
(defn root
  [{:keys [color]} options]
  (p/rectangle {:color color
                :width 100
                :height 100}))

(def colors
  [[255 255 0 0]
   [255 0 255 0]
   [255 0 0 255]])

(defn change-color!
  [state-atom]
  (let [current-color (:color @state-atom)
        index (.indexOf (to-array colors) current-color)
        next-index (-> index inc (mod (count colors)))
        next-color (get colors next-index)]
    (swap! state-atom assoc :color next-color)))

(defn handler
  [event-ch state-atom options]
  (go-loop []
    (match (<! event-ch)
      [:click _] (change-color! state-atom)
      unhandled (println "Unhandled event" unhandled))
    (recur)))

(init!
  :root-view root
  :event-handler handler
  :events [:click]
  :state {:color [255 255 0 0]}
  :canvas (.getElementById js/document "canvas"))

```

## TODO: Usage on android
