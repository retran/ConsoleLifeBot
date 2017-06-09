(ns consolelifebot.tags
  (:require [consolelifebot.database :as database]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]))

(def collection "tags")

(defn- save-one [tag]
  (mc/insert database/db collection {:tag tag}))

(defn- exist? [tag]
  (seq (mc/find-maps database/db collection {:tag tag})))

(defn get-all []
  (sort (map :tag 
             (mc/find-maps database/db collection))))

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
