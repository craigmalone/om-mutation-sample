(ns om-mutation.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [om-mutation.core-test]))

(enable-console-print!)

(doo-tests 'om-mutation.core-test)
