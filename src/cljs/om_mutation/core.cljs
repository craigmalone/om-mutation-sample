(ns om-mutation.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.util :as om-util]
            [om.dom :as dom]
            ))

(enable-console-print!)

;; ========================= State ======================================

(def init-data
  {
   :parent1 {:parent1/child1 {:child1/name "John" :child1/value 10}
             :parent1/child2 {:child2/name "Bill" :child2/value 20}}
   })

;; ========================== Parser functions =========================

(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [query target state ast]} dkey _]
  (when (not target)
    (case dkey
      (let [top-level-prop (nil? query)
            key (or (:key ast) dkey)
            data (get @state key)]
        {:value
         (cond
           top-level-prop data
           :else (om/db->tree query data @state))}))))

(defmulti mutate om/dispatch)

(defmethod mutate 'child1-child2/inc [{:keys [state] :as env} k {:keys [id] :as params}]
  {:action (fn []
             (swap! state update-in [:parent1 :parent1/child1 :child1/value] inc)
             (swap! state update-in [:parent1 :parent1/child2 :child2/value] inc))})

;; ======================== Child components ============================

(defui ^:once Child1
  static om/IQuery
  (query [this] [:child1/name :child1/value])

  Object
  (render [this]
          (println "Child1::render(): props: " (om/props this))
          (dom/div nil (str (om/props this)))))

(defui ^:once Child2
  static om/IQuery
  (query [this] [:child2/name :child2/value])

  Object
  (render [this]
          (println "Child2::render(): props: " (om/props this))
          (dom/div nil
                   (dom/span nil (str (om/props this)))
                   (dom/button
                    #js {:onClick (fn [] (om/transact! this ['(child1-child2/inc) :child1/name]))}
                    "Increment"))))

(def child1-component (om/factory Child1))
(def child2-component (om/factory Child2))

;; ================================== Root component ============================

(defui ^:once Root
  static om/IQuery
  (query [this] [{:parent1 [{:parent1/child1 (om/get-query Child1)}
                            {:parent1/child2 (om/get-query Child2)}]}])

  Object
  (render  [this]
   (println "Root::render(), keys: " (keys (om/props this)))
   (let [{{:keys [:parent1/child1 :parent1/child2]} :parent1} (om/props this)]
     (dom/div nil
              (dom/div nil (str  "Root: " (om/props this)))
              (child1-component child1)
              (child2-component child2)))))


(def reconciler
  (om/reconciler {:state  init-data :parser (om/parser {:read read :mutate mutate})}))

(om/add-root! reconciler Root (gdom/getElement "app"))
