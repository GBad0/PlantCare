<#
.SYNOPSIS
    Script de configuração automática para projetos JavaFX
.DESCRIPTION
    Baixa o JavaFX SDK 21.0.2, configura o ambiente e compila o projeto
#>

# Configurações
$JAVAFX_VERSION = "21.0.2"
$JAVAFX_URL = "https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_windows-x64_bin-sdk.zip"
$INSTALL_DIR = "$env:USERPROFILE\javafx"
$JAVAFX_SDK_DIR = "$INSTALL_DIR\javafx-sdk-$JAVAFX_VERSION"

# Verifica se o JavaFX já está instalado
if (Test-Path -Path $JAVAFX_SDK_DIR) {
    Write-Host "JavaFX SDK já instalado em $JAVAFX_SDK_DIR" -ForegroundColor Yellow
} else {
    # Cria diretório se não existir
    New-Item -ItemType Directory -Path $INSTALL_DIR -Force | Out-Null

    # Baixa o JavaFX SDK
    Write-Host "Baixando JavaFX SDK $JAVAFX_VERSION..." -ForegroundColor Cyan
    $zipFile = "$INSTALL_DIR\javafx.zip"
    Invoke-WebRequest -Uri $JAVAFX_URL -OutFile $zipFile

    # Extrai o arquivo
    Write-Host "Extraindo JavaFX SDK..." -ForegroundColor Cyan
    Expand-Archive -Path $zipFile -DestinationPath $INSTALL_DIR -Force

    # Remove o arquivo zip
    Remove-Item -Path $zipFile

    # Renomeia diretório extraído para o padrão
    $extractedDir = Get-ChildItem -Path $INSTALL_DIR -Directory | Where-Object { $_.Name -like "javafx-sdk*" } | Select-Object -First 1
    if ($extractedDir) {
        Rename-Item -Path $extractedDir.FullName -NewName "javafx-sdk-$JAVAFX_VERSION"
    }

    Write-Host "JavaFX SDK instalado com sucesso em $JAVAFX_SDK_DIR" -ForegroundColor Green
}

# Agora executa o script de compilação
Write-Host "`nCompilando e executando o projeto..." -ForegroundColor Cyan
.\compilar.ps1