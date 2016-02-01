(ns rerenderer.lang.forms)

(defprotocol IForm
  (serialize [this]))

(defprotocol IVar)

(defprotocol IInstruction)

; Representation of variables:
(defrecord Ref [id]
  IVar
  IForm
  (serialize [_] [:ref (str id)]))

(defrecord Val [value]
  IVar
  IForm
  (serialize [_] [:val value]))

(defrecord Static [id]
  IVar
  IForm
  (serialize [_] [:static id]))

; Instructions:
(defrecord New [result-ref cls args]
  IInstruction
  IForm
  (serialize [_] [:new (serialize result-ref) (serialize cls) (mapv serialize args)]))

(defrecord Set [ref attr value]
  IInstruction
  IForm
  (serialize [_] [:set (serialize ref) (str attr) (serialize value)]))

(defrecord Get [result-ref ref attr]
  IInstruction
  IForm
  (serialize [_] [:get (serialize result-ref) (serialize ref) (str attr)]))

(defrecord Call [result-ref ref method args]
  IInstruction
  IForm
  (serialize [_] [:call (serialize result-ref) (serialize ref) (str method)
                  (mapv serialize args)]))

(defrecord Free [ref]
  IInstruction
  IForm
  (serialize [_] [:free (serialize ref)]))
