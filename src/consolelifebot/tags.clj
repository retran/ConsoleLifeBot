(ns consolelifebot.tags
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]))

(def connection (mg/connect))
(def db (mg/get-db connection "consolelifebot"))
(def collection "tags")

(defn- save-one [tag]
  (mc/insert db collection {:tag tag}))

(defn- exist? [tag]
  (seq (mc/find-maps db collection {:tag tag})))

(defn get-all []
  (sort (map :tag 
             (mc/find-maps db collection))))

(defn- save [tags]
  (doseq [tag tags]
    (when (not (exist? tag))
      (save-one tag))))

(defn- extract-tags-from [text]
  (let [pattern (re-pattern "#\\S+")]
  (re-seq pattern text)
  ))

(defn save-from [text]
  (-> (extract-tags-from text)
      save))
