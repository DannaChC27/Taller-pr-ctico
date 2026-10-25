#!/bin/bash
# Compila y ejecuta el Sistema de Biblioteca
mvn -q package -DskipTests && java -jar target/gestion-biblioteca.jar
