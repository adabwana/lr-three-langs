(ns utils.clojure-helpers
  (:require [fastmath.ml.regression :as reg]
            [fastmath.random :as random]
            [scicloj.hanamicloth.v1.api :as haclo]
            [tablecloth.api :as tc]
            [tech.v3.datatype.functional :as dtf]))

(defn lm-summary [model]
  (let [coeffs (:coefficients model)
        intercept (first coeffs)
        slopes (rest coeffs)
        residuals (:residuals model)
        raw-residuals (:raw residuals)
        min-residual (apply min raw-residuals)
        max-residual (apply max raw-residuals)
        q1-residual (nth (sort raw-residuals) (int (* 0.25 (count raw-residuals))))
        q3-residual (nth (sort raw-residuals) (int (* 0.75 (count raw-residuals))))
        median-residual (nth (sort raw-residuals) (int (* 0.5 (count raw-residuals))))]
    (println "Call:")
    (println "lm(formula = y ~ x1 + x2 + ... + xn)")
    (println)
    (println "Residuals:")
    (printf "     Min       1Q   Median       3Q      Max \n")
    (printf "%8.4f %8.4f %8.4f %8.4f %8.4f\n" 
            min-residual q1-residual median-residual q3-residual max-residual)
    (println)
    (println "Coefficients:")
    (printf "            Estimate Std. Error t value Pr(>|t|)\n")
    (printf "(Intercept) %8.6f %10.6f %7.2f %8.2e %s\n" 
            (:estimate intercept) 
            (:stderr intercept) 
            (:t-value intercept) 
            (:p-value intercept)
            (cond
              (< (:p-value intercept) 0.001) "***"
              (< (:p-value intercept) 0.01) "**"
              (< (:p-value intercept) 0.05) "*"
              (< (:p-value intercept) 0.1) "."
              :else ""))
    (doseq [[idx slope] (map-indexed vector slopes)]
      (printf "X_%d         %8.6f %10.6f %7.2f %8.2e %s\n" 
              idx
              (:estimate slope) 
              (:stderr slope) 
              (:t-value slope) 
              (:p-value slope)
              (cond
                (< (:p-value slope) 0.001) "***"
                (< (:p-value slope) 0.01) "**"
                (< (:p-value slope) 0.05) "*"
                (< (:p-value slope) 0.1) "."
                :else "")))
    (println "---")
    (println "Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1")
    (println)
    (printf "Residual standard error: %.3f on %d degrees of freedom\n" 
            (:sigma model) 
            (get-in model [:df :residual]))
    (printf "Multiple R-squared:  %.4f,    Adjusted R-squared:  %.4f\n" 
            (:r-squared model) 
            (:adjusted-r-squared model))
    (printf "F-statistic: %.1f on %d and %d DF,  p-value: < %.1e\n" 
            (:f-statistic model) 
            (get-in model [:df :model]) 
            (get-in model [:df :residual])
            (:p-value model))))

;; Create polynomial features
(defn add-poly-features [dataset degree]
  (reduce (fn [ds n]
            (tc/add-column ds (keyword (str "hp_" n)) (map #(Math/pow % n) (:hp ds))))
          dataset
          (range 2 (inc degree))))

;; Generate predictions for a polynomial model
(defn generate-predictions [model degree hp-range]
  (let [poly-data (add-poly-features (tc/dataset {:hp hp-range}) degree)
        feature-columns (map #(keyword (str "hp" (if (= % 1) "" (str "_" %)))) 
                             (range 1 (inc degree)))
        features (tc/select-columns poly-data feature-columns)]
    (map #(reg/predict model %) (tc/rows features))))


;; Linear Model Assumptions
;; 1. Check for independence of random error and linearity
(defn check-linearity-independence
  [model]
  (let [df (tc/dataset {:fitted (:fitted model)
                        :residuals (get-in model [:residuals :raw])})]
    (-> df
        (haclo/layer-point {:=x :fitted :=y :residuals
                            :=title "Check for Independence of Random Error and Linearity"
                            :=x-title "Fitted Values"
                            :=y-title "Residuals"})
        ;; (haclo/layer-smooth {:=x :fitted :=y :residuals
        ;;                      :=mark-color :gray :=stroke-dash [5 5]})
        (haclo/layer-line {:=x :fitted :=y 0
                           :=mark-color :red :=stroke-dash [5 5]}))))

(defn inv-normal
  "Calculates the inverse of the standard normal cumulative distribution function.
   p: probability (0 < p < 1)"
  [p]
  (random/icdf (random/distribution :normal) p))

;; 2. Check for normality of random error
(defn check-normality-qq
  [model]
  (let [residuals (get-in model [:residuals :raw])
        n (count residuals)
        sorted-residuals (sort residuals)
        sample-quantiles (map-indexed (fn [i x] [(/ (inc i) (inc n)) x]) sorted-residuals)
        theoretical-quantiles (map #(inv-normal (/ (inc %) (inc n))) (range n))
        qq-data (tc/dataset {:theoretical theoretical-quantiles
                             :sample (map second sample-quantiles)})

        ;; Calculate the first (25%) and third (75%) quartiles for both theoretical and sample
        q1-theoretical (nth theoretical-quantiles (int (* 0.25 n)))
        q3-theoretical (nth theoretical-quantiles (int (* 0.75 n)))
        q1-sample      (nth sorted-residuals (int (* 0.25 n)))
        q3-sample      (nth sorted-residuals (int (* 0.75 n)))

        ;; Calculate slope and intercept for the Q-Q line
        slope  (/ (- q3-sample q1-sample) (- q3-theoretical q1-theoretical))
        intercept (- q1-sample (* slope q1-theoretical))

        ;; Calculate extended range for the line
        min-theoretical (apply min theoretical-quantiles)
        max-theoretical (apply max theoretical-quantiles)
        range-theoretical (- max-theoretical min-theoretical)
        extended-min (- min-theoretical (* 0.1 range-theoretical))
        extended-max (+ max-theoretical (* 0.1 range-theoretical))

        ;; Define two points for the Q-Q line based on slope and intercept, with extended range
        line-data (tc/dataset {:x [min-theoretical max-theoretical]
                               :y [(+ (* slope min-theoretical) intercept)
                                   (+ (* slope max-theoretical) intercept)]})]
    (-> qq-data
        (haclo/layer-point {:=x :theoretical :=y :sample
                            :=title "Normal Q-Q Plot of Residuals"
                            :=x-title "Theoretical Quantiles"
                            :=y-title "Sample Quantiles"})
        (haclo/update-data (fn [_] line-data))
        (haclo/layer-line {:=x :x :=y :y :=mark-size 1
                           :=mark-color :red :=stroke-dash [5 5]}))))

;; 3. Check for zero mean and constant variance of random error
(defn check-homoscedasticity
  [model]
  (let [raw-residuals (get-in model [:residuals :raw])
        mean-resid (dtf/mean raw-residuals)
        std-resid (dtf/standard-deviation raw-residuals)
        standardized-residuals (dtf// (dtf/- raw-residuals mean-resid) std-resid)
        abs-std-resid (dtf/sqrt (dtf/abs standardized-residuals))
        df (-> (tc/dataset {:fitted (:fitted model)
                            :abs-std-resid abs-std-resid})
               (tc/add-column :mean-abs-std-resid (dtf/mean abs-std-resid)))]
    (-> df
        (haclo/layer-point {:=x :fitted :=y :abs-std-resid
                            :=title "Scale-Location"
                            :=x-title "Fitted Values"
                            :=y-title "sqrt(abs(Standardized Residuals))"})
        ;; (haclo/layer-smooth {:=x :fitted :=y :abs-std-resid
        ;;                      :=mark-color :gray :=stroke-dash [5 5]})
        (haclo/layer-line {:=x :fitted :=y :mean-abs-std-resid
                           :=mark-color :red :=stroke-dash [5 5]}))))

;; 4. Check for independence of random error
(defn check-independence
  [model data sort-var]
  (let [raw-residuals (get-in model [:residuals :raw])
      df (-> (tc/dataset {:residuals raw-residuals
                          :sort-var (sort-var data)})
               (tc/order-by :sort-var :desc)
               (tc/add-column :row-numbers (range (tc/row-count raw-residuals))))]
    (-> df
        (haclo/layer-point {:=x :row-numbers :=y :residuals
                            :=title (str "Check for Independence \n Residuals sorted by " (name sort-var))
                            :=x-title "Row Numbers"
                            :=y-title "Residuals"})
        ;; (haclo/layer-smooth {:=x :row-numbers :=y :residuals
        ;;                      :=mark-color :gray :=stroke-dash [5 5]})
        (haclo/layer-line {:=x :row-numbers :=y 0
                           :=mark-color :red :=stroke-dash [5 5]}))))

;; Additional diagnostic plot: Observed vs Predicted
(defn check-observed-vs-predicted
  [model data response]
  (let [df (tc/dataset {:fitted (:fitted model)
                        :actual (response data)})
        min-value (apply min (:fitted df))
        max-value (apply max (:fitted df))
        update-data (tc/dataset [{:x min-value :y min-value} 
                                 {:x max-value :y max-value}])]
    (-> df
        (haclo/layer-point {:=x :fitted :=y :actual
                            :=title "Observed vs Predicted Values"
                            :=x-title "Fitted Values"
                            :=y-title "Actual Values"})
        ;; (haclo/layer-smooth {:=x :fitted :=y :actual
        ;;                      :=mark-color :gray :=stroke-dash [5 5]})
        (haclo/update-data (fn [_] update-data))
        (haclo/layer-line {:=x :x :=y :y :=mark-size 1
                           :=mark-color :red :=stroke-dash [5 5]}))))

(comment
  ;; Residuals vs Leverage plot
  (defn check-residuals-vs-leverage
    [model]
      (let [df (tc/dataset {:leverage (:leverage model)
                            :std-resid (get-in model [:residuals :standardized])})]
        (-> df
            (haclo/layer-point {:=x :leverage :=y :std-resid
                                :=title "Residuals vs Leverage"
                                :=x-title "Leverage"
                                :=y-title "Standardized Residuals"})
            ;; (haclo/layer-smooth {:=x :leverage :=y :std-resid
            ;;                      :=mark-color :gray :=stroke-dash [5 5]})
            (haclo/layer-line {:=x :leverage :=y 0
                          :=mark-color :red :=stroke-dash [5 5]})))))
