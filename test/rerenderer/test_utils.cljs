(ns rerenderer.test-utils
  (:require [rerenderer.component :refer [IComponent]]))

(defn make-component
  [tag props & childs]
  (reify
    IComponent
    (tag [_] tag)
    (childs [_] childs)
    (props [_] props)))
