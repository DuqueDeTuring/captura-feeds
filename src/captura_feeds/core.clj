(ns captura-feeds.core
  (:require [feedparser-clj.core :as feedparser]))


(defn llave [par]
  (first par))

(defn valor [par]
  (second par))

(defn descargar-contenido-noticias
  "Asocia a la estructura de la noticia una llave *:text-noticia* que contiene el texto de la noticia extraído según una función provista vía parámetro"
  [noticia extractor-de-texto]
  (assoc-in noticia
            [:texto-noticia]
            (extractor-de-texto (:uri noticia))))

(defn obtenga-el-contenido [id-de-feed titulares extractor]
  {id-de-feed
   (map #(descargar-contenido-noticias % extractor) titulares)})

(defn obtenga-los-titulares [id-de-feed url]
  {id-de-feed (:entries (feedparser/parse-feed url))})

(defn descargar-rss
  "Recibe un mapa en donde cada feed de rss está asociado a una llave X, se extraen los titulares y luego se adjunta el texto de cada una de las entradas de cada uno de los feeds" 
  [todos-los-feeds extrae-contenido]
  (let [todos-los-titulares
        (reduce merge
                (map #(obtenga-los-titulares (llave %) (valor %))
                     todos-los-feeds))]
    (reduce merge
            (map #(obtenga-el-contenido (llave %) (valor %) extrae-contenido) 
                 todos-los-titulares))))



