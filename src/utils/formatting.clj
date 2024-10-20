(ns utils.formatting
  (:require
   [scicloj.kindly.v4.api :as kindly]
   [scicloj.kindly.v4.kind :as kind]))

^:kindly/hide-code
(kind/md "## Utils")

;; Formatting code
(def md (comp kindly/hide-code kind/md))
(def question (fn [content] ((comp kindly/hide-code kind/md) (str "## " content "\n---"))))
(def sub-question (fn [content] ((comp kindly/hide-code kind/md) (str "#### *" content "*"))))
(def sub-sub (fn [content] ((comp kindly/hide-code kind/md) (str "***" content "***"))))
(def formula (comp kindly/hide-code kind/tex))
(def answer 
  (fn [content] 
    (kind/md 
     (str "> <span style=\"color: black; font-size: 1.5em;\">**" content "**</span>"))))
