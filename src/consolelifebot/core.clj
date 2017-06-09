(ns consolelifebot.core
  (:require [clojure.string :as s]
            [consolelifebot.configuration :as configuration]
            [consolelifebot.feed :as feed]
            [consolelifebot.messages :as messages]
            [consolelifebot.telegram :as telegram]
            [consolelifebot.tags :as tags]
            [org.httpkit.timer :as timer]
            [consolelifebot.users :as users])
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
      

      
      (let [user (:from message)]
        (if (:new_chat_members message)
          (do
            (reply messages/welcome)
            (when-not (users/exist? user)
              (do
                (users/register user)
                (timer/schedule-task 180000
                                     (when-not (users/is-activated user)
                                       (do
                                         (reply "Вас приветствует охранный робот. Пожалуйста, представьтесь или вы будете УНИЧТОЖЕНЫ!")
                                         (timer/schedule-task 300000
                                                              (when-not (users/is-activated user)
                                                                (do
                                                                  (reply "УНИЧТОЖИТЬ! УНИЧТОЖИТЬ! УНИЧТОЖИТЬ!")
                                                                  @(telegram/kick-user :with-id (:id user)
                                                                                       :at chat-id)
                                                                  @(telegram/unban-user :with-id (:id user)
                                                                                        :at chat-id))))))))))
          (when-not (users/is-activated user)
            (do
              (users/activate user)
              (reply "Посетитель авторизован. Приятного посещения.")))))

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

(users/get-all)

