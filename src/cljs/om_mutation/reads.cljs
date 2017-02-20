(ns om-mutation.reads
  (:require [om.next :as om]
            [om.util :as om-util]
            ))


(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [query target state ast]} dkey _]
  ;;(println "read(): dkey: " dkey " query: " query " target: " target)
  (when (not target)
    (case dkey
      ;; top-level-prop means we have key but no query
      (let [top-level-prop (nil? query)
            key (or (:key ast) dkey)

            ;; Check if the key is actually an ident (using om.util/ident?)
            by-ident? (om-util/ident? key)

            ;; If the query is a map, then by definition it is a union
            union? (map? query)

            ;; Use the key for our initial fetch - either get-in for an ident, or direct key otherwise.
            data (if by-ident? (get-in @state key) (get @state key))]

        ;;(println " read(): case: key: " key " union?:" union? " top-level-prop: " top-level-prop)
        {:value
         (cond
           ;; For a union we reform the initial join query and act on the entire DB.
           union? (get (om/db->tree [{key query}] @state @state) key)

           ;; For top level, we already have what we need
           top-level-prop data

           ;; We have non-union query, use it to pull the required response out of the keyed data
           :else (let [response (om/db->tree query data @state)]
                   ;;(println " read(): else: response: " response "\n data:" data)
                   response))}))))
