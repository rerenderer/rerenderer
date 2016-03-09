(ns rerenderer.platform.browser.core
  (:require [rerenderer.platform.core :as platform]
            [rerenderer.platform.browser.interpreter :refer [interpret!]]
            [rerenderer.platform.browser.events :refer [bind-events!]]
            [rerenderer.lang.core :as r :include-macros true]
            [rerenderer.types.render-result :refer [->RenderResult]]
            [rerenderer.types.component :refer [IComponent props]]
            [rerenderer.types.node :refer [Node]]))

; Set platform if nothing was set before:
(when-not @platform/platform
  (reset! platform/platform :browser))

(defprotocol IBrowser
  "Should be implemented for adding browser support to component."
  (render-browser [_ ctx]))

(defn- get-canvas
  "Return canvas from options or first canvas in the document."
  [options]
  (or (:canvas options)
      (-> js/document
          (.getElementsByTagName "canvas")
          (aget 0))))

; Platform methods:
(defmethod platform/apply-script! :browser
  [script [_ root-ref] options]
  (let [canvas (get-canvas options)
        ctx (.getContext canvas "2d")
        pool (interpret! script)
        rendered (pool root-ref)]
    (if (:scale options)
      (.drawImage ctx rendered
                  0 0 (.-width rendered) (.-height rendered)
                  0 0 (.-width canvas) (.-height canvas))
      (.drawImage ctx rendered 0 0))))

(defmethod platform/listen! :browser
  [ch options]
  (bind-events! ch (get-canvas options)))

(defmethod platform/render :browser
  [component]
  {:pre [(satisfies? IComponent component)
         (satisfies? IBrowser component)]}
  (r/recording script
    (let [{:keys [width height]} (props component)
          canvas (r/.. document (createElement "canvas"))
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

(defmethod platform/information :browser
  [options]
  (let [canvas (get-canvas options)]
    {:width (.-width canvas)
     :height (.-height canvas)
     :input #{:mouse :keyboard}}))
