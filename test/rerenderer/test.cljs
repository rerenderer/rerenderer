(ns ^:figwheel-always rerenderer.test
  (:require [cljs.test :refer-macros [run-all-tests] :as test]
            [devtools.core :as devtools]
            ; Fixes stange and infrequent errors like:
            ; Uncaught TypeError: Cannot read property 'forms_test' of undefined
            rerenderer.debug-test
            rerenderer.lang.core-test
            rerenderer.lang.forms-test
            rerenderer.lang.gc-test
            rerenderer.lang.utils-test
            rerenderer.types.component-test
            rerenderer.types.node-test
            rerenderer.types.render-result-test
            rerenderer.render-test
            rerenderer.platform.browser.interpreter-test
            rerenderer.platform.browser.events-test
            rerenderer.platform.browser.core-test
            rerenderer.platform.android.bus-test
            rerenderer.platform.android.events-test
            rerenderer.platform.android.core-test
            rerenderer.platform.utils-test))

(enable-console-print!)
(devtools/install!)

(defn print-comparison
  [{:keys [expected actual]}]
  (.log js/console "expected:" expected)
  (.log js/console "  actual:" actual))

(with-redefs [test/print-comparison print-comparison]
  (run-all-tests #"rerenderer\..*-test"))
