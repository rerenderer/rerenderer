(ns rerenderer.primitives
  "Simple primitives for drawing. Using primitives is more preferd then
  creating components by yourself or operating with native-objects."
  (:require [rerenderer.platform.browser.core :refer [IBrowser]]
            [rerenderer.platform.android.core :refer [IAndroid]]
            [rerenderer.component :refer [IComponent component->string
                                                prepare-childs ->rgba
                                                ->url]]))

(defn rectangle
  "Rectangle primitive, can be nested.

  * `width` - rectangle width - number;
  * `height` - rectange height - number;
  * `color` - rectangle color, supported formats: `#ff0000`, `rgb(255, 255, 0)`, `argb(255, 0, 0, 0)`, `red`;
  * `x` - rectangle upper left corner x coordinate on parent;
  * `y` - rectangle upper left corner y coordinate on parent;
  * `childs` - primitives rendered inside the rectangle.

  Usage:

  ```
  (rectangle {:color \"red\"
              :width 100
              :height 300
              :x 10
              :y 10}
    (rectangle {:color \"#00ff00\"
                :x 50
                :y 50
                :x 10
                :y 10}))
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
      (childs [_] (prepare-childs childs))
      (props [_] props)
      IBrowser
      (render-browser [_ ctx]
        (let [[r g b a] color
              color (str "rgba(" r ", " g ", " b ", " a ")")]
          (set! (.-fillStyle ctx) color))
        (.fillRect ctx 0 0 width height))
      IAndroid
      (android-primitive [_] "bundled.rectangle"))))

(defn text
  "Text primitive, can be nested.

  * `width` - text holder width - number;
  * `height` - text holder height - number;
  * `font-size` - font size in px;
  * `color` - text color, supported formats: #ff0000, rgb(255, 255, 0), argb(255, 0, 0, 0), red;
  * `x` - text holder upper left corner x coordinate on parent;
  * `y` - text holder upper left corner y coordinate on parent;
  * `value` - text;
  * `childs` - primitives rendered inside the rectangle.

  Usage:

  ```
  (text {:width 100
         :height 30
         :font-size 10
         :color \"#ff00ff\"
         :x 10
         :y 10
         :value \"Hi there\"}
    (rectangle {:color \"#00ff00\"
                :x 50
                :y 50
                :x 10
                :y 10}))
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
      (childs [_] (prepare-childs childs))
      (props [_] props)
      IBrowser
      (render-browser [_ ctx]
        (let [[r g b a] color
              color (str "rgba(" r ", " g ", " b ", " a ")")]
          (set! (.-fillStyle ctx) color)
          (set! (.-font ctx) (str font-size "px sans")))
        (.fillText ctx value 0 font-size))
      IAndroid
      (android-primitive [_] "bundled.text")
      ;(render-android [_ bitmap]
      ;  (let [paint (r/new (r/.. android -graphics -Paint))
      ;        [r g b a] color
      ;        y (- height y)]
      ;    (r/.. paint (setARGB a r g b))
      ;    (r/.. paint (setTextSize font-size))
      ;    (r/.. bitmap (drawText value x y paint))))
      )))

(defn image
  "Image primitive, can be nested.

  * `width` - text holder width - number;
  * `height` - text holder height - number;
  * `x` - text holder upper left corner x coordinate on parent;
  * `y` - text holder upper left corner y coordinate on parent;
  * `sx` - source image x, useful for cutting sprites;
  * `sy` - source image y, useful for cutting sprites;
  * `childs` - primitives rendered inside the rectangle.

  Example:

  ```
  (image {:width 100
          :height 200
          :src \"/bird.png\" ; absolute path to image
          :sx 20 ; x on source image, usable for cutting sprites
          :sy 30 ; y on source image, usable for cutting sprites
          :x 10
          :y 20}
    (rectangle {:color \"#00ff00\"
                :x 50
                :y 50
                :x 10
                :y 10}))
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
      (childs [_] (prepare-childs childs))
      (props [_] props)
      ;IBrowser
      ;(render-browser [_ ctx]
      ;  (let [img (r/.. document (getElementById src))]
      ;    (r/.. ctx (drawImage img sx sy width height 0 0 width height))))
      ;IAndroid
      ;(render-android [_ bitmap]
      ;  (let [url (->url src)
      ;        bitmap (r/.. com -nvbn -tryrerenderer -RerendererLoader (bitmapFromUrl url))
      ;        clipped (r/.. android -graphics -Bitmap (createBitmap bitmap sx sy width height))]
      ;    (r/.. bitmap (drawBitmap clipped 0 0 (r/new Paint)))))
      )))
