# rerenderer

[![Clojars Project](https://img.shields.io/clojars/v/org.rerenderer/rerenderer.svg)](https://clojars.org/org.rerenderer/rerenderer)

[Documentation.](https://rerenderer.github.io/rerenderer/)

Simple platform agnostic react-like library for drawing on canvas,
handling events and playing sounds.

Supported platforms:

- Browser;
- Android.

## How it works?

When state (atom) changes `rerenderer` calls a rendering function,
inside the function we work with shadow canvas (like shadow dom in React).
And applies changes to real canvas only when shadow canvas has difference
with shadow canvas of the previous call of the rendering function.
 
And as a real canvas we can use browser canvas, android canvas
or even iOS canvas (not implemented).

## Usage

Create new project from template (name should contains at least one dot):

```bash
lein new rerenderer-game com.my-super-game
```

And start developing with figwheel with:

```bash
lein figwheel
```

For building standalone version run:

```bash
lein cljsbuild once
```

For more information look at [lein-figwheel](https://github.com/bhauman/lein-figwheel) and [lein-cljsbuild](https://github.com/bhauman/lein-cljsbuild).

## Android

For using rerenderer figwheel on android please change `getUrl` in your `MainActivity`, like:

```java
public class MainActivity extends RerendererActivity {
    @Override
    public String getUrl() {
        return "http://192.168.2.100:3449";
    }
}
```

Host should be the same as in `:figwheel :websocket-host`.

For building android version for figwheel use:

```bash
cd android
chmod +x gradlew
./gradlew installDebug
```

If you want to build standalone version for android, change `getUrl` to 
`"file:///android_asset/index.html"` and:

```bash
lein cljsbuild once
cd android 
./gradlew copyAssets installDebug
```

## Creating your own primitives

If built-in primitives aren't enough for you, it's easy to create your
own. You need to create function (primitive) that returns object (component)
that implemented `IComponent` and platform-specific (`IAndroid`, `IBrowser`)
protocols. And for nicer debugging it's good thing to implement 
`Object` protocol with `toString` method.

~~~clojure
(ns example
  (:require [rerenderer.platform.browser.core :refer [IBrowser]]
            [rerenderer.platform.android.core :refer [IAndroid]]
            [rerenderer.lang.core :as r :include-macros true]
            [rerenderer.types.component :refer [IComponent component->string
                                                prepare-childs]]))
                                                
(defn my-primitive
  [{:keys [width height x y] :as props} & childs]
  (reify
    Object
    (toString [this] (component->string this))
    IComponent
    (tag [_] "my-primitive")
    (childs [_] (prepare-childs childs))
    (props [_] props)
    IBrowser
    (render-browser [_ ctx] ; ctx is canvas's 2d context
      ; Will run as `ctx.fillRect(x, y, width, height)`
      (r/.. ctx (fillRect x y width height)))
    IAndroid
    (render-android [_ bitmap] ; android.graphics.Bitmap
      ; Will run as `new android.graphics.Paint()`
      (let [paint (r/new (r/.. android -graphics -Paint))]
        ; Will run as `bitmap.drawRect(x, y, width, height, paint)
        (r/.. bitmap (drawRect x y width height paint))))))
~~~

It's necessary to use `component->string` in `toString` and 
`prepare-childs` in `childs`.

Code inside platform's methods will be executed on platform's side. So
here we should use `r/..`, `r/new` and `r/set!` instead of clojure's `..`,
`new` and `set!`. For more information look to
[documentation of rerenderer.lang.core](https://rerenderer.github.io/rerenderer/rerenderer.lang.core.html).

Also code inside platform's methods should be fast and don't have side
effects. It will be executed when any prop or child changed.
