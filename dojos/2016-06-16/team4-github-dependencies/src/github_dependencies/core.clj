(ns github-dependencies.core
  (:require  [clojure.string :as str]
             [clj-http.client :as http]))

(def github-search-url "https://api.github.com/search/repositories")

(def github-projects-data
  (:body
   (http/get github-search-url
             {:query-params {:q "language:clojure"
                             :sort "stars"
                             :order "desc"}
              :as :json})))

(defn get-project-urls [github-response]
  (map (fn [item]
         (let [html-url (:html_url item)
               replace-github (str/replace-first html-url
                                                 "github.com"
                                                 "raw.githubusercontent.com")]
           (str replace-github "/master/project.clj"))) (:items github-response)))

(defn get-dependencies [project-clj]
  (get
   (apply hash-map (drop 3 (read-string project-clj)))
   :dependencies))

(def all-dependencies
  (->> (get-project-urls github-projects-data)
       (map (fn [url]
              (try
                (slurp url)
                (catch Exception _ :no-project-clj))))
       (filter (fn [content]
                 (not= content :no-project-clj)))
       (mapcat get-dependencies)))

(take 10 (sort-by val > (frequencies (map first all-dependencies))))
;;=> ([org.clojure/clojure 16] [org.clojure/clojurescript 6] [org.clojure/tools.logging 3] [cheshire 3] [clj-http 3] [org.clojure/data.json 2] [com.draines/postal 2] [clj-time 2] [stencil 2] [cljsjs/react-dom 2])
