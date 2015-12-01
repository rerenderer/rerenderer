(defproject rerenderer "0.2.0-SNAPSHOT"
  :description "Simple platform agnostic react-like library for drawing on canvas, handling events and playing sounds."
  :url "https://github.com/rerenderer/rerenderer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.async "0.2.371"]
                 [com.cognitect/transit-cljs "0.8.225"]]
  :source-paths ["src" "script"]
  :profiles {:dev {:dependencies [[figwheel-sidecar "0.3.7"]
                                  [figwheel "0.3.7"]
                                  [binaryage/devtools "0.3.0"]]
                   :plugins [[lein-cljsbuild "1.0.6"]
                             [lein-figwheel "0.3.7"]
                             [lein-codox "0.9.0"]]
                   :codox {:language :clojurescript
                           :namespaces [rerenderer.core rerenderer.interop rerenderer.primitives]
                           :doc/format :markdown
                           :output-path "docs"}
                   :cljsbuild {:builds {:test {:source-paths ["test" "src"]
                                               :compiler {:output-to "target/cljs-test.js"
                                                          :optimizations :whitespace
                                                          :pretty-print true}}
                                        :examples {:source-paths ["src" "test"]
                                                   :figwheel {:websocket-host "nvbn-XPS13-9333.local"}
                                                   :compiler {:output-to "resources/public/compiled/main.js"
                                                              :output-dir "resources/public/compiled"
                                                              :asset-path "/compiled"
                                                              :source-map true
                                                              :optimizations :none
                                                              :pretty-print false}}}
                               :test-commands {"test" ["phantomjs"
                                                       "resources/test/test.js"
                                                       "resources/test/test.html"]}}}})
