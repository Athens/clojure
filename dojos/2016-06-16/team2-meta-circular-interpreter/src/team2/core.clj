(ns team2.core
  (:refer-clojure :exclude [eval])
  (:require [clojure.core.match :refer [match]]))


(defn extend [k v l]
  (fn [k']
    (if (= k k') v (l k'))))

(def empty (fn [k'] (throw (Exception. "oups"))))

(defn eval [e env]
  (match [e]
    [(['lambda ([param] :seq) body] :seq)]
      (fn [v] (eval body (extend param v env)))
    [([e1 e2] :seq)]
      ((eval e1 env) (eval e2 env))
    [x] (env x) ))

(comment
  (eval '((lambda (x) (x x)) (lambda (x) (x x))) empty))
