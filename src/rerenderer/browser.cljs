(ns rerenderer.browser
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.match :refer-macros [match]]
            [cljs.core.async :refer [>! <! chan]]
            [clojure.string :refer [replace-first]]
            [rerenderer.core :refer [IPlatform]]))

(defn browser-call!
  [canvas-ctx path]
  (loop [[step & other] path
         obj canvas-ctx]
    (if step
      (recur other (let [[method & args] step
                         args (or args [])]
                     (if (= (-> method first) \-)
                       (aget obj (replace-first method "-" ""))
                       (.apply (aget obj method) obj (clj->js args)))))
      obj)))

(defn browser-set!
  [canvas-ctx path value]
  (let [obj (browser-call! canvas-ctx (butlast path))
        attr (-> path last first (replace-first "-" ""))]
    (aset obj attr value)))

(defn browser
  [canvas]
  (let [canvas-ctx (.getContext canvas "2d")
        platform-id (str (gensym))]
    (set! (.. canvas -dataset -platfromId) platform-id)
    (reify IPlatform
      (render! [_ ctx]
        (when (= platform-id (.. canvas -dataset -platfromId))
          (doseq [action ctx]
            (match action
              [:set path value] (browser-set! canvas-ctx path value)
              [:call path] (browser-call! canvas-ctx path)))))
      (listen! [_ event ch]
        (.addEventListener canvas (name event)
                           #(go (>! ch %))))
      (image [_ src]
        (let [img (js/Image.)
              loaded-ch (chan)]
          (set! (.-src img) src)
          (.addEventListener img "load" #(go (>! loaded-ch true)))
          (go (<! loaded-ch)
              img)))
      (sound [_ src]
        (go (js/Audio. src)))
      (play! [_ sound]
        (.play sound)))))
