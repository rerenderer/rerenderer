(ns ^:figwheel-always rerenderer.primitives
  "Simple primitives for drawing. Using primitives is more preferd then
  creating components by yourself or operating with native-objects."
  (:require [rerenderer.platform.browser :refer [IBrowser]]
            [rerenderer.platform.android :refer [IAndroid]]
            [rerenderer.platform.core :as p]
            [rerenderer.interop :as r :include-macros true]
            [rerenderer.core :refer [IComponent]]))

(defn ^:no-doc render-childs
  [parent childs]
  (doseq [child (flatten childs)
          :when (not (nil? child))]
    (p/render-to! child parent)))

(defn rectangle
  "Rectangle primitive, can be nested:

  ```
  (rectangle {:color [255 0 0 0] ; argb
              :width 100
              :height 300
              :x 10
              :y 10}
    #_ another-rectangle)
  ```"
  [{:keys [width height color x y]
    :or {width 0
         height 0
         color [255 0 0 0]
         x 0
         y 0}}
   & childs]
  (reify
    IComponent
    (size [_] [width height])
    (position [_] [x y])
    IBrowser
    (render-browser [_ ctx]
      (let [[a r g b] color
            color (str "rgba(" r ", " g ", " b ", " a ")")]
        (r/set! (r/.. ctx -fillStyle) color))
      (r/.. ctx (fillRect 0 0 width height))
      (render-childs ctx childs))
    IAndroid
    (render-android [_ canvas]
      (let [paint (r/new Paint)
            [a r g b] color]
        (r/.. paint (setARGB a r g b))
        (r/.. canvas (drawRect 0 0 width height paint)))
      (render-childs canvas childs))))

(defn text
  "Text primitive, can be nested:

  ```
  (text {:width 100
         :height 30
         :font-size 10
         :color [255 255 255 0]
         :x 10
         :y 10}
    \"Hi there\")
  ```"
  [{:keys [width height font-size color x y]
    :or {width 0
         height 0
         font-size 0
         color [255 0 0 0]
         x 0
         y 0}}
   value & childs]
  (reify
    IComponent
    (size [_] [width height])
    (position [_] [x y])
    IBrowser
    (render-browser [_ ctx]
      (let [[a r g b] color
            color (str "rgba(" r ", " g ", " b ", " a ")")]
        (r/set! (r/.. ctx -fillStyle) color)
        (r/set! (r/.. ctx -font) (str font-size "px sans")))
      (r/.. ctx (fillText value 0 font-size))
      (render-childs ctx childs))
    IAndroid
    (render-android [_ canvas]
      (let [paint (r/new Paint)
            [a r g b] color]
        (r/.. paint (setARGB a r g b))
        (r/.. paint (setTextSize font-size))
        (r/.. canvas (drawText value x y paint)))
      (render-childs canvas childs))))

(defn image
  "Image primitive, can be nested:

  ```
  (image {:width 100
          :height 200
          :src \"bird\" ; `id` of image on bootstraping  html page
          :sx 20 ; x on source image, usable for cutting sprites
          :sy 30 ; y on source image, usable for cutting sprites
          :x 10
          :y 20})
  ```"
  [{:keys [width height src x y sx sy]
    :or {width 0
         height 0
         x 0
         y 0
         sx 0
         sy 0}}
   & childs]
  (reify
    IComponent
    (size [_] [width height])
    (position [_] [x y])
    IBrowser
    (render-browser [_ ctx]
      (let [img (r/.. 'document (getElementById src))]
        (r/.. ctx (drawImage img sx sy width height 0 0 width height)))
      (render-childs ctx childs))
    IAndroid
    (render-android [_ canvas]
      (let [url (.. js/document (getElementById src) -src)
            bitmap (r/.. 'RerendererLoader (bitmapFromUrl url))
            clipped (r/.. 'Bitmap (createBitmap bitmap sx sy width height))]
        (r/.. canvas (drawBitmap clipped 0 0 (r/new Paint)))
        (render-childs canvas childs)))))
