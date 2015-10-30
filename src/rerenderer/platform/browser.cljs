(ns ^:figwheel-always rerenderer.platform.browser
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.match :refer-macros [match]]
            [cljs.core.async :refer [>! chan]]
            [rerenderer.interop :as r :include-macros true]
            [rerenderer.platform.core :as platform]
            [rerenderer.core :refer [IComponent size position]]))

(def vars (atom {'document js/document}))

(defn var-or-val
  "Returns value of var named `value` or just `value`."
  [vars value]
  (match value
    [:var x] (get vars x)
    [:val x] x))

(defn create-instance
  "Creates an instance of `cls` with `args` and puts it in `vars`."
  [vars result-var cls args]
  (let [prepared-args (mapv #(var-or-val vars %) args)
        inst (match [cls prepared-args]
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
  (try
    (match line
      [:new result-var cls args] (create-instance vars result-var cls args)
      [:set var attr value] (set-attr vars var attr value)
      [:get result-var var attr] (get-attr vars result-var var attr)
      [:call result-var var method args] (call-method vars result-var var
                                                      method args)
      [:free var] (dissoc vars var))
    (catch js/Error e
      (println "Can't execute line " line)
      (throw e))))

(defn interprete
  "Interpretes `script` and returns hash-map with vars."
  [script]
  (swap! vars #(reduce interprete-line % script)))

(when-not @platform/platform
  (reset! platform/platform :browser))

(defmethod platform/apply-script :browser
  [script root-id {:keys [canvas]}]
  (let [ctx (.getContext canvas "2d")
        pool (interprete script)]
    (.drawImage ctx (pool root-id) 0 0)))

(defn translate-event
  [event data]
  (condp = event
    :click [:click {:x (.-clientX data)
                    :y (.-clientY data)}]
    [event data]))

(defmethod platform/listen! :browser
  [ch event {:keys [canvas]}]
  (.addEventListener canvas (name event)
                     #(go (>! ch (translate-event event %)))))

(defprotocol IBrowser
  (render-browser [_ ctx]))

(defmethod platform/render! :browser
  [component]
  {:pre [(satisfies? IComponent component)
         (satisfies? IBrowser component)]}
  (let [[w h] (size component)
        canvas (r/new Canvas)
        ctx (r/.. canvas (getContext "2d"))]
    (r/set! (r/.. canvas -width) w)
    (r/set! (r/.. canvas -height) h)
    (render-browser component ctx)
    canvas))

(defmethod platform/render-to! :browser
  [component ctx]
  {:pre [(satisfies? IComponent component)
         (satisfies? IBrowser component)]}
  (let [[x y] (position component)]
    (r/.. ctx (drawImage (platform/render! component) x y))))
