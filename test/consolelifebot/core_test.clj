(ns consolelifebot.core-test
  (:require [clojure.test :refer :all]
            [consolelifebot.core :refer :all]
            [consolelifebot.telegram :refer :all]
            [consolelifebot.configuration :refer :all]
            [consolelifebot.feed :refer :all]
            [consolelifebot.messages :refer :all]
            [consolelifebot.tags :refer :all]))

(deftest sanity-check-test
  (testing "sanity check"
    (is true)))

