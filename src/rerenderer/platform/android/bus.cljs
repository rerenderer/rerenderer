(ns rerenderer.platform.android.bus
  (:require [rerenderer.platform.utils :refer [to-json from-json]]))

; Interface for android-side interpreter/event-emiter,
; it should be available at `window.android`
(defn available?
  "Returns `true` when interop object available."
  []
  (aget js/window "android"))

(defn interprete!
  "Interpretes script on android side."
  [script root]
  (.interprete js/android (to-json {:script script
                                    :root root})))

(defn on-event!
  "Subscribes to events, event have format {:event name **props}"
  [callback]
  (set! (.-androidEventsCallback js/window)
        #(callback (from-json %))))

(on-event! #(.log js/console "Event listener not set, skip:" %))

(def information (atom {:width 0
                        :height 0
                        :input #{:touch}}))

(set! (.-androidUpdateInformation js/window)
      #(swap! information assoc
              :width %1
              :height %2))

