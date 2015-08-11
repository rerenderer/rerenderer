(ns rerenderer.browser
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.match :refer-macros [match]]
            [cljs.core.async :refer [>! chan]]
            [rerenderer.core :as r :include-macros true]))

(defn var-or-val
  "Returns value of var named `value` or just `value`."
  [vars value]
  (get vars value value))

(defn create-instance
  "Creates an instance of `cls` with `args` and puts it in `vars`."
  [vars result-var cls args]
  (let [prepared-args (mapv #(var-or-val vars %) args)
        inst (match [cls prepared-args]
               [:Image []] (js/Image.)
               [:Canvas []] (.createElement js/document "canvas"))]
    (assoc vars result-var inst)))

(defn set-attr
  "Sets `value` to `attr` of `var`."
  [vars var attr value]
  (aset (vars var) attr (var-or-val vars value))
  vars)

(defn get-attr
  "Gets value of `attr` of `var` and puts it in `vars`."
  [vars result-var var attr]
  (assoc vars result-var (aget (vars var) attr)))

(defn call-method
  "Calls `method` of `var` with `args` and puts result in `vars`."
  [vars result-var var method args]
  (let [obj (vars var)
        js-args (clj->js (mapv #(var-or-val vars %) args))
        call-result (.apply (aget obj method) obj js-args)]
    (assoc vars result-var call-result)))

(defn interprete-line
  "Interpretes a single `line` of script and returns changed `vars`."
  [vars line]
  (match line
    [:new result-var cls args] (create-instance vars result-var cls args)
    [:set var attr value] (set-attr vars var attr value)
    [:get result-var var attr] (get-attr vars result-var var attr)
    [:call result-var var method args] (call-method vars result-var var
                                                    method args)))

(defn interprete
  "Interpretes `script` and returns hash-map with vars."
  [script]
  (reduce interprete-line {} script))

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
