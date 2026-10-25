#!/bin/bash
cd "$(dirname "$0")"
mvn -q package -DskipTests
java -jar target/gestion-biblioteca.jar
