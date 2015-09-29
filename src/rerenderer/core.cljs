(ns ^:figwheel-always rerenderer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! sliding-buffer]]
            [cljs.core.match :refer-macros [match]]))

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


; api-v2
(defmulti get-platform true?)

(defmulti apply-script get-platform)

(defmulti listen! get-platform)

(defmulti render! get-platform)

(defmulti render-to! get-platform)

(defprotocol IComponent
  (size [_])
  (position [_]))

(defn render-ch
  [root options]
  (let [ch (chan (sliding-buffer 1))]
    (go-loop [last-script []]
      (reset! script [])
      (let [[_ root-id] (render! (root (<! ch)))]
        (when-not (= last-script @script)
          (apply-script @script root-id options)))
      (recur @script))
    ch))

(defn init!
  [root state-atom options]
  (let [ch (render-ch root options)]
    (go (>! ch @state-atom))
    (add-watch state-atom :render
               #(go (>! ch %4)))))
