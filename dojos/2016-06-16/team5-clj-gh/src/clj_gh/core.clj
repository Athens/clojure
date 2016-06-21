(ns clj_gh.gh
  (:require [cheshire.core :as json]))

(defn get-projects [name]
  (str "https://raw.githubusercontent.com/"
       name
       "/master/project.clj"))

(defn retrieve-clj-projects []
  (let [uri "https://api.github.com/search/repositories?q=language:clojure"]
    (-> (slurp uri)
        (json/parse-string true)
        :items)))

(defn project-urls []
  (map #(get-projects (:full_name %)) (retrieve-clj-projects)))

(defn get-dependencies [uri]
  (let [a (slurp uri)]
    (map #(-> % first str) (nth (read-string a) 8))))
