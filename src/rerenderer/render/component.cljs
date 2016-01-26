(ns rerenderer.render.component)

(defprotocol ^:no-doc IComponent
  (tag [this]))
