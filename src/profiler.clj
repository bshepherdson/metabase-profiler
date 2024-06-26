(ns profiler
  (:require
   [clj-async-profiler.core :as prof]))

(defn run-profile [& {:keys [pid port duration] :as opts}]
  (let [duration (or duration 5000)
        cfg      (dissoc opts :duration)]
    (prn cfg)
    (println (str "Starting " duration "ms profile of " pid " ..."))
    (prof/start cfg)
    (Thread/sleep duration)
    (prof/stop cfg)
    (println (str "Profile complete. Serving UI on " port
                  " and waiting for you to quit."))
    (prof/serve-ui port)
    (loop []
      (Thread/sleep 500)
      (recur))))
