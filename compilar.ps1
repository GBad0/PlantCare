$PATH_TO_FX = "$env:USERPROFILE\javafx\javafx-sdk-21.0.2\lib"
$OUT_DIR = "out"
$SRC_DIR = "src"

# JARs necessários
$JAKARTA_MAIL_JAR = "lib/angus-mail-2.0.2.jar"
$JAKARTA_ACTIVATION_JAR = "lib/angus-activation-2.0.1.jar"
$JSON_JAR = "lib/json-20231013.jar"

# Construir classpath com todos os JARs
$classpath = "$OUT_DIR"
if (Test-Path -Path $JAKARTA_MAIL_JAR) { $classpath += ";$JAKARTA_MAIL_JAR" }
if (Test-Path -Path $JAKARTA_ACTIVATION_JAR) { $classpath += ";$JAKARTA_ACTIVATION_JAR" }
if (Test-Path -Path $JSON_JAR) { $classpath += ";$JSON_JAR" }

Write-Host "=== COMPILANDO PLANT CARE ===" -ForegroundColor Green
Write-Host ""

# Limpeza completa
Write-Host "1. Limpando diretório de saída..." -ForegroundColor Cyan
Remove-Item -Path $OUT_DIR -Recurse -Force -ErrorAction SilentlyContinue

# Cria estrutura de diretórios
Write-Host "2. Criando estrutura de diretórios..." -ForegroundColor Cyan
New-Item -ItemType Directory -Path "$OUT_DIR\ui\views" -Force | Out-Null
New-Item -ItemType Directory -Path "$OUT_DIR\ui\controllers" -Force | Out-Null

# Copia arquivos FXML e recursos
Write-Host "3. Copiando arquivos FXML..." -ForegroundColor Cyan
Copy-Item -Path "$SRC_DIR\ui\views\*" -Destination "$OUT_DIR\ui\views\" -Recurse -Force

# Compilação
Write-Host "4. Compilando código Java..." -ForegroundColor Cyan
try {
    javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.base -cp $classpath -d $OUT_DIR (Get-ChildItem -Path src -Recurse -Filter "*.java").FullName
    Write-Host "   ✓ Compilação concluída com sucesso!" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Erro na compilação: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Execução
Write-Host "`n5. Executando aplicação..." -ForegroundColor Cyan
Write-Host ""

try {
    java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.base --enable-native-access=javafx.graphics -cp $classpath app.Main
} catch {
    Write-Host "   ❌ Erro na execução: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   💡 Verifique se todas as dependências estão instaladas" -ForegroundColor Yellow
}