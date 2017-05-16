(defproject consolelifebot "0.1.0-SNAPSHOT"
  :description "Console Life Community Bot"
  :url "https://github.com/retran/consolelifebot"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot consolelifebot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
