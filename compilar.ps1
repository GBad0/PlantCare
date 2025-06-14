$PATH_TO_FX = "$env:USERPROFILE\javafx\javafx-sdk-21.0.2\lib"
$OUT_DIR = "out"

Remove-Item -Path $OUT_DIR -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $OUT_DIR -Force | Out-Null

# Compilação
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.base -d $OUT_DIR (Get-ChildItem -Path src -Recurse -Filter "*.java").FullName

# Execução
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.base -cp "$OUT_DIR" app.Main