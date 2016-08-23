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

(def ^:no-doc cache (transient {}))
(declare ^:no-doc ^:dynamic *used*)

(defmethod platform/listen! :browser
  [ch options]
  (bind-events! ch (get-canvas options)))

(defn ^:no-doc render-component
  [parent-canvas component]
  {:pre [(satisfies? IComponent component)
         (satisfies? IBrowser component)]}
  (let [{:keys [width height x y]} (props component)
        parent-ctx (.getContext parent-canvas "2d")
        component-path (path component)]
    (conj! *used* component-path)
    (if-let [canvas (get cache component-path)]
      (do (.drawImage parent-ctx canvas x y)
          canvas)
      (let [canvas (.createElement js/document "canvas")
            ctx (.getContext canvas "2d")]
        (set! (.-width canvas) width)
        (set! (.-height canvas) height)
        (render-browser component ctx)
        (doseq [child (childs component)]
          (render-component canvas child))
        (assoc! cache component-path canvas)
        (.drawImage parent-ctx canvas x y)
        canvas))))

(defn render-top-component
  [canvas component stats]
  (when stats (.begin stats))
  (let [result (render-component canvas component)]
    (when stats (.end stats))
    result))

(defmethod platform/render :browser
  [component options]
  {:pre [(satisfies? IComponent component)
         (satisfies? IBrowser component)]}
  (js/requestAnimationFrame
    (fn []
      (binding [*used* (transient #{})]
        (let [result (render-top-component (get-canvas options)
                                           component
                                           (:stats options))
              used (persistent! *used*)]
          (set! cache (->> (persistent! cache)
                           (filter #(-> % first used))
                           (into {})
                           transient))
          result)))))

(defmethod platform/information :browser
  [options]
  (let [canvas (get-canvas options)]
    {:width (.-width canvas)
     :height (.-height canvas)
     :input #{:mouse :keyboard}}))
