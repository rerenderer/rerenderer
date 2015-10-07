(ns ^:figwheel-always rerenderer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! sliding-buffer timeout]]
            [cljs.core.match :refer-macros [match]]))

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
(defmulti get-platform true?)

(defmulti apply-script get-platform)

(defmulti listen! get-platform)

(defmulti render! get-platform)

(defmulti render-to! get-platform)

; /

(defprotocol IComponent
  (size [_])
  (position [_]))

(defn arg-for-tree
  [tree arg]
  (match arg
    [:var x] [:var (get tree x)]
    _ arg))

(defn build-leaf
  [tree line]
  (match line
    [:new result-var cls args]
    (assoc tree
      result-var [:new cls (map #(arg-for-tree tree %) args)])
    [:set var attr value]
    (update tree var conj [:set attr value])
    [:get result-var var attr]
    (assoc tree
      result-var [:get (get tree var) attr])
    [:call result-var var method args]
    (-> tree
        (assoc result-var [:call (get tree var) method
                           (map #(arg-for-tree tree %) args)])
        (update var conj [:call method (map #(arg-for-tree tree %) args)]))))

(defn build-tree
  [script]
  (reduce build-leaf (sorted-map) script))

(defn get-duplicates
  [tree v]
  (map first (filter #(= v (last %)) tree)))

(defn find-duplicates
  [tree]
  (loop [tree tree
         result {}]
    (if (seq tree)
      (let [[k v] (first tree)
            tree (dissoc tree k)
            duplicates (get-duplicates tree v)
            tree (apply dissoc tree duplicates)
            reversed (into {} (for [dup duplicates]
                                [dup k]))]
        (if (seq duplicates)
          (recur tree (merge result reversed))
          (recur tree result)))
      result)))

(defn replace-id-in-args
  [args from to]
  (mapv #(if (= % [:var from]) [:var to] %) args))

(defn replace-id
  [script from to]
  (remove nil?
          (for [line script]
            (match line
              ; Creational:
              [:new from & _] nil
              [:get from & _] nil
              [:call from & _] nil
              [:set from & _] nil
              ; As callee:
              [:get result-var from attr] [:get result-var to attr]
              [:call result-var from method args] [:call result-var to method args]
              ; As argument
              [:new result-var cls (args :guard #(some #{[:var from]} %))]
              [:new result-var cls (replace-id-in-args args from to)]

              [:call result-var var method (args :guard #(some #{[:var from]} %))]
              [:call result-var var method (replace-id-in-args args from to)]
              _ line))))

(defn remove-duplicates
  [script]
  (reduce (fn [script [from to]] (replace-id script from to))
          script (find-duplicates (build-tree script))))

(defn render-ch
  [root options]
  (let [ch (chan (sliding-buffer 1))]
    (go-loop [last-script []]
      (<! (timeout (/ 1000 fps-limit)))
      (reset! script [])
      (let [[_ root-id] (render! (root (<! ch)))
            current-script (remove-duplicates @script)]
        (when-not (= current-script @script)
          (apply-script current-script root-id options))
        (recur current-script)))
    ch))

(defn init!
  [root state-atom options]
  (let [ch (render-ch root options)]
    (go (>! ch @state-atom))
    (add-watch state-atom :render
               #(go (>! ch %4)))))
