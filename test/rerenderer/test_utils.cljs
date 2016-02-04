(ns rerenderer.test-utils
  (:require [rerenderer.types.component :as c]
            [rerenderer.lang.forms :refer [->Ref]]))

(defn make-component
  [tag props & childs]
  (reify
    c/IComponent
    (tag [_] tag)
    (childs [_] childs)
    (props [_] props)))

(defn genref
  []
  (->Ref (gensym)))
