(ns create-project-skeleton
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))


(defn clean-line [line]
  "Clean up the line by removing tree characters like ├── and └──."
  (-> line
      (str/replace #"├── |└── " "") ;; Remove ├── and └── prefixes
      (str/replace #"│" "")          ;; Remove │ characters
      (str/trim)))                   ;; Trim whitespace

(defn create-dir [dir-path]
  "Create directory if it doesn't exist and log the action."
  (if (fs/exists? dir-path)
    (println "Directory already exists:" dir-path)
    (do
      (println "Creating directory:" dir-path)
      (fs/create-dirs dir-path))))

(defn create-file [file-path]
  "Create an empty file and log the action."
  (if (fs/exists? file-path)
    (println "File already exists:" file-path)
    (do
      (println "Creating file:" file-path)
      (fs/create-file file-path))))


(defn- depth [line]
  (let [ct (count (re-find #"^[│ ]*[├└]?" line))]
    (cond
      (= 0 ct) 0
      (= 1 ct) 1
      (< 1 ct) (-> ct dec (/ 4 ) inc) )))

(comment 
  (depth "your-project/")
  (depth "├── deps.edn")
  (depth "│   └── my_web_server/")
  )

(defn parse-structure [content]
  (loop [lines (str/split-lines content)
         current-path []
         result []]
    (if (empty? lines)
      result
      (let [line (first lines)
            item (-> line
                     (str/replace #"[│├└]" "")
                     (str/replace #"─" "")
                     str/trim)
            dir? (clojure.string/ends-with? item "/")]
        (recur (rest lines)
               (if (and dir? (not (str/blank? item)))
                 (-> current-path
                     (subvec 0 (depth line))
                     (conj item))
                 current-path)
               (if (not (str/blank? item))
                 (conj result (if dir?
                                (str/join "" current-path)
                                (str (str/join "" current-path) item)))
                 result))))))

(comment
  (parse-structure (slurp "schema.txt")))




(defn create-structure [structure-text]
  "Create directories and files from parsed structure."
  (println "Parsing structure...")
  (let [structure (parse-structure structure-text)]
    ;; Log the parsed structure for verification
    (println "Parsed structure:" structure)
    ;; Loop through the parsed structure and create directories/files
    (doseq [path (rest structure)]
      (prn path)
      (if (str/ends-with? path "/")
        (create-dir path)
        (do
          (create-dir (fs/parent path)) ;; Ensure parent directory exists
          (create-file path))))))

(defn -main [& args]
  "Main function to handle command line argument for schema file."
  (if-let [schema-file (first args)]
    (try
      (let [schema-text (slurp schema-file)]
        ;; Log that we're reading the schema file
        (println "Reading schema from:" schema-file)
        ;; Print the content of the schema for debugging purposes
        (println "Schema content:\n" schema-text)
        ;; Proceed to create the structure
        (create-structure schema-text))
      (catch Exception e
        (println  e)))
    (println "Usage: bb create_project.clj schema.txt")))


;; Entry point for Babashka script
(apply -main *command-line-args*)


