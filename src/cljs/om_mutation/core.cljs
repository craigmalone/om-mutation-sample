(ns om-mutation.core
  (:require [goog.dom :as gdom]
            [om.next :as om]
            [om.dom :as dom]
            [om-mutation.view :as view]
            [om-mutation.state :as state]
            [om-mutation.reads :as reads]
            [om-mutation.mutations :as mutations]
            ))

(enable-console-print!)


(def reconciler
  (om/reconciler
   {:state  state/init-data
    :parser (om/parser {:read reads/read :mutate mutations/mutate})}))

(om/add-root! reconciler
              view/Root (gdom/getElement "app"))
