(ns consolelifebot.feed
  (:require [consolelifebot.configuration :as configuration]
            [consolelifebot.telegram :as telegram]
            [org.httpkit.client :as http]
            [org.httpkit.timer :as timer]
            [clojure.xml :as xml]
            [clj-time.core :as t]
            [clj-time.format :as f]))

(defn get-xml [url]
  (xml/parse (java.io.ByteArrayInputStream.
                         (.getBytes (:body @(http/get url))))))

(defn node [tag item]
  (some #(when (= tag (:tag %)) %)
               item))

(defn value [tag item]
  (first (:content (node tag item))))
             
(defn- rss [url]
  (let [feed (get-xml url)
        channel (-> feed :content first :content)
        title (value :title channel)
        items (map :content (filter #(= :item (:tag %)) channel))]
    (map (fn [item]
           {:channel title
            :title (value :title item)
            :url (value :link item)
            :date (f/parse (f/formatters :rfc822)
                           (value :pubDate item))})
         items)))

(defn- youtube [url]
  (let [feed (get-xml url)
        channel (-> feed :content)
        title (value :title channel)
        items (map :content (filter #(= :entry (:tag %)) channel))]
    (map (fn [item]
           {:channel title
            :title (value :title item)
            :url (:href (:attrs (node :link item)))
            :date (f/parse (f/formatters :date-time-no-ms)
                           (value :published item))})
         items)))

(defn- post-from [get-feed channel-url after]
  (letfn [(filter-by-date [items date]
            (filter #(t/after? (:date %) date) items))
          (format-item [item]
                       (format "<a href=\"%s\">%s</a>"
                               (:url item)
                               (:title item)))]
    (let [items (sort #(compare (:date %1) (:date %2))
                      (get-feed channel-url))]
      (when (seq items)
        (doseq [item (filter-by-date items after)]
          @(telegram/post-message :with-text (format-item item)
                                  :at configuration/telegram-notes-channel)))
      (:date (last items)))))

(defn- schedule [get-feed channel-url after]
  (let [next-after (post-from get-feed channel-url after)]
    (timer/schedule-task configuration/feed-polling-interval
                         (schedule get-feed channel-url next-after))))

(defn schedule-feeds []
  (doseq [channel configuration/rss-channels]
    (schedule rss channel (t/now)))
  (doseq [channel configuration/youtube-channels]
    (schedule youtube channel (t/now))))

