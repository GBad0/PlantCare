$PATH_TO_FX = "$env:USERPROFILE\javafx\javafx-sdk-21.0.2\lib"
$OUT_DIR = "out"
$SRC_DIR = "src"

# Caminhos absolutos dos JARs (Jakarta Mail oficial)
$JAKARTA_MAIL_JAR = (Resolve-Path "lib/jakarta.mail-2.0.1.jar").Path
$JAKARTA_ACTIVATION_JAR = (Resolve-Path "lib/jakarta.activation-2.0.1.jar").Path
$JSON_JAR = (Resolve-Path "lib/json-20231013.jar").Path
$OUT_DIR_ABS = (Resolve-Path $OUT_DIR).Path

# Construir classpath com todos os JARs, tudo entre aspas duplas
$classpath = "$OUT_DIR_ABS;$JAKARTA_MAIL_JAR;$JAKARTA_ACTIVATION_JAR;$JSON_JAR"
$classpathQuoted = '"' + $classpath + '"'

Write-Host "=== COMPILANDO PLANT CARE ===" -ForegroundColor Green
Write-Host ""

# Limpeza completa
Write-Host "1. Limpando diretorio de saida..." -ForegroundColor Cyan
Remove-Item -Path $OUT_DIR -Recurse -Force -ErrorAction SilentlyContinue

# Cria estrutura de diretórios
Write-Host "2. Criando estrutura de diretorios..." -ForegroundColor Cyan
New-Item -ItemType Directory -Path "$OUT_DIR\app" -Force | Out-Null
New-Item -ItemType Directory -Path "$OUT_DIR\ui\views" -Force | Out-Null
New-Item -ItemType Directory -Path "$OUT_DIR\ui\controllers" -Force | Out-Null
New-Item -ItemType Directory -Path "$OUT_DIR\ui\models" -Force | Out-Null
New-Item -ItemType Directory -Path "$OUT_DIR\ui\services" -Force | Out-Null

# Copia arquivos FXML e recursos
Write-Host "3. Copiando arquivos FXML e recursos..." -ForegroundColor Cyan
Copy-Item -Path "$SRC_DIR\ui\views\*" -Destination "$OUT_DIR\ui\views\" -Recurse -Force -ErrorAction SilentlyContinue

# Compilação
Write-Host "4. Compilando codigo Java..." -ForegroundColor Cyan

# Verificar se JavaFX está disponível
if (!(Test-Path -Path $PATH_TO_FX)) {
    Write-Host "   ERRO: JavaFX nao encontrado em: $PATH_TO_FX" -ForegroundColor Red
    Write-Host "   Dica: Execute primeiro: .\setup.ps1" -ForegroundColor Yellow
    exit 1
}

# Verificar se os JARs existem
Write-Host "   Verificando JARs..." -ForegroundColor Cyan
if (Test-Path -Path $JAKARTA_MAIL_JAR) {
    Write-Host "   OK: Jakarta Mail JAR encontrado" -ForegroundColor Green
} else {
    Write-Host "   ERRO: Jakarta Mail JAR nao encontrado" -ForegroundColor Red
    Write-Host "   Dica: Execute primeiro: .\setup.ps1 para baixar os JARs" -ForegroundColor Yellow
    exit 1
}

if (Test-Path -Path $JAKARTA_ACTIVATION_JAR) {
    Write-Host "   OK: Jakarta Activation JAR encontrado" -ForegroundColor Green
} else {
    Write-Host "   ERRO: Jakarta Activation JAR nao encontrado" -ForegroundColor Red
    Write-Host "   Dica: Execute primeiro: .\setup.ps1 para baixar os JARs" -ForegroundColor Yellow
    exit 1
}

# Obter arquivos .java
$javaFiles = Get-ChildItem -Path $SRC_DIR -Recurse -Filter "*.java" | ForEach-Object { "`"$($_.FullName)`"" }

# Montar string com os arquivos
$javaFilesString = $javaFiles -join ' '

# Comando javac completo
$compileCmd = "javac -encoding UTF-8 --module-path `"$PATH_TO_FX`" --add-modules javafx.controls,javafx.fxml,javafx.base -cp $classpathQuoted -d `"$OUT_DIR`" $javaFilesString"

Write-Host "Executando compilacao..."
Write-Host "Classpath: $classpathQuoted"

try {
    Invoke-Expression $compileCmd
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   OK: Compilacao concluida com sucesso!" -ForegroundColor Green
    } else {
        Write-Host "   ERRO: Erro na compilacao." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   ERRO: Durante a compilacao: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Execução
Write-Host "`n5. Executando aplicacao..." -ForegroundColor Cyan
Write-Host ""

$runCmd = "java --module-path `"$PATH_TO_FX`" --add-modules javafx.controls,javafx.fxml,javafx.base --enable-native-access=javafx.graphics -cp $classpathQuoted app.Main"

Write-Host "Executando aplicacao..."
try {
    Invoke-Expression $runCmd
    if ($LASTEXITCODE -ne 0) {
        Write-Host "   ERRO: Erro na execucao da aplicacao." -ForegroundColor Red
        Write-Host "   Dica: Verifique se todas as dependencias estao corretas." -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ERRO: Durante a execucao: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Dica: Verifique se o JavaFX esta configurado corretamente." -ForegroundColor Yellow
}
