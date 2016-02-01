(ns rerenderer.platform.android.core
  (:require [rerenderer.platform.core :as platform]
            [rerenderer.platform.android.bus :refer [interprete! available?]]
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
  [script root _]
  (interprete! script root))

(defmethod platform/listen! :android
  [ch _]
  (bind-event! ch))

(defmethod platform/render :android
  [component]
  {:pre [(satisfies? IComponent component)
         (satisfies? IAndroid component)]}
  (r/recording script
    (let [{:keys [width height]} (props component)
          colorspace (r/.. r/static -Bitmap$Config (valueOf "ARGB_8888"))
          bitmap (r/.. r/static -Bitmap (createBitmap width height colorspace))
          canvas (r/new Canvas bitmap)]
      (render-android component canvas)
      (->RenderResult @script bitmap))))

(defmethod platform/render-to :android
  [child parent]
  {:pre [(instance? Node child)
         (instance? Node parent)]}
  (r/recording script
    (let [paint (r/new Paint)]
      (r/.. (:canvas parent) (drawBitmap (:canvas child)
                                         (:x child) (:y child)
                                         paint)))
    @script))
