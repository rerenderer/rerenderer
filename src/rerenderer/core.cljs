(ns ^:figwheel-always rerenderer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! sliding-buffer timeout]]
            [cljs.core.match :refer-macros [match]]
            [rerenderer.optimizer :refer [reuse]]))

(def fps-limit 25)

(def script (atom []))

(defn get-var
  []
  (gensym))

(defn prepare-arg
  [arg]
  (match arg
    [:var _] arg
    [:val _] arg
    _ [:val arg]))

(defn unpack-var
  [var]
  (match var
    [:var x] x
    _ var))

; [:new result-var class [arguments]]
; each argument can be [:var var] or [:val val]
(defn rnew
  [cls & args]
  (let [id (get-var)]
    (swap! script conj [:new id cls (mapv prepare-arg args)])
    [:var id]))

; [:set var attr value]
; value can be [:var var] or [:val val]
(defn rset!
  [var attr value]
  (swap! script conj [:set (unpack-var var) attr (prepare-arg value)]))

; [:get result-var var attr]
(defn rget
  [var attr]
  (let [id (get-var)]
    (swap! script conj [:get id (unpack-var var) attr])
    [:var id]))

; [:call result-var var method [arguments]]
; each argument can be [:var var] or [:val val]
(defn rcall!
  [var method & args]
  (let [id (get-var)]
    (swap! script conj [:call id (unpack-var var)
                        method (mapv prepare-arg args)])
    [:var id]))


; should be implemented in platforms:
(def platform (atom nil))

(defn get-platform [] @platform)

(defmulti apply-script get-platform)

(defmulti listen! get-platform)

(defmulti render! get-platform)

(defmulti render-to! get-platform)

; /

(defprotocol IComponent
  (size [_])
  (position [_]))

(defn render-ch
  [root options]
  (let [ch (chan (sliding-buffer 1))]
    (go-loop [cache {}]
      (<! (timeout (/ 1000 fps-limit)))
      (reset! script [])
      (let [[_ root-id] (render! (root (<! ch)))]
        (let [[cache current-script root-id] (reuse cache @script root-id)]
          (apply-script current-script root-id options)
          (recur cache))))
    ch))

(defn init!
  [root state-atom options]
  (let [ch (render-ch root options)]
    (go (>! ch @state-atom))
    (add-watch state-atom :render
               #(go (>! ch %4)))))
