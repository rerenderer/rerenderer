(ns rerenderer.platform.browser.core
  (:require [rerenderer.platform.core :as platform]
            [rerenderer.platform.browser.interpreter :refer [interprete!]]
            [rerenderer.platform.browser.events :refer [bind-events!]]
            [rerenderer.lang.core :as r :include-macros true]
            [rerenderer.types.render-result :refer [->RenderResult]]
            [rerenderer.types.component :refer [IComponent props]]
            [rerenderer.types.node :refer [Node]]))

; Set protocol if nothing was set before:
(when-not @platform/platform
  (reset! platform/platform :browser))

(defprotocol IBrowser
  "Should be implemented for adding browser support to component."
  (render-browser [_ ctx]))

; Platform methods:
(defmethod platform/apply-script! :browser
  [script [_ root-ref] {:keys [canvas]}]
  (let [ctx (.getContext canvas "2d")
        pool (interprete! script)]
    (.drawImage ctx (pool root-ref) 0 0)))

(defmethod platform/listen! :browser
  [ch {:keys [canvas]}]
  (bind-events! ch canvas))

(defmethod platform/render :browser
  [component]
  {:pre [(satisfies? IComponent component)
         (satisfies? IBrowser component)]}
  (r/recording script
    (let [{:keys [width height]} (props component)
          canvas (r/new Canvas)
          ctx (r/.. canvas (getContext "2d"))]
      (r/set! (r/.. canvas -width) width)
      (r/set! (r/.. canvas -height) height)
      (render-browser component ctx)
      (->RenderResult @script canvas))))

(defmethod platform/render-to :browser
  [child parent]
  {:pre [(instance? Node child)
         (instance? Node parent)]}
  (r/recording script
    (let [ctx (r/.. (:canvas parent) (getContext "2d"))]
      (r/.. ctx (drawImage (:canvas child) (:x child) (:y child))))
    @script))
