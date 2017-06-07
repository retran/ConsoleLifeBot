(ns consolelifebot.telegram
  (:require [consolelifebot.configuration :as configuration]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]))

(def base-url (str "https://api.telegram.org/bot"
                   configuration/telegram-bot-api-token))

(defn- get-updates [offset]
  (http/get (str base-url "/getUpdates")
            {:query-params {:offset offset
                            :limit 100
                            :timeout 60}
             :keep-alive 60000}
            (fn [{:keys [status headers body error]}]
              (if (not= status 200)
                (do
                  (log/error body)
                  (Thread/sleep 500)
                  [])
                (:result (json/read-str body :key-fn keyword))))))

(defn- post [method body]
  (log/info "[telegram] post " method " " body) 
  (http/post (str base-url method)
             {:headers {"Content-Type" "application/json"}
              :body (json/write-str body)}
            (fn [{:keys [status headers body error]}]
              (if (not= status 200)
                (do
                  (log/error body) nil)
                (:result (json/read-str body :key-fn keyword))))))

(defn post-message [& {with-text :with-text at :at disable-preview :disable-preview}]
  (post "/sendMessage" {:chat_id at
                        :text with-text
                        :parse_mode "HTML"
                        :disable_web_page_preview (or disable-preview false)}))

(defn reply-with-message [& {with-text :with-text to :to at :at}]
  (post "/sendMessage" {:chat_id at
                        :reply_to_message_id to
                        :text with-text
                        :parse_mode "HTML"
                        :disable_web_page_preview true}))

(defn delete-message [& {with-id :with-id at :at}]
  (post "/deleteMessage" {:chat_id at
                          :message_id with-id}))

(defn for-each-new-message [handle]
  (future
    (log/info "[telegram] start")
    (loop [offset 0]
      (let [updates @(get-updates offset)
            next-offset (if (seq updates)
                          (-> updates last :update_id inc)
                          0)]
        (when (seq updates)
          (doseq [message (map :message updates)]
            (log/info "[telegram] receive " message)
            (future
                (handle message))))
        (when (not (Thread/interrupted))
          (recur next-offset))))))

