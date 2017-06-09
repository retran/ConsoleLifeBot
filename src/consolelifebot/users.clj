(ns consolelifebot.users
  (:require [consolelifebot.database :as database]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]))

(def collection "users")

(defn get-all []
  (mc/find-maps database/db collection))

(defn exist? [user]
  (seq (mc/find-maps database/db collection {:id (:id user)})))

(defn register [user]
  (mc/insert-and-return database/db collection (assoc user
                                           :activated false)))

(defn activate [user]
  (mc/update-by-id database/db collection (:_id (first (exist? user)))
                   {$set {:activated true}}))

(defn is-activated [user]
  (let [u (exist? user)]
    (if u
      (:activated (first u))
      true)))

