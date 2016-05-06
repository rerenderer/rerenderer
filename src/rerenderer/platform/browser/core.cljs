(ns rerenderer.platform.browser.core
  (:require [rerenderer.platform.core :as platform]
            [rerenderer.platform.browser.events :refer [bind-events!]]
            [rerenderer.component :refer [IComponent props path childs]]))

; Set platform if nothing was set before:
(when-not @platform/platform
  (reset! platform/platform :browser))

(defprotocol IBrowser
  "Component that implement that protocol support rendering in browser."
  (render-browser [_ ctx]
                  "Render component on canvas context.

                  Example:

                  ```
                  (reify
                    IComponent
                    ...
                    IBrowser
                    (render-browser [_ ctx]
                      (set! (.-fillStyle ctx) \"rgb(255, 0, 0)\")
                      (.fillRect ctx 0 0 100 100)))
                  ```"))

(defn- get-canvas
  "Return canvas from options or first canvas in the document."
  [options]
  (or (:canvas options)
      (-> js/document
          (.getElementsByTagName "canvas")
          (aget 0))))

(def cache (atom {}))
(def used (atom #{}))

(defmethod platform/listen! :browser
  [ch options]
  (bind-events! ch (get-canvas options)))

(defn render-component
  [parent-canvas component]
  {:pre [(satisfies? IComponent component)
         (satisfies? IBrowser component)]}
  (let [{:keys [width height x y]} (props component)
        parent-ctx (.getContext parent-canvas "2d")
        component-path (path component)]
    (swap! used conj component-path)
    (if (@cache component-path)
      (let [canvas (@cache component-path)]
        (.drawImage parent-ctx canvas x y)
        canvas)
      (let [canvas (.createElement js/document "canvas")
            ctx (.getContext canvas "2d")]
        (set! (.-width canvas) width)
        (set! (.-height canvas) height)
        (render-browser component ctx)
        (doseq [child (childs component)]
          (render-component canvas child))
        (.drawImage parent-ctx canvas x y)
        canvas))))

(defmethod platform/render :browser
  [component options]
  {:pre [(satisfies? IComponent component)
         (satisfies? IBrowser component)]}
  (reset! used #{})
  (let [result (render-component (get-canvas options) component)]
    (doseq [[k _] @cache
            :when (not (@used k))]
      (swap! cache dissoc k))
    result))

(defmethod platform/information :browser
  [options]
  (let [canvas (get-canvas options)]
    {:width (.-width canvas)
     :height (.-height canvas)
     :input #{:mouse :keyboard}}))
