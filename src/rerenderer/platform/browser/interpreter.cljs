(ns rerenderer.platform.browser.interpreter
  (:require [cljs.core.match :refer-macros [match]]))

(def refs-cache (atom {'document js/document}))

(defn extract-var
  [refs var]
  (match var
    [:ref x] (refs x)
    [:val x] x))

; TODO: allow to create all available classes dynamically
(defn create-instance
  "Creates an instance of `cls` with `args` and puts it in `refs`."
  [refs [_ ref-id] cls args]
  (let [prepared-args (mapv #(extract-var refs %) args)
        inst (match [cls prepared-args]
               ["Canvas" []] (.createElement js/document "canvas"))]
    (assoc refs ref-id inst)))

(defn set-attr
  "Sets `value` to `attr` of `ref`."
  [refs [_ ref] attr value]
  (aset (refs ref) attr (extract-var refs value))
  refs)

(defn get-attr
  "Gets value of `attr` of `ref` and puts it in `refs`."
  [refs [_ result-ref] ref attr]
  (assoc refs result-ref (aget (extract-var refs ref) attr)))

(defn call-method
  "Calls `method` of `var` with `args` and puts result in `refs`."
  [refs [_ result-ref] var method args]
  (let [obj (extract-var refs var)
        js-args (clj->js (mapv #(extract-var refs %) args))
        call-result (.apply (aget obj method) obj js-args)]
    (assoc refs result-ref call-result)))

(defn free
  "Removes `ref` from `refs`."
  [refs [_ ref]]
  (dissoc refs ref))

(defn interprete-instruction
  "Interpretes a single `instruction` of script and returns changed `refs`."
  [refs instruction]
  (try
    (match instruction
      [:new result-var cls args] (create-instance refs result-var cls args)
      [:set var attr value] (set-attr refs var attr value)
      [:get result-var var attr] (get-attr refs result-var var attr)
      [:call result-var var method args] (call-method refs result-var var
                                                      method args)
      [:free var] (free refs var))
    (catch js/Error e
      (println "Can't execute instruction " instruction)
      (throw e))))

(defn interprete!
  "Interpretes `script` and returns hash-map with vars."
  [script]
  (swap! refs-cache #(reduce interprete-instruction % script)))
