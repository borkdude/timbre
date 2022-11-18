#!/usr/bin/env bb

(ns graal-test
  (:require
   [babashka.fs :as fs]
   [babashka.process :refer [shell]]
   [clojure.string :as str]))

(defn uberjar []
  (shell "lein with-profiles +graal-test uberjar"))

(defn native-image []
  (let [graalvm-home (System/getenv "GRAALVM_HOME")
        native-image-bin (fs/file graalvm-home "bin" "native-image")]
    (shell (str (fs/file graalvm-home "bin" "gu")) "install" "native-image")
    (shell native-image-bin "-jar" "target/graal.jar" "--no-fallback" "graal_test")))

(defn test-native-image []
  (let [{:keys [out]}
        (shell {:out :string} (if (fs/windows?)
                                "graal_test.exe"
                                "./graal_test") "1" "2" "3")]
    (assert (str/includes? out (str '("1" "2" "3"))) out)))
