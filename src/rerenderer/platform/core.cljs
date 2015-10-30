(ns rerenderer.platform.core)

(def platform (atom nil))

(defn get-platform [] @platform)

; Should be implemented on each platform:

(defmulti apply-script get-platform)

(defmulti listen! get-platform)

(defmulti render! get-platform)

(defmulti render-to! get-platform)
