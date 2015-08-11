(ns rerenderer.browser
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.match :refer-macros [match]]
            [cljs.core.async :refer [>! chan]]
            [rerenderer.core :as r :include-macros true]))

(defn- run
  [pool line]
  (match line
    [:new id :Image []] (swap! pool assoc id (js/Image.))
    [:new id :Canvas []] (swap! pool assoc id
                                (.createElement js/document "canvas"))
    [:set var attr value] (aset (@pool var) attr (get @pool value value))
    [:get id var attr] (swap! pool assoc id (aget (@pool var) attr))
    [:call id var method args] (swap! pool assoc id
                                      (.apply (aget (@pool var) method)
                                              (@pool var)
                                              (clj->js (mapv #(get @pool % %) args))))))

(defn- interprete
  [script]
  (let [pool (atom {})]
    (doseq [line script]
      (run pool line))
    @pool))

(defmethod r/apply-script :browser
  [script root-id {:keys [canvas]}]
  (let [ctx (.getContext canvas "2d")
        pool (interprete script)]
    (.drawImage ctx (pool root-id) 0 0)))

(defmethod r/listen! :browser
  [event {:keys [canvas]}]
  (let [ch (chan)]
    (.addEventListener canvas (name event)
                       #(go (>! ch %)))
    ch))

(defmethod r/make-canvas! :browser
  [w h]
  (let [canvas (r/new Canvas)]
    (r/set! (r/.. canvas -width) w)
    (r/set! (r/.. canvas -height) h)
    canvas))

(defprotocol IBrowser
  (render-browser [_ ctx]))

(defmethod r/component->canvas :browser
  [component canvas]
  (when-not (satisfies? IBrowser component)
    (throw (js/Error. "Should implement IBrowser!")))
  (let [ctx (r/.. canvas (getContext "2d"))]
    (render-browser component ctx)))
