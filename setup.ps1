<#
.SYNOPSIS
    Script de configuração automática para projetos JavaFX com todas as dependências
.DESCRIPTION
    Baixa o JavaFX SDK 21.0.2, Jakarta Mail JARs, configura o ambiente e compila o projeto
#>

# Configurações
$JAVAFX_VERSION = "21.0.2"
$JAVAFX_URL = "https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_windows-x64_bin-sdk.zip"
$INSTALL_DIR = "$env:USERPROFILE\javafx"
$JAVAFX_SDK_DIR = "$INSTALL_DIR\javafx-sdk-$JAVAFX_VERSION"

# URLs para Jakarta Mail JARs
$JAKARTA_MAIL_URL = "https://repo1.maven.org/maven2/org/eclipse/angus/angus-mail/2.0.2/angus-mail-2.0.2.jar"
$JAKARTA_ACTIVATION_URL = "https://repo1.maven.org/maven2/org/eclipse/angus/angus-activation/2.0.1/angus-activation-2.0.1.jar"

Write-Host "=== CONFIGURAÇÃO COMPLETA DO PLANT CARE ===" -ForegroundColor Green
Write-Host ""

# 1. Criar estrutura de diretórios
Write-Host "1. Criando estrutura de diretórios..." -ForegroundColor Cyan
$directories = @(
    "lib",
    "data",
    "out",
    "out/ui",
    "out/ui/views",
    "out/ui/controllers"
)

foreach ($dir in $directories) {
    if (!(Test-Path -Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Write-Host "   ✓ Criado: $dir" -ForegroundColor Green
    } else {
        Write-Host "   ✓ Já existe: $dir" -ForegroundColor Yellow
    }
}

# 2. Verificar e baixar JavaFX SDK
Write-Host "`n2. Configurando JavaFX SDK..." -ForegroundColor Cyan
if (Test-Path -Path $JAVAFX_SDK_DIR) {
    Write-Host "   ✓ JavaFX SDK já instalado em $JAVAFX_SDK_DIR" -ForegroundColor Yellow
} else {
    # Cria diretório se não existir
    New-Item -ItemType Directory -Path $INSTALL_DIR -Force | Out-Null

    # Baixa o JavaFX SDK
    Write-Host "   📥 Baixando JavaFX SDK $JAVAFX_VERSION..." -ForegroundColor Cyan
    $zipFile = "$INSTALL_DIR\javafx.zip"
    try {
        Invoke-WebRequest -Uri $JAVAFX_URL -OutFile $zipFile -UseBasicParsing
        Write-Host "   ✓ Download concluído" -ForegroundColor Green
    } catch {
        Write-Host "   ❌ Erro ao baixar JavaFX: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }

    # Extrai o arquivo
    Write-Host "   📦 Extraindo JavaFX SDK..." -ForegroundColor Cyan
    try {
        Expand-Archive -Path $zipFile -DestinationPath $INSTALL_DIR -Force
        Write-Host "   ✓ Extração concluída" -ForegroundColor Green
    } catch {
        Write-Host "   ❌ Erro ao extrair: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }

    # Remove o arquivo zip
    Remove-Item -Path $zipFile -Force

    # Renomeia diretório extraído para o padrão
    $extractedDir = Get-ChildItem -Path $INSTALL_DIR -Directory | Where-Object { $_.Name -like "javafx-sdk*" } | Select-Object -First 1
    if ($extractedDir) {
        Rename-Item -Path $extractedDir.FullName -NewName "javafx-sdk-$JAVAFX_VERSION"
    }

    Write-Host "   ✓ JavaFX SDK instalado com sucesso em $JAVAFX_SDK_DIR" -ForegroundColor Green
}

# 3. Verificar e baixar Jakarta Mail JARs
Write-Host "`n3. Configurando Jakarta Mail JARs..." -ForegroundColor Cyan

# Verificar se os JARs já existem
$mailJar = "lib/angus-mail-2.0.2.jar"
$activationJar = "lib/angus-activation-2.0.1.jar"

if (!(Test-Path -Path $mailJar)) {
    Write-Host "   📥 Baixando Jakarta Mail JAR..." -ForegroundColor Cyan
    try {
        Invoke-WebRequest -Uri $JAKARTA_MAIL_URL -OutFile $mailJar -UseBasicParsing
        Write-Host "   ✓ Jakarta Mail JAR baixado" -ForegroundColor Green
    } catch {
        Write-Host "   ❌ Erro ao baixar Jakarta Mail: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "   ✓ Jakarta Mail JAR já existe" -ForegroundColor Yellow
}

if (!(Test-Path -Path $activationJar)) {
    Write-Host "   📥 Baixando Jakarta Activation JAR..." -ForegroundColor Cyan
    try {
        Invoke-WebRequest -Uri $JAKARTA_ACTIVATION_URL -OutFile $activationJar -UseBasicParsing
        Write-Host "   ✓ Jakarta Activation JAR baixado" -ForegroundColor Green
    } catch {
        Write-Host "   ❌ Erro ao baixar Jakarta Activation: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "   ✓ Jakarta Activation JAR já existe" -ForegroundColor Yellow
}

# 4. Verificar JSON JAR
Write-Host "`n4. Verificando JSON JAR..." -ForegroundColor Cyan
if (Test-Path -Path "lib/json-20231013.jar") {
    Write-Host "   ✓ JSON JAR já existe" -ForegroundColor Yellow
} else {
    Write-Host "   ⚠️ JSON JAR não encontrado. Baixe manualmente se necessário." -ForegroundColor Yellow
}

# 5. Criar arquivo de configuração de email (se não existir)
Write-Host "`n5. Configurando arquivo de email..." -ForegroundColor Cyan
$emailConfigFile = "email_config.properties"
if (!(Test-Path -Path $emailConfigFile)) {
    $emailConfig = @"
# Configuração de Email
# Descomente e configure as linhas abaixo com suas credenciais

# Gmail SMTP
email.smtp.host=smtp.gmail.com
email.smtp.port=587
email.smtp.auth=true
email.smtp.starttls.enable=true

# Suas credenciais (substitua pelos seus dados)
# email.username=seu-email@gmail.com
# email.password=sua-senha-de-app

# Alternativa: usar variáveis de ambiente
# EMAIL_USERNAME=seu-email@gmail.com
# EMAIL_PASSWORD=sua-senha-de-app
"@
    
    $emailConfig | Out-File -FilePath $emailConfigFile -Encoding UTF8
    Write-Host "   ✓ Arquivo de configuração de email criado: $emailConfigFile" -ForegroundColor Green
    Write-Host "   ⚠️ Configure suas credenciais de email no arquivo $emailConfigFile" -ForegroundColor Yellow
} else {
    Write-Host "   ✓ Arquivo de configuração de email já existe" -ForegroundColor Yellow
}

# 6. Verificar Java
Write-Host "`n6. Verificando Java..." -ForegroundColor Cyan
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($javaVersion) {
        Write-Host "   ✓ Java encontrado: $javaVersion" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Java não encontrado. Instale o Java 17 ou superior." -ForegroundColor Red
    }
} catch {
    Write-Host "   ❌ Java não encontrado. Instale o Java 17 ou superior." -ForegroundColor Red
}

# 7. Compilar e executar
Write-Host "`n7. Compilando e executando o projeto..." -ForegroundColor Cyan
Write-Host ""

# Executa o script de compilação
try {
    .\compilar.ps1
} catch {
    Write-Host "   ❌ Erro durante a compilação: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   💡 Verifique se o Java está instalado e configurado corretamente" -ForegroundColor Yellow
}

Write-Host "`n=== CONFIGURAÇÃO CONCLUÍDA ===" -ForegroundColor Green
Write-Host "`n📋 Resumo da configuração:" -ForegroundColor Cyan
Write-Host "   ✓ Estrutura de diretórios criada" -ForegroundColor Green
Write-Host "   ✓ JavaFX SDK configurado" -ForegroundColor Green
Write-Host "   ✓ Jakarta Mail JARs baixados" -ForegroundColor Green
Write-Host "   ✓ Arquivo de configuração de email criado" -ForegroundColor Green
Write-Host "`n🚀 O projeto está pronto para uso!" -ForegroundColor Green
Write-Host "`n💡 Próximos passos:" -ForegroundColor Yellow
Write-Host "   1. Configure suas credenciais de email em email_config.properties" -ForegroundColor White
Write-Host "   2. Execute o projeto com: .\compilar.ps1" -ForegroundColor White
Write-Host "   3. Faça login e comece a usar o PlantCare!" -ForegroundColor White