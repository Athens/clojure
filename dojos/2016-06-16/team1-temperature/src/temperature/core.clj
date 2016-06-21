(ns temperature.core
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))

(defn get-coords [city-name]
  (let [url (str "https://maps.googleapis.com/maps/api/geocode/json?address=" city-name "&region=gr")
        resp @(http/get url)]
    (if (= (:status resp) 200)
      (some-> (json/parse-string (:body resp) true)
              :results first :geometry :location)
      (throw (ex-info "wrong responce code" {:status (:status resp)})))))

;;https://api.forecast.io/forecast/ceb2d5905eb7af7ccb8966f0f77ce3d0/37.9229212,23.7478653?units=si

(defn get-temp [{:keys [lat lng]}]
  (let [url (str "https://api.forecast.io/forecast/ceb2d5905eb7af7ccb8966f0f77ce3d0/" lat "," lng "?units=si")
        resp @(http/get url)]
    (some-> (json/parse-string (:body resp) true)
            :currently :temperature)
    (throw (ex-info "wrong responce code" {:status (:status resp)}))))

(def cities ["Larissa" "Volos" "Patra" "Athina" "Chania" "Serres" "Chios"])


(def temp-map (zipmap cities (pmap get-temp (pmap get-coords cities))))
