(ns ergodox-tent.core
  (:use [scad-clj.scad])
  (:use [scad-clj.model]))

;; Note that I did this using the values from lister's case design.
;; I got all the numbers from the SVG, which represents all values
;; in pixels, with 90 pixels per inch.  At the end, I scale everything
;; to be in mm.

(def rect-depth 441.894)
(def rect-width 522.174)
(def rect-height 100)

(def curve-radius 25.803)

(def total-width (+ rect-width (* 2 curve-radius)))
(def total-depth (+ rect-depth (* 2 curve-radius)))

(def slope (/ Math/PI 12))

(def screw-radius (/ 11.63 2))
(def nut-radius 11.63)

(def nut-hole
  (cylinder nut-radius (* 5 rect-height)))

(def main-base
  (difference
   (translate [0 0 (/ rect-height 2)]
              (union
               (cube total-width rect-depth rect-height)
               (cube rect-width total-depth rect-height)
               (translate [(/ rect-width 2) (/ rect-depth 2) 0] (cylinder curve-radius rect-height))
               (translate [(/ rect-width -2) (/ rect-depth 2) 0] (cylinder curve-radius rect-height))
               (translate [(/ rect-width -2) (/ rect-depth -2) 0] (cylinder curve-radius rect-height))
               (translate [(/ rect-width 2) (/ rect-depth -2) 0] (cylinder curve-radius rect-height))))
   (->> nut-hole
        (translate [(+ (/ rect-width 2) 4)
                    (+ (/ rect-depth 2) 4)
                    0]))
   (->> nut-hole
        (translate [(+ (/ rect-width 2) 4 -189.508)
                    (+ (/ rect-depth 2) 4 6.45)
                    0]))
   (->> nut-hole
        (translate [(+ (/ rect-width 2) 4 -241.465)
                    (+ (/ rect-depth 2) 4 -452.55)
                    0]))))


(def shift-down-height
  (let [x (* (/ total-width 2) (Math/tan slope))]
    (* (- rect-height x)
       (Math/cos slope))))

(def flush-top
  (->> (union main-base (translate [0 0 -100] main-base))
       (rotate (- slope) [0 1 0])
       (translate [0 0 (- shift-down-height)])))

(def tent
  (scale [(/ 25.4 90) (/ 25.4 90) (/ 25.4 90)]
         (difference
          flush-top
          (translate [0 0 -250] (cube 700 700 500))
          (translate [-375 0 -0] (cube 700 700 500))
          (translate [0 0 100] (cube (* 0.9 rect-width) (* 0.95 rect-depth) 300)))))

(spit "resources/tent.scad"
      (write-scad
       (union
        tent
        (->> tent
             (mirror [1 0 0])
             (translate [50 (- 20) 0])))))
