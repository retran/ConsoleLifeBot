(ns consolelifebot.core
  (:require [consolelifebot.telegram :as telegram]
            [consolelifebot.messages :as messages]
            [consolelifebot.configuration :as configuration]
            [consolelifebot.feed :as feed]
            [clojure.string :as s])
  (:gen-class))

(defn dispatch [message]
  (let [text (s/trim (:text message))]
    (letfn [(pattern [command]
              (re-pattern (str "/" command "(@" configuration/telegram-bot-name ")?")))
            (matches [command]
                     (re-matches (pattern command) text))
            (reply [response]
                   (telegram/reply-with-message
                    :to (:message_id message)
                    :with-text response
                    :at (-> message :chat :id)))]

      (when (:new_chat_member message)
        (reply messages/welcome))

      (doseq [[command response]
              [["about" messages/about]
               ["rules" messages/rules]
               ["list" messages/console-list]]]
        (when (matches command)
          (reply response))))))

(defn -main
  [& args]
  (feed/schedule-feeds)
  @(telegram/for-each-new-message dispatch))

