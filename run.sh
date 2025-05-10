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
cp src/ui/views/MainDashboard.fxml $OUT_DIR/ui/views/
cp src/ui/views/ListaHortas.fxml $OUT_DIR/ui/views/
cp src/ui/views/ListaEditHortas.fxml $OUT_DIR/ui/views/
cp src/ui/views/NewHortas.fxml $OUT_DIR/ui/views/
cp src/ui/views/ListaNotes.fxml $OUT_DIR/ui/views/
cp src/ui/views/ListaAddNotes.fxml $OUT_DIR/ui/views/
cp src/ui/views/PrevisaoTempo.fxml $OUT_DIR/ui/views/
cp src/ui/views/Colheita.fxml $OUT_DIR/ui/views/
cp src/ui/views/*.css $OUT_DIR/ui/views/ 2>/dev/null || :  # Ignora se não existir CSS

# Compilação
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -d $OUT_DIR $(find src -name "*.java")

# Verificação
echo "Estrutura gerada:"
find $OUT_DIR -type f

# Execução
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp "$OUT_DIR" app.Main