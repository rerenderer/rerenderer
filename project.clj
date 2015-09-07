(defproject rerenderer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.28"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.cognitect/transit-cljs "0.8.220"]
                 [figwheel-sidecar "0.3.7"]
                 [figwheel "0.3.7"]
                 [binaryage/devtools "0.3.0"]
                 [enlive "1.1.6"]
                 [clj-http "2.0.0"]]
  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.7"]]
  :source-paths ["src" "script"]
  :cljsbuild {:builds {:test {:source-paths ["test" "src"]
                              :compiler {:output-to "target/cljs-test.js"
                                         :optimizations :whitespace
                                         :pretty-print true}}
                       :examples {:source-paths ["src" "examples"]
                                  :figwheel {:websocket-host "192.168.0.108"
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
