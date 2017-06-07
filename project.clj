(defproject consolelifebot "0.1.0-SNAPSHOT"
  :description "Console Life Community Bot"
  :url "https://github.com/retran/consolelifebot"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.logging "0.3.1"]
                 [clj-time "0.13.0"]
                 [aero "1.1.2"]
                 [com.novemberain/monger "3.1.0"]
                 [http-kit "2.2.0"]]
  :main ^:skip-aot consolelifebot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
