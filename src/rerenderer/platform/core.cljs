(ns rerenderer.platform.core)

; Should be changed when platform deceted:
(def platform (atom nil))

(defn get-platform [] @platform)

; Should be implemented on each platform:

(defmulti listen!
  "(event-ch options) -> null
  Should put events to `event-ch`, supported events:
    - [:click {:x :y}]
    - [:keydown {:keycode}]
    - [:keyup {:keycode}]"
  get-platform)

(defmulti render
  "Renders one component.

  (parent-canvas component) -> result"
  get-platform)

(defmulti render-to (get-platform))

(defmulti root-canvas get-platform)

;(defmulti finalize
;  "Call after rendering scene.")

(defmulti information
  "Returns platform information in format:
  {:width int-px
   :height int-px
   :input #{:mouse :keyboard :touch}}"
  get-platform)

