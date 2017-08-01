(ns clj-esourcing.core
  (:gen-class)
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

;; Mongo config
(def conn (mg/connect))
(def db   (mg/get-db conn "logs"))

;; Cols
(def log-coll "logs")

(defn parse-int [x]
  (Integer. (re-find #"[0-9]*" x)))

(defn get-input []
  (println "Choose an option: ")
    (read-line))


(defn add-log [payload]
  "Payload -> keyword"
  (mc/insert db "logs" {:msg (get payload :msg)
                        :type (get payload :type)
                        :created_at (new java.util.Date)})
  (println "ADD"))

(defn intro [val]
  (cond
    (= (parse-int val) 1) (add-log {:msg "Add a log" :type "ADD_LOG"})
    :else (println "Bye!")))

(defn -main
  "docstring."
  [& args]
  (println "Init clojure event soucing demo")
  (intro (get-input)))
