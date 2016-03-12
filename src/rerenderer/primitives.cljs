(ns rerenderer.primitives
  "Simple primitives for drawing. Using primitives is more preferd then
  creating components by yourself or operating with native-objects."
  (:require [cljsjs.tinycolor]
            [rerenderer.platform.browser.core :refer [IBrowser]]
            [rerenderer.platform.android.core :refer [IAndroid]]
            [rerenderer.lang.core :as r :include-macros true]
            [rerenderer.types.component :refer [IComponent component->string]]))

(def ->rgba (memoize
              (fn [color]
                (let [{:keys [r g b a]} (-> color
                                            clj->js
                                            js/tinycolor
                                            .toRgb
                                            (js->clj :keywordize-keys true))]
                  [r g b (* 255 a)]))))

(defn rectangle
  "Rectangle primitive, can be nested:

  ```
  (rectangle {:color \"red\"
              :width 100
              :height 300
              :x 10
              :y 10}
    childs)
  ```"
  [{:keys [width height color x y] :as props} & childs]
  {:pre [(not (nil? width))
         (not (nil? height))
         (not (nil? color))]}
  (let [color (->rgba color)
        x (or x 0)
        y (or y 0)
        props (assoc props
                :color color
                :x x
                :y y)]
    (reify
      Object
      (toString [this] (component->string this))
      IComponent
      (tag [_] "rectangle")
      (childs [_] (flatten childs))
      (props [_] props)
      IBrowser
      (render-browser [_ ctx]
        (let [[r g b a] color
              color (str "rgba(" r ", " g ", " b ", " a ")")]
          (r/set! (r/.. ctx -fillStyle) color))
        (r/.. ctx (fillRect 0 0 width height)))
      IAndroid
      (render-android [_ canvas]
        (let [paint (r/new (r/.. android -graphics -Paint))
              [r g b a] color]
          (r/.. paint (setARGB a r g b))
          (r/.. canvas (drawRect 0 0 width height paint)))))))

(defn text
  "Text primitive, can be nested:

  ```
  (text {:width 100
         :height 30
         :font-size 10
         :color \"#ff00ff\"
         :x 10
         :y 10
         :value \"Hi there\"}
    childs)
  ```"
  [{:keys [width height font-size color x y value] :as props} & childs]
  {:pre [(not (nil? width))
         (not (nil? height))
         (not (nil? font-size))
         (not (nil? color))
         (not (nil? value))]}
  (let [color (->rgba color)
        x (or x 0)
        y (or y 0)
        props (assoc props
                :color color
                :x x
                :y y)]
    (reify
      Object
      (toString [this] (component->string this))
      IComponent
      (tag [_] "text")
      (childs [_] (flatten childs))
      (props [_] props)
      IBrowser
      (render-browser [_ ctx]
        (let [[r g b a] color
              color (str "rgba(" r ", " g ", " b ", " a ")")]
          (r/set! (r/.. ctx -fillStyle) color)
          (r/set! (r/.. ctx -font) (str font-size "px sans")))
        (r/.. ctx (fillText value 0 font-size)))
      IAndroid
      (render-android [_ canvas]
        (let [paint (r/new (r/.. android -graphics -Paint))
              [r g b a] color
              y (- height y)]
          (r/.. paint (setARGB a r g b))
          (r/.. paint (setTextSize font-size))
          (r/.. canvas (drawText value x y paint)))))))

(def get-image-url
  (memoize (fn [src]
             (str (.. document -location -protocol)
                  ":/"
                  (.. document -location -host)
                  src))))

(defn image
  "Image primitive, can be nested:

  ```
  (image {:width 100
          :height 200
          :src \"/bird.png\" ; absolute path to image
          :sx 20 ; x on source image, usable for cutting sprites
          :sy 30 ; y on source image, usable for cutting sprites
          :x 10
          :y 20}
    childs)
  ```"
  [{:keys [width height src x y sx sy] :as props} & childs]
  {:pre [(not (nil? width))
         (not (nil? height))
         (not (nil? src))]}
  (let [x (or x 0)
        y (or y 0)
        sx (or sx 0)
        sy (or sy 0)
        props (assoc props
                :x x
                :y y
                :sx sx
                :sy sy)]
    (reify
      Object
      (toString [this] (component->string this))
      IComponent
      (tag [_] "image")
      (childs [_] (flatten childs))
      (props [_] props)
      IBrowser
      (render-browser [_ ctx]
        (let [img (r/.. document (getElementById src))]
          (r/.. ctx (drawImage img sx sy width height 0 0 width height))))
      IAndroid
      (render-android [_ canvas]
        (let [url (get-image-url src)
              bitmap (r/.. com -nvbn -tryrerenderer -RerendererLoader (bitmapFromUrl url))
              clipped (r/.. android -graphics -Bitmap (createBitmap bitmap sx sy width height))]
          (r/.. canvas (drawBitmap clipped 0 0 (r/new Paint))))))))
