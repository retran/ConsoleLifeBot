(ns consolelifebot.configuration
  (:require [aero.core :refer (read-config)]))

(def config
  (read-config "consolelifebot.edn"))

(def telegram-bot-api-token
  (:telegram-bot-api-token config))

(def telegram-bot-name
  (:telegram-bot-name config))

(def telegram-notes-channel
  (:telegram-notes-channel config))

(def feed-polling-interval
  (:feed-polling-interval config))

(def rss-channels
  (:rss-channels config))

(def youtube-channels
  (:youtube-channels config))

