(ns rerenderer.lang.core
  (:require [rerenderer.lang.forms :as forms]))

(def ^:no-doc script (atom []))

(defn to-var
  [x]
  (if (satisfies? forms/IVar x)
    x
    (forms/->Val x)))

(defn make-ref [] (forms/->Ref (gensym)))

(defn ^:no-doc rnew
  [cls args]
  (let [result-ref (make-ref)]
    (swap! script conj (forms/->New result-ref cls (mapv to-var args)))
    result-ref))

(defn ^:no-doc rset!
  [ref attr value]
  (swap! script conj (forms/->Set (to-var ref) attr (to-var value))))

(defn ^:no-doc rget
  [ref attr]
  (let [result-ref (make-ref)]
    (swap! script conj (forms/->Get result-ref (to-var ref) attr))
    result-ref))

(defn ^:no-doc rcall!
  [ref method args]
  (let [result-ref (make-ref)]
    (swap! script conj (forms/->Call result-ref (to-var ref) method
                                     (mapv to-var args)))
    result-ref))

; TODO: ensure that it works correctly
(def static (forms/->Ref "STATIC"))
