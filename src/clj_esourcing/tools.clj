(ns clj-esourcing.tools
  (:gen-class))

(defn parse-int [x]
  (Integer. (re-find #"[0-9]*" x)))

(defn get-input [str]
  (println str)
  (read-line))
