(ns clj-esourcing.core
  (:gen-class)
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

;; Mongo config
(def conn (mg/connect))
(def db   (mg/get-db conn "test"))

;; Cols
(def log-coll "logs")
(def product-coll "products")

(defn parse-int [x]
  (Integer. (re-find #"[0-9]*" x)))

(defn get-input [str]
  (println str)
  (read-line))

(defn add-log [payload]
  "Payload -> keyword"
  (mc/insert db log-coll {:msg (get payload :msg)
                          :type (get payload :type)
                          :product (get payload :product)
                          :created_at (new java.util.Date)}))

(defn add-product [payload]
  "payload -> keyword"
  (get (mc/insert-and-return db product-coll {:product-desc (get payload :desc)
                                              :product-type (clojure.string/upper-case (get payload :type))
                                              :created_at   (new java.util.Date)}) :_id))

(defn get-product-info []
  (add-log {:msg "Product ae add successfully."
            :type "PRODUCT_ADD"
            :product (add-product {:desc (get-input "Type the product description: ")
                                   :type (get-input "Type the product type: ")})}))

(defn get-log [n]
  (reverse (take n (reverse (mc/find-maps db log-coll)))))

(defn get-log-info [n]
  (doall (map println (map #(% :msg)(get-log (parse-int n))))))

(defn menu []
  (println "")
  (println "Clojure Event Sourcing Demo. Please, choose an option:")
  (println "1- Add Product")
  (println "2- Products options")
  (println "3- Log")
  (println "4- Exit"))

(defn get-input-menu []
  (menu)
  (read-line))

(defn intro [val]
  (println "--------------------")
  (cond
    (= (parse-int val) 1)(get-product-info)
    (= (parse-int val) 2)(println "2")
    (= (parse-int val) 3)(get-log-info (get-input "Type a log range: ")))
  (cond
    (= (parse-int val) 4)(println "Bye!")
    :else (intro (get-input-menu))))

(defn -main
  "docstring."
  [& args]
  (println "Init clojure event soucing demo")
  (intro (get-input-menu)))
