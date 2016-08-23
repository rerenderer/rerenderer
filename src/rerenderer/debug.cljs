(ns rerenderer.debug
  "Debug functions for using in REPL.")

(defn rerender!
  "Rerenders game manualy, useful with figwheel.

  * `game` - `Game` record."
  [game]
  (println "Rerender game manually!")
  (let [{:keys [render-state! state-atom]} game]
    (render-state! @state-atom)))

(defn swap-state!
  "Swaps game state, works like atom's `swap!`.

  * `game` - `Game` record;
  * `args` - `swap!` args."
  [game & args]
  (apply swap! (:state-atom game) args))

(defn reset-state!
  "Resets game state, works like atom's `reset!`.

  * `game` - `Game` record;
  * `value` - new value."
  [game value]
  (reset! (:state-atom game) value))

(defn reset-state-to-initial!
  "Resets game state to initial value.

  * `game` - `Game` record."
  [game]
  (reset! (:state-atom game) (:initial-state game)))
