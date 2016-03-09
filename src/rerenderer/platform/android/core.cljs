(ns rerenderer.platform.android.core
  (:require [rerenderer.platform.core :as platform]
            [rerenderer.platform.android.bus :refer [interpret! available? information]]
            [rerenderer.platform.android.events :refer [bind-event!]]
            [rerenderer.types.render-result :refer [->RenderResult]]
            [rerenderer.types.component :refer [IComponent props]]
            [rerenderer.types.node :refer [Node]]
            [rerenderer.lang.core :as r :include-macros true]))

; Sets platform to android, when interop object available:
(when (available?)
  (reset! platform/platform :android))

(defprotocol IAndroid
  "Should be implemented for adding android support to component."
  (render-android [_ bitmap]))

(defmethod platform/apply-script! :android
  [script root options]
  (interpret! script root options))

(defmethod platform/listen! :android
  [ch _]
  (bind-event! ch))

(defmethod platform/render :android
  [component]
  {:pre [(satisfies? IComponent component)
         (satisfies? IAndroid component)]}
  (r/recording script
    (let [{:keys [width height]} (props component)
          colorspace (r/.. android -graphics -Bitmap$Config -ARGB_8888)
          bitmap (r/.. android -graphics -Bitmap (createBitmap width height colorspace))
          canvas (r/new (r/.. android -graphics -Canvas) bitmap)]
      (render-android component canvas)
      (->RenderResult @script bitmap))))

(defmethod platform/render-to :android
  [child parent]
  {:pre [(instance? Node child)
         (instance? Node parent)]}
  (r/recording script
    (let [paint (r/new (r/.. android -graphics -Paint))
          parent-canvas (r/new (r/.. android -graphics -Canvas) (:canvas parent))]
      (r/.. parent-canvas (drawBitmap (:canvas child)
                                      (:x child) (:y child)
                                      paint)))
    @script))

(defmethod platform/information :android
  [_]
  @information)
