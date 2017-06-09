(ns consolelifebot.core
  (:require [clojure.string :as s]
            [consolelifebot.configuration :as configuration]
            [consolelifebot.feed :as feed]
            [consolelifebot.messages :as messages]
            [consolelifebot.telegram :as telegram]
            [consolelifebot.tags :as tags]
            [org.httpkit.timer :as timer])
  (:gen-class))

(defn dispatch [message]
  (let [text (if (:text message)
               (s/trim (:text message))
               "")
        chat-id (-> message :chat :id)]
    (letfn [(pattern [command]
              (re-pattern (str "/" command "(@" configuration/telegram-bot-name ")?")))
            (matches [command]
                     (re-matches (pattern command) text))
            (reply [response]
                   (telegram/reply-with-message
                    :to (:message_id message)
                    :with-text response
                    :at chat-id))
            (reply-and-remove [response]
              (let [reply-message @(reply response)]
                (timer/schedule-task 60000 (do
                                             (telegram/delete-message :with-id (:message_id reply-message)
                                                                      :at chat-id)
                                             (telegram/delete-message :with-id (:message_id message)
                                                                      :at chat-id)))))]
      
      (when (:new_chat_members message)
        (reply messages/welcome))

      (tags/save-from text)

      (when (matches "tags")
        (let [tags (tags/get-all)]
          (when (seq tags)
            (reply-and-remove (s/join " " tags)))))
                  
      (doseq [[command response]
              [["about" messages/about]
               ["rules" messages/rules]
               ["list" messages/console-list]]]
        (when (matches command)
          (reply-and-remove response))))))

(defn -main
  [& args]
  (feed/schedule-feeds)
  @(telegram/for-each-new-message dispatch))

