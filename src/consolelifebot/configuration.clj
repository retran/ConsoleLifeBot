(ns consolelifebot.configuration
  (:require [aero.core :refer (read-config)]))

(def config
  (read-config "consolelifebot.edn"))

(def telegram-bot-api-token
  (:telegram-bot-api-token config))

(def telegram-bot-name
  (:telegram-bot-name config))

