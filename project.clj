(defproject rerenderer "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-3211"]
                           [org.clojure/core.match "0.3.0-alpha4"]
                           [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
            :plugins [[lein-cljsbuild "1.0.5"]
                      [lein-figwheel "0.3.3"]]
            :cljsbuild {:builds {:test {:source-paths ["test" "src"]
                                        :compiler {:output-to "target/cljs-test.js"
                                                   :optimizations :whitespace
                                                   :pretty-print true}}
                                 :examples {:source-paths ["src" "examples"]
                                        :figwheel {:websocket-host "192.168.0.107"
                                                   :on-jsload "rerenderer.examples.core/reload"}
                                        :compiler {:output-to "resources/public/compiled/examples.js"
                                                   :output-dir "resources/public/compiled"
                                                   :asset-path "/compiled"
                                                   :source-map true
                                                   :main "rerenderer.examples.core"
                                                   :optimizations :none
                                                   :pretty-print false}}}
                        :test-commands {"test" ["phantomjs"
                                                "resources/test/test.js"
                                                "resources/test/test.html"]}})
