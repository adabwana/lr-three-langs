(ns languages.clojure
  (:require
   [utils.clojure-helpers :refer :all]
   [fastmath.ml.regression :as reg]
   [scicloj.hanamicloth.v1.api :as haclo]
   ;; [scicloj.ml.smile.regression]           ;; registers smile regression models
   ;; [scicloj.ml.tribuo]                      ;; registers all tribuo models
   ;; [scicloj.sklearn-clj :as sklearn-clj]
   ;; [scicloj.sklearn-clj.ml]                 ;; registers all scikit-learn models
   ;; [tech.v3.dataset.modelling :as ds-mod]
   [tablecloth.api :as tc]))

;; ## Load data
(defonce autompg (-> "data/autompg.csv"
                     (tc/dataset {:key-fn (fn [colname]
                                            (-> colname        ;kabab-case keyword
                                                (clojure.string/replace #"\.|\s" "-")
                                                clojure.string/lower-case
                                                keyword))})))
;; ### Glimpse at the data
(tc/head autompg)
(tc/info autompg)

;; ## Plot data
(-> autompg
    (haclo/layer-point {:=x :hp :=y :mpg
                        :=title "MPG vs Horsepower"
                        :=x-title "Horsepower"
                        :=y-title "Miles per Gallon"}))

;; ## Fit simple linear regression model
(def slr
  (reg/lm (:mpg autompg) (tc/rows (tc/select-columns autompg [:hp]))))

(lm-summary slr)

;; ## Predict
(reg/predict slr 210)

;; ### View prediction on plot
(let [hp-range (range (apply min (:hp autompg)) (apply max (:hp autompg)))
      predicted-mpg (map #(reg/predict slr %) hp-range)
      model-line (tc/dataset {:hp hp-range :mpg predicted-mpg})
      predicted-point (tc/dataset {:hp 210 :mpg (reg/predict slr 210)})]
  (-> autompg
      (haclo/layer-point {:=x :hp :=y :mpg :=mark-color :black
                          :=title "MPG vs Horsepower"
                          :=x-title "Horsepower"
                          :=y-title "Miles per Gallon"})
      (haclo/update-data (fn [_] model-line))
      (haclo/layer-line {:=x :hp :=y :mpg
                         :=mark-color :blue})
      (haclo/update-data (fn [_] predicted-point))
      (haclo/layer-point {:=x :hp :=y :mpg
                          :=mark-size 100
                          :=mark-color :red})))

;; ## Fit polynomial regression models
(let [poly-2-ds (add-poly-features autompg 2)
      selected-poly-ds (tc/select-columns poly-2-ds [:hp :hp_2])
      y (:mpg autompg)]
  (def poly-2-lr (reg/lm y (tc/rows selected-poly-ds))))

(let [poly-3-ds (add-poly-features autompg 3)
      selected-poly-ds (tc/select-columns poly-3-ds [:hp :hp_2 :hp_3])
      y (:mpg autompg)]
  (def poly-3-lr (reg/lm y (tc/rows selected-poly-ds))))

^:kindly/code
(lm-summary poly-2-lr)
(lm-summary poly-3-lr)

(comment
  (generate-predictions slr 1
                        (range 0 300 10)))

;; ## Plot the three models
(let [min-hp (apply min (:hp autompg))
      max-hp (apply max (:hp autompg))
      step (/ (- max-hp min-hp) 99)
      hp-range (range min-hp (+ max-hp step) step)
      pred-slr (generate-predictions slr 1 hp-range)
      pred-poly-2 (generate-predictions poly-2-lr 2 hp-range)
      pred-poly-3 (generate-predictions poly-3-lr 3 hp-range)
      plot-data (-> (tc/dataset {:hp (vec (apply concat (repeat 3 hp-range)))
                                 :mpg (vec (concat pred-slr pred-poly-2 pred-poly-3))
                                 :model (vec (concat (repeat 100 "Linear")
                                                     (repeat 100 "Degree 2")
                                                     (repeat 100 "Degree 3")))})
                    (tc/convert-types {:hp :float64}))]
  (-> autompg
      (haclo/layer-point {:=x :hp :=y :mpg :=mark-color :black
                          :=title "MPG vs Horsepower with Different Polynomial Fits"
                          :=x-title "Horsepower"
                          :=y-title "Miles per Gallon"})
      (haclo/update-data (fn [_] plot-data))
      (haclo/layer-line {:=x :hp :=y :mpg
                         :=color :model
                         :=stroke-width 2})))

;; ## Model diagnostics: SLR
;; ### 1. Check for linearity and independence
(check-linearity-independence slr)

;; ### 2. Check for normality of residuals
(check-normality-qq slr)

(let [residuals (get-in slr [:residuals :raw])
      df (tc/dataset {:residuals residuals})]
  (-> df
      (haclo/layer-histogram {:=x :residuals
                              :=title "Residuals"
                              :=x-title "Residuals"
                              :=y-title "Frequency"})))

;; ### 3. Check for homoscedasticity
(check-homoscedasticity slr)

;; ### 4. Check for independence
(check-independence slr autompg :hp)

;; ### EXTRA:Check for observed vs predicted
(check-observed-vs-predicted slr autompg :mpg)


;; ## Model diagnostics: Polinomial Degree 2
;; ### 1. Check for linearity and independence
(check-linearity-independence poly-2-lr)

;; ### 2. Check for normality
(check-normality-qq poly-2-lr)

(let [residuals (get-in poly-2-lr [:residuals :raw])
      df (tc/dataset {:residuals residuals})]
  (-> df
      (haclo/layer-histogram {:=x :residuals
                              :=title "Residuals"
                              :=x-title "Residuals"
                              :=y-title "Frequency"})))

;; ### 3. Check for homoscedasticity
(check-homoscedasticity poly-2-lr)

;; ### 4. Check for independence
(check-independence poly-2-lr autompg :hp)

;; ### EXTRA: Check for observed vs predicted
(check-observed-vs-predicted poly-2-lr autompg :mpg)


^:kindly/hide-code
(comment
  (def train-ds
    (-> (tc/dataset {:x1 [1 1 2 2]
                     :x2 [1 2 2 3]
                     :y  [6 8 9 11]})
        (ds-mod/set-inference-target :y)))

  (def test-ds
    (->
     (tc/dataset {:x1 [3]
                  :x2 [5]
                  :y  [0]})
     (ds-mod/set-inference-target :y)))

  (def lin-reg
    (sklearn-clj/fit train-ds :sklearn.neighbors :k-neighbors-classifier))

  ;; Call predict with new data on the estimator
  (sklearn-clj/predict test-ds lin-reg [:y]))

