$PATH_TO_FX = "$env:USERPROFILE\javafx\javafx-sdk-21.0.2\lib"
$OUT_DIR = "out"
$SRC_DIR = "src"

# Limpeza completa
Remove-Item -Path $OUT_DIR -Recurse -Force -ErrorAction SilentlyContinue

# Cria estrutura de diretórios
New-Item -ItemType Directory -Path "$OUT_DIR\ui\views" -Force | Out-Null
New-Item -ItemType Directory -Path "$OUT_DIR\ui\controllers" -Force | Out-Null

# Copia arquivos FXML e recursos
Copy-Item -Path "$SRC_DIR\ui\views\*" -Destination "$OUT_DIR\ui\views\" -Recurse -Force

# Compilação
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.base -d $OUT_DIR (Get-ChildItem -Path src -Recurse -Filter "*.java").FullName

# Execução
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.base --enable-native-access=javafx.graphics -cp "$OUT_DIR" app.Main