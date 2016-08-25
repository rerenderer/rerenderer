(defproject org.rerenderer/rerenderer "0.4.3"
  :description "Simple platform agnostic react-like library for drawing on canvas, handling events and playing sounds."
  :url "https://github.com/rerenderer/rerenderer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.async "0.2.371"]
                 [cljsjs/tinycolor "1.3.0-0"]]
  :source-paths ["src" "script"]
  :jvm-opts ["-Xss32m"]
  :profiles {:dev {:dependencies [[binaryage/devtools "0.5.2"]]
                   :plugins [[lein-cljsbuild "1.1.2"]
                             [lein-figwheel "0.5.0-6"]
                             [lein-codox "0.9.4"]]
                   :codox {:language :clojurescript
                           :namespaces [rerenderer.core
                                        rerenderer.primitives
                                        rerenderer.debug
                                        rerenderer.platform.android.core
                                        rerenderer.platform.browser.core
                                        rerenderer.component]
                           :metadata {:doc/format :markdown}
                           :output-path "docs"}
                   :cljsbuild {:builds {:test {:source-paths ["test" "src"]
                                               :compiler {:output-to "target/cljs-test.js"
                                                          :optimizations :whitespace
                                                          :pretty-print true}}
                                        :live-test {:source-paths ["src" "test"]
                                                    :figwheel true
                                                    :compiler {:output-to "resources/public/compiled/main.js"
                                                               :output-dir "resources/public/compiled"
                                                               :asset-path "/compiled"
                                                               :main "rerenderer.test"
                                                               :source-map true
                                                               :optimizations :none
                                                               :pretty-print false}}}
                               :test-commands {"test" ["phantomjs"
                                                       "resources/test/test.js"
                                                       "resources/test/test.html"]}}}})
