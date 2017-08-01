(ns clj-esourcing.core
  (:gen-class)
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

(def conn (mg/connect))
(def db   (mg/get-db conn "logs"))
(def disc (mg/disconnect))

(defn -main
  "docstring."
  [& args]
  (println "Init clj-esoucing-demo"))
