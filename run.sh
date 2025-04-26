#!/bin/bash

PATH_TO_FX=~/javafx/javafx-sdk-21.0.2/lib
OUT_DIR=out

# Limpeza completa
rm -rf $OUT_DIR

# Cria estrutura de diretórios
mkdir -p $OUT_DIR/ui/views
mkdir -p $OUT_DIR/ui/controllers

# Copia recursos
cp src/ui/views/Login.fxml $OUT_DIR/ui/views/

# Compilação
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -d $OUT_DIR $(find src -name "*.java")

# Verificação
echo "Estrutura gerada:"
find $OUT_DIR -type f

# Execução
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp "$OUT_DIR" app.Main