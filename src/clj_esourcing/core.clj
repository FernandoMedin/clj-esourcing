(ns clj-esourcing.core
  (:gen-class)
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

;; Mongo config
(def conn (mg/connect))
(def db   (mg/get-db conn "esourcing"))

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
  (add-log {:msg "Product add successfully."
            :type "PRODUCT_ADD"
            :product (add-product {:desc (get-input "Type the product description: ")
                                   :type (get-input "Type the product type: ")})}))

(defn get-product [filter]
  (mc/find-maps db product-coll filter))

(defn get-all-products []
  (doall (map println (map (fn [x] (str (get x :product-desc) " - " (get x :product-type)))
                           (get-product {})))))

(defn get-product-log [filter]
  (mc/find-maps db log-coll filter))

(defn get-filter-products [product-status]
  (filter (fn [x] (= product-status (get x :status)))
          (for [x (get-product {})]
            (assoc x :status (get (first (take 1 (reverse (get-product-log {:product (get x :_id)}))))
                                  :type)))))

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

(defn products-menu []
  (println "")
  (println "Products Menu. Please, choose an option:")
  (println "1- See all products")
  (println "2- See active products")
  (println "3- See deleted products")
  (println "4- Delete product")
  (println "5- Exit products menu"))

(defn get-input-menu [x]
  (cond
    (= x 1)(menu)
    :else (products-menu))
  (read-line))

(defn print-product-list [product-status]
  (doall (map println (map (fn [x] (str (get x :product-desc) " - " (get x :product-type)))
                           (get-filter-products product-status)))))


(defn products-intro [val]
  (println "--------------------")
  (cond
    (= (parse-int val) 1)(get-all-products)
    (= (parse-int val) 2)(print-product-list "PRODUCT_ADD")
    (= (parse-int val) 3)(print-product-list "PRODUCT_DELETE")
    (= (parse-int val) 4)(println "4"))
  (cond
    (= (parse-int val) 5)(println "Returning to menu")
    :else (products-intro (get-input-menu 2))))

(defn intro [val]
  (println "--------------------")
  (cond
    (= (parse-int val) 1)(get-product-info)
    (= (parse-int val) 2)(products-intro (get-input-menu 2))
    (= (parse-int val) 3)(get-log-info (get-input "Type a log range: ")))
  (cond
    (= (parse-int val) 4)(println "Bye!")
    :else (intro (get-input-menu 1))))

(defn -main
  "docstring."
  [& args]
  (println "Init clojure event soucing demo")
  (intro (get-input-menu 1)))
