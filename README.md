# rerenderer

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

Renders rectangle that changes colors on click:

```clojure    

```

## TODO: Usage on android
