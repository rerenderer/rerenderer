(ns rerenderer.debug)

(defn rerender!
  "Rerenders game manualy, useful with figwheel."
  [game]
  (println "Rerender game manually!")
  (swap! (:state-atom game) identity))

(defn swap-state!
  "Swaps game state, works like atom's swap!"
  [game & args]
  (apply swap! (:state-atom game) args))

(defn reset-state!
  "Resets game state, works like atom's reset!"
  [game value]
  (reset! (:state-atom game) value))

(defn reset-state-to-initial!
  "Resets game state to initial value."
  [game]
  (reset! (:state-atom game) (:initial-state game)))
