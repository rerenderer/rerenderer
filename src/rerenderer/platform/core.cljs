(ns rerenderer.platform.core)

(def platform (atom nil))

(defn get-platform [] @platform)

; Should be implemented on each platform:

(defmulti apply-script!
  "(script, root-canvas-id, options) -> null
  Run script on platform side. final result of script on `root-canvas-id`."
  get-platform)

(defmulti listen!
  "(event-ch options) -> null
  Should put events to `event-ch`, supported events:
    - [:click {:x :y}]
    - [:keydown {:keycode}]
    - [:keyup {:keycode}]"
  get-platform)

(defmulti render
  "(component) -> RenderResult"
  get-platform)

(defmulti render-to
  "(child-node, parent-node) -> script
  Render child node on top of parent node"
  get-platform)

