(ns om-mutation.mutations
  (:require [om.next :as om]
            [om.util :as om-util]
            ))

;; ===================== Mutate helpers =========================

(defn increment-counter [counter] (update counter :counter/n inc))
(defn increment-sibling [sibling] (update sibling :sibling/value inc))

;; ====================== Mutate multimethods ====================

(defmulti mutate om/dispatch)

(defmethod mutate :default
  [_ k _]
  (println "Default mutate " k)
  {:remote false})


(defmethod mutate 'counter/inc [{:keys [state] :as env} k {:keys [id] :as params}]
  {:action (fn [] (swap! state update-in [:counter/by-id id] increment-counter))})

(defmethod mutate 'add-counters-to-panel [{:keys [state] :as env} k {:keys [id] :as params}]
  {:action (fn []
             (let [ident-list (get @state :all-counters)]
               (swap! state update-in [:panels/by-kw :counter] assoc :counters ident-list)))})


(defmethod mutate 'child1/inc [{:keys [state] :as env} k {:keys [id] :as params}]
  {:action (fn []
             (println "child1/inc::action()")
             (swap! state update-in [:parent1 :parent1/child1 :child1/value] inc))})

(defmethod mutate 'child1-child2/inc [{:keys [state] :as env} k {:keys [id] :as params}]
  {:action (fn []
             (println "child1-child2/inc::action()")
             (swap! state update-in [:parent1 :parent1/child1 :child1/value] inc)
             (swap! state update-in [:parent1 :parent1/child2 :child2/value] inc))})


(defmethod mutate 'child1-child2-child3/inc [{:keys [state] :as env} k {:keys [id] :as params}]
  {:action (fn []
             (println "child1-child2-child3/inc::action()")
             (swap! state update-in [:parent1 :parent1/child1 :child1/value] inc)
             (swap! state update-in [:parent1 :parent1/child2 :child2/value] inc)
             (swap! state update-in [:parent1 :parent1/child3 :child3/value] inc))})

(defmethod mutate 'child4-sibling-Chris/inc [{:keys [state] :as env} k {:keys [id] :as params}]
  {:action (fn []
             (println "child4-sibling-Chris::action()")
             (swap! state update-in [:parent1 :parent1/child4 :child4/value] inc)
             (swap! state update-in [:sibling/by-name "Chris"] increment-sibling))})

(defmethod mutate 'sibling/inc [{:keys [state] :as env} k {:keys [name] :as params}]
  {:action (fn []
             (println "sibling/inc::action() enter, name: " name)
             (swap! state update-in [:sibling/by-name name] increment-sibling))})
