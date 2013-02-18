(ns captura-feeds.costarica
  (:gen-class)
  (:require [captura-feeds.core :as core]
            [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]))

(def lanacion-feeds {:portada "http://www.nacion.com/Generales/RSS/EdicionRss.aspx?section=portada"
                     :nacionales "http://www.nacion.com/Generales/RSS/EdicionRss.aspx?section=elpais"
                     :economía "http://www.nacion.com/Generales/RSS/EdicionRss.aspx?section=economia"
                     :opinión "http://www.nacion.com/Generales/RSS/EdicionRss.aspx?section=opinion"
                     :internacionales "http://www.nacion.com/Generales/RSS/EdicionRss.aspx?section=mundo"
                     :sucesos "http://www.nacion.com/Generales/RSS/EdicionRss.aspx?section=sucesos"
                     :entretenimiento "http://www.nacion.com/Generales/RSS/EdicionRss.aspx?section=entretenimiento"})

(defn contenido-noticia-nacion
  "Extrae el texto de una noticia del periódico La Nación. Unimos todos los fragmentos de texto recuperados del cuerpo de los elementos <p> en una sola hilera pero, como hay algunos casos en los que el filtro anterior no es suficiente porque tienen algunos nodos extra como hijos, debemos hacer una última revisión para extraer cualquier otro texto. Ese es el propósito de la aplicación del *map* antes del *reduce* al final."
  [url]
  (let [resource (html/html-resource (java.net.URL. url))
        párrafos (html/select resource [:p.para])
        contenido-párrafos (map #(:content %) párrafos)]
    (reduce str (map #(if (string? %)
                        %
                        (first (:content %)))
                     (flatten contenido-párrafos)))))

(defn transforma-valor-a-json [key value]
  (if (= key :published-date)
    (.format (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss.SSSZ") value)
    value))

(defn -main
  [& args]
  (let [archivo-salida (first args)
        noticias (core/descargar-rss lanacion-feeds contenido-noticia-nacion)]
    (spit archivo-salida (json/write-str noticias :value-fn transforma-valor-a-json)))) ; escribimos toda la estructura a disco en formato JSON

