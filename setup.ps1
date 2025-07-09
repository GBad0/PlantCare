<#
.SYNOPSIS
    Script de configuracao automatica para projetos JavaFX com todas as dependencias
.DESCRIPTION
    Baixa o JavaFX SDK 21.0.2, Jakarta Mail JARs, configura o ambiente e compila o projeto
#>

# Configuracoes
$JAVAFX_VERSION = "21.0.2"
$JAVAFX_URL = "https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_windows-x64_bin-sdk.zip"
$INSTALL_DIR = "$env:USERPROFILE\javafx"
$JAVAFX_SDK_DIR = "$INSTALL_DIR\javafx-sdk-$JAVAFX_VERSION"

# URLs para Jakarta Mail JARs (oficiais)
$JAKARTA_MAIL_URL = "https://repo1.maven.org/maven2/com/sun/mail/jakarta.mail/2.0.1/jakarta.mail-2.0.1.jar"
$JAKARTA_ACTIVATION_URL = "https://repo1.maven.org/maven2/com/sun/activation/jakarta.activation/2.0.1/jakarta.activation-2.0.1.jar"

Write-Host "=== CONFIGURACAO COMPLETA DO PLANT CARE ===" -ForegroundColor Green
Write-Host ""

# 0. Verificar Java primeiro
Write-Host "0. Verificando Java..." -ForegroundColor Cyan
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($javaVersion) {
        Write-Host "   OK: Java encontrado: $javaVersion" -ForegroundColor Green
    } else {
        Write-Host "   ERRO: Java nao encontrado. Instale o Java 17 ou superior." -ForegroundColor Red
        Write-Host "   Dica: Baixe em: https://adoptium.net/" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "   ERRO: Java nao encontrado. Instale o Java 17 ou superior." -ForegroundColor Red
    Write-Host "   Dica: Baixe em: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# 1. Criar estrutura de diretorios
Write-Host "`n1. Criando estrutura de diretorios..." -ForegroundColor Cyan
$directories = @(
    "lib",
    "data",
    "out",
    "out/app",
    "out/ui",
    "out/ui/views",
    "out/ui/controllers",
    "out/ui/models",
    "out/ui/services"
)

foreach ($dir in $directories) {
    if (!(Test-Path -Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Write-Host "   OK: Criado: $dir" -ForegroundColor Green
    } else {
        Write-Host "   OK: Ja existe: $dir" -ForegroundColor Yellow
    }
}

# 2. Verificar e baixar JavaFX SDK
Write-Host "`n2. Configurando JavaFX SDK..." -ForegroundColor Cyan
if (Test-Path -Path $JAVAFX_SDK_DIR) {
    Write-Host "   OK: JavaFX SDK ja instalado em $JAVAFX_SDK_DIR" -ForegroundColor Green
} else {
    # Cria diretorio se nao existir
    New-Item -ItemType Directory -Path $INSTALL_DIR -Force | Out-Null

    # Baixa o JavaFX SDK
    Write-Host "   Baixando JavaFX SDK $JAVAFX_VERSION..." -ForegroundColor Cyan
    $zipFile = "$INSTALL_DIR\javafx.zip"
    try {
        Invoke-WebRequest -Uri $JAVAFX_URL -OutFile $zipFile -UseBasicParsing
        Write-Host "   OK: Download concluido" -ForegroundColor Green
    } catch {
        Write-Host "   ERRO: Ao baixar JavaFX: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "   Dica: Verifique sua conexao com a internet" -ForegroundColor Yellow
        exit 1
    }

    # Extrai o arquivo
    Write-Host "   Extraindo JavaFX SDK..." -ForegroundColor Cyan
    try {
        Expand-Archive -Path $zipFile -DestinationPath $INSTALL_DIR -Force
        Write-Host "   OK: Extracao concluida" -ForegroundColor Green
    } catch {
        Write-Host "   ERRO: Ao extrair: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }

    # Remove o arquivo zip
    Remove-Item -Path $zipFile -Force

    # Renomeia diretorio extraido para o padrao
    $extractedDir = Get-ChildItem -Path $INSTALL_DIR -Directory | Where-Object { $_.Name -like "javafx-sdk*" } | Select-Object -First 1
    if ($extractedDir) {
        Rename-Item -Path $extractedDir.FullName -NewName "javafx-sdk-$JAVAFX_VERSION"
    }

    Write-Host "   OK: JavaFX SDK instalado com sucesso em $JAVAFX_SDK_DIR" -ForegroundColor Green
}

# 3. Verificar e baixar Jakarta Mail JARs
Write-Host "`n3. Configurando Jakarta Mail JARs..." -ForegroundColor Cyan

$mailJar = "lib/jakarta.mail-2.0.1.jar"
$activationJar = "lib/jakarta.activation-2.0.1.jar"

if (!(Test-Path -Path $mailJar)) {
    Write-Host "   Baixando Jakarta Mail JAR..." -ForegroundColor Cyan
    try {
        Invoke-WebRequest -Uri $JAKARTA_MAIL_URL -OutFile $mailJar -UseBasicParsing
        Write-Host "   OK: Jakarta Mail JAR baixado" -ForegroundColor Green
    } catch {
        Write-Host "   ERRO: Ao baixar Jakarta Mail: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "   OK: Jakarta Mail JAR ja existe" -ForegroundColor Green
}

if (!(Test-Path -Path $activationJar)) {
    Write-Host "   Baixando Jakarta Activation JAR..." -ForegroundColor Cyan
    try {
        Invoke-WebRequest -Uri $JAKARTA_ACTIVATION_URL -OutFile $activationJar -UseBasicParsing
        Write-Host "   OK: Jakarta Activation JAR baixado" -ForegroundColor Green
    } catch {
        Write-Host "   ERRO: Ao baixar Jakarta Activation: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "   OK: Jakarta Activation JAR ja existe" -ForegroundColor Green
}

# 4. Verificar JSON JAR
Write-Host "`n4. Verificando JSON JAR..." -ForegroundColor Cyan
if (Test-Path -Path "lib/json-20231013.jar") {
    Write-Host "   OK: JSON JAR ja existe" -ForegroundColor Green
} else {
    Write-Host "   ATENCAO: JSON JAR nao encontrado. Baixe manualmente se necessario." -ForegroundColor Yellow
    Write-Host "   URL: https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar" -ForegroundColor Cyan
}

# 5. Criar arquivo de configuracao de email (se nao existir)
Write-Host "`n5. Configurando arquivo de email..." -ForegroundColor Cyan
$emailConfigFile = "email_config.properties"
if (!(Test-Path -Path $emailConfigFile)) {
    $emailConfig = @"
# Configuracao de Email
# Descomente e configure as linhas abaixo com suas credenciais

# Gmail SMTP
email.smtp.host=smtp.gmail.com
email.smtp.port=587
email.smtp.auth=true
email.smtp.starttls.enable=true

# Suas credenciais (substitua pelos seus dados)
# email.username=seu-email@gmail.com
# email.password=sua-senha-de-app

# Alternativa: usar variaveis de ambiente
# EMAIL_USERNAME=seu-email@gmail.com
# EMAIL_PASSWORD=sua-senha-de-app
"@

    $emailConfig | Out-File -FilePath $emailConfigFile -Encoding UTF8
    Write-Host "   OK: Arquivo de configuracao de email criado: $emailConfigFile" -ForegroundColor Green
    Write-Host "   ATENCAO: Configure suas credenciais de email no arquivo $emailConfigFile" -ForegroundColor Yellow
} else {
    Write-Host "   OK: Arquivo de configuracao de email ja existe" -ForegroundColor Green
}

# 6. Verificar se todos os arquivos necessarios estao presentes
Write-Host "`n6. Verificando arquivos do projeto..." -ForegroundColor Cyan
$requiredFiles = @(
    "src/app/Main.java",
    "src/ui/views/Login.fxml",
    "src/ui/controllers/LoginController.java"
)

$missingFiles = @()
foreach ($file in $requiredFiles) {
    if (Test-Path -Path $file) {
        Write-Host "   OK: $file" -ForegroundColor Green
    } else {
        Write-Host "   ERRO: $file" -ForegroundColor Red
        $missingFiles += $file
    }
}

if ($missingFiles.Count -gt 0) {
    Write-Host "   ATENCAO: Alguns arquivos estao faltando. Verifique a estrutura do projeto." -ForegroundColor Yellow
}

# 7. Compilar e executar
Write-Host "`n7. Compilando e executando o projeto..." -ForegroundColor Cyan
Write-Host ""

try {
    & .\compilar.ps1
} catch {
    Write-Host "   ERRO: Durante a compilacao: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Dica: Verifique se o Java esta instalado e configurado corretamente" -ForegroundColor Yellow
}

Write-Host "`n=== CONFIGURACAO CONCLUIDA ===" -ForegroundColor Green
Write-Host "`nResumo da configuracao:" -ForegroundColor Cyan
Write-Host "   - Estrutura de diretorios criada" -ForegroundColor Green
Write-Host "   - JavaFX SDK configurado" -ForegroundColor Green
Write-Host "   - Jakarta Mail JARs baixados" -ForegroundColor Green
Write-Host "   - Arquivo de configuracao de email criado" -ForegroundColor Green
Write-Host "`nProjeto pronto para uso!" -ForegroundColor Green
Write-Host "`nProximos passos:" -ForegroundColor Yellow
Write-Host "   1. Configure suas credenciais de email em email_config.properties" -ForegroundColor White
Write-Host "   2. Execute o projeto com: .\\compilar.ps1" -ForegroundColor White
Write-Host "   3. Faca login e comece a usar o PlantCare!" -ForegroundColor White 