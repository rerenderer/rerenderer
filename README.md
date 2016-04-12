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
