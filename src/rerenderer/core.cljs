(ns ^:figwheel-always rerenderer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >!]]))

(def ^:dynamic *script* (atom []))

(def ^:dynamic *platform* :browser)

(defn rnew
  [cls & args]
  (let [id (gensym)]
    (swap! *script* conj [:new id cls (vec args)])
    id))

(defn rset!
  [var attr value]
  (swap! *script* conj [:set var attr value]))

(defn rget
  [var attr]
  (let [id (gensym)]
    (swap! *script* conj [:get id var attr])
    id))

(defn rcall!
  [var method & args]
  (let [id (gensym)]
    (swap! *script* conj [:call id var method args])
    id))

(defmulti apply-script (constantly *platform*))

(defmulti listen! (constantly *platform*))

(defmulti make-canvas! (constantly *platform*))

(defmulti component->canvas (constantly *platform*))

(defprotocol IComponent
  (size [_]))

(defn render
  [component-fn & args]
  (let [component (apply component-fn args)
        [w h] (size component)
        canvas (make-canvas! w h)]
    (component->canvas component canvas)
    canvas))

(defn render-ch
  [root options]
  (let [ch (chan)]
    (go-loop [last-script []]
      (reset! *script* [])
      (let [root-id (render root (<! ch))]
        (when-not (= last-script @*script*)
          (apply-script @*script* root-id options)))
      (recur @*script*))
    ch))

(defn init!
  [root state-atom options]
  (let [ch (render-ch root options)]
    (go (>! ch @state-atom))
    (add-watch state-atom :render
               #(go (>! ch %4)))))
