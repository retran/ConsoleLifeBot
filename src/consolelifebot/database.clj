(ns consolelifebot.database
  (:require [monger.core :as mg]))

(def connection (mg/connect))
(def db (mg/get-db connection "consolelifebot"))

