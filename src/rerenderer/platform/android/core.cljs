(ns rerenderer.platform.android.core
  (:require [rerenderer.platform.core :as platform]
            [rerenderer.platform.android.bus :refer [render! available? information]]
            [rerenderer.platform.android.events :refer [bind-event!]]
            [rerenderer.component :refer [IComponent props childs path]]))
;
;; Sets platform to android, when interop object available:
(when (available?)
  (reset! platform/platform :android))

(defprotocol IAndroid
  "Component that implement that protocol support rendering on Android."
  (android-primitive [this] "Name of android-side primitive."))

(defn ^:no-doc serialize-component
  [component]
  [(android-primitive component) (props component)
   (for [child (childs component)]
     (serialize-component child))
   (path component)])

(defmethod platform/render :android
  [component options]
  (render! (serialize-component component) options))

(defmethod platform/listen! :android
  [ch _]
  (bind-event! ch))

(defmethod platform/information :android
  [_]
  @information)
