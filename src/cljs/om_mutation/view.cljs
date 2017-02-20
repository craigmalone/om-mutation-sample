(ns om-mutation.view
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [om-mutation.reads :as reads]
            [cljs.pprint :as pp]
            [om-mutation.mutations :as mutations]
            ))

;; ============================== Styles =============================

(def child-style #js {:style #js {:marginLeft "40px" :marginTop "20px"}})
(def name-style #js {:style #js {:fontWeight "bold"}})
(def tx-style #js {:style #js {:marginLeft "10px" :fontSize "90%"}})
(def props-style #js {:style #js {:marginLeft "20px"}})

;; ================================= Utility functions ===============

(defn render-child
  [this name tx renders-desc]
  (println (str  name "::render(): props") (om/props this) " tx: " tx)
  (dom/div
   child-style
   (dom/span name-style  (str name ": "))
   (dom/span nil (str (om/props this)))
   (dom/div
    nil
    (dom/button #js {:onClick (fn [] (om/transact! this tx))} "Increment")
    (dom/span
     tx-style
     (str  "(Transaction: " tx ", Renders: " renders-desc ")")))))


;; ======================== Child components ============================

(defui ^:once Child1
  static om/IQuery
  (query
   [this]
   [:child1/name :child1/value :common/age])

  Object
  (render
   [this]
   (render-child this "Child1" ['(child1-child2/inc)] "Child1")))

(defui ^:once Child2
  static om/IQuery
  (query
   [this]
   [:child2/name :child2/value :common/age])

  Object
  (render
   [this]
   (render-child this "Child2" ['(child1-child2/inc) :child1/name] "Parent Child1 Child2")))

(defui ^:once Child3
  static om/IQuery
  (query
   [this]
   [:child3/name :child3/value])

  Object
  (render
   [this]
   (render-child this "Child3" ['(child1-child2-child3/inc) :child1/name] "Parent Child1 Child2 Child3")))

(defui ^:once Child3
  static om/IQuery
  (query
   [this]
   [:child3/name :child3/value])

  Object
  (render
   [this]
   (render-child this "Child3" ['(child1-child2-child3/inc) :child1/name] "Parent Child1 Child2 Child3")))

(defui ^:once Child4
  static om/IQuery
  (query
   [this]
   [:child4/name :child4/value])

  Object
  (render
   [this]
   (render-child this "Child4" ['(child4-sibling-Chris/inc) [:sibling/by-name "Chris"]] "Child4")))

(defui ^:once Child5
  static om/IQuery
  (query
   [this]
   [:child5/name :child5/value])

  Object
  (render
   [this]
   (render-child this "Child5" ['(child1-child2-child3/inc) :common/age :child3/name] "Parent Child1 Child2 Child3")))

(def child1-component (om/factory Child1))
(def child2-component (om/factory Child2))
(def child3-component (om/factory Child3))
(def child4-component (om/factory Child4))
(def child5-component (om/factory Child5))

;; ========================== Sibling component ================================

(defui ^:once Sibling
  static om/Ident
  (ident
   [this {:keys [:sibling/name] :as props}]
   [:sibling/by-name name])

  static om/IQuery
  (query
   [this]
   [:sibling/name :sibling/value])

  Object
  (render
   [this]
   (let [{:keys [:sibling/name]} (om/props this)
         sibling-prompt (str "Sibling-" name)]
     (render-child this "Sibling" [`(sibling/inc {:name ~name})] sibling-prompt))))

(def sibling-component (om/factory Sibling))

;; ================================== Root component ============================

(defui ^:once Root
  static om/IQuery
  (query
   [this]
   [{:parent1 [{:parent1/child1 (om/get-query Child1)}
               {:parent1/child2 (om/get-query Child2)}
               {:parent1/child3 (om/get-query Child3)}
               {:parent1/child4 (om/get-query Child4)}
               {:parent1/child5 (om/get-query Child5)}]}
    {:parent2 (om/get-query Sibling)}])
  Object
  (render
   [this]
   (println "Root::render(), keys: " (keys (om/props this)))
   (let [{:keys [:parent1 :parent2]} (om/props this)
         {:keys [:parent1/child1 :parent1/child2 :parent1/child3 :parent1/child4 :parent1/child5]} parent1
         harry (first (filter #(= "Harry" (:sibling/name %)) parent2))]
     (dom/div
      nil
      (dom/div name-style "Root: ")
      (dom/div props-style (str  ":parent1 " parent1))
      (dom/div props-style (str  ":parent2 " parent2))
      (child1-component child1)
      (child2-component child2)
      (child3-component child3)
      (child4-component child4)
      (child5-component child5)
      (map sibling-component parent2)
      (sibling-component harry)))))
