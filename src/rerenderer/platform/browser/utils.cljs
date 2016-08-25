(ns rerenderer.platform.browser.utils)

(def get-image
  (memoize (fn [src]
             (let [img (js/Image.)]
               (set! (.-src img) src)
               img))))
