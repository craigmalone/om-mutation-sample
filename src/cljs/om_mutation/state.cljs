(ns om-mutation.state)

;; ============================= State ========================

(def init-data
  {
   :parent1 {:parent1/child1 {:child1/name "John"
                              :child1/value 10
                              :common/age 31}

             :parent1/child2 {:child2/name "Bill"
                              :child2/value 20
                              :common/age 33}

             :parent1/child3 {:child3/name "Tom"
                              :child3/value 30}

             :parent1/child4 {:child4/name "Mark"
                              :child4/value 40}

             :parent1/child5 {:child5/name "Toby"
                              :child5/value 50}}

   :parent2 [{:sibling/name "Chris" :sibling/value 10}
             {:sibling/name "Harry" :sibling/value 20}
             {:sibling/name "Neil" :sibling/value 30}
             ]
   }
  )
