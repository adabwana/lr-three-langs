(ns dev
  (:require [scicloj.clay.v2.api :as clay]))

(defn build []
  (clay/make!
   {:format              [:quarto :html]
    :book                {:title "Data Science: Comparing Languages"}
    :subdirs-to-sync     ["notebooks" "data"]
    :source-path         ["src/index.clj"
                          "src/languages/clojure.clj"]
    :base-target-path    "docs"
    :clean-up-target-dir true}))

(comment
  (build))
