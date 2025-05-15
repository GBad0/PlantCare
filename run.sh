#!/bin/bash

PATH_TO_FX=~/javafx/javafx-sdk-21.0.2/lib
OUT_DIR=out

# Limpeza completa
rm -rf $OUT_DIR

# Cria estrutura de diretórios
mkdir -p $OUT_DIR/ui/views
mkdir -p $OUT_DIR/ui/controllers
# mkdir -p $OUT_DIR/data

cp -r src/ui/views/* $OUT_DIR/ui/views/
# cp -r src/data/* $OUT_DIR/data/ 2>/dev/null || :  # Ignora se não houver dados

# Compilação
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.base -d $OUT_DIR $(find src -name "*.java")

# Verificação
echo "Estrutura gerada:"
find $OUT_DIR -type f

# Execução
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.base  -cp "$OUT_DIR" app.Main