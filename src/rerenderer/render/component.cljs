(ns rerenderer.render.component)

(defprotocol IComponent
  (tag [this])
  (childs [this])
  (props [this]))
