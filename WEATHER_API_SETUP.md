# 🌤️ Integração com OpenWeatherMap API

## ✅ Status: Implementado e Funcionando

A funcionalidade de previsão do tempo foi **completamente implementada** e está pronta para uso!

### 🎯 Funcionalidades Implementadas

- ✅ **Busca por cidade**: Digite o nome de qualquer cidade para obter a previsão
- ✅ **Dados atuais**: Temperatura, umidade, velocidade do vento e descrição
- ✅ **Previsão de 3 dias**: Temperatura máxima/mínima, descrição e ícones para os próximos 3 dias
- ✅ **Interface responsiva**: Design moderno e amigável
- ✅ **Ícones visuais**: Exibição de ícones do clima
- ✅ **Dicas agrícolas**: Informações úteis para planejamento agrícola
- ✅ **Tratamento de erros**: Mensagens claras para problemas de conexão ou cidade não encontrada

### 🔧 Configuração

A chave da API já está configurada no arquivo `src/ui/services/WeatherService.java`:
```java
private static final String API_KEY = "a23dbe7a693a2e745fb7b4233beeb1a4";
```

### 📁 Arquivos Criados/Modificados

1. **`src/ui/models/WeatherData.java`** - Modelo para dados do clima (atualizado para suportar previsões)
2. **`src/ui/services/WeatherService.java`** - Serviço para chamadas à API (atualizado para buscar previsão de 5 dias)
3. **`src/ui/controllers/PrevisaoTempoController.java`** - Controlador da interface (atualizado para exibir previsões)
4. **`src/ui/views/PrevisaoTempo.fxml`** - Interface gráfica
5. **`lib/json-20231013.jar`** - Biblioteca JSON (já baixada)

### 🚀 Como Usar

1. Execute o projeto no IntelliJ IDEA
2. Faça login no sistema
3. No menu lateral, clique em **"🌤️ Previsão do tempo"**
4. Digite o nome de uma cidade (ex: "São Paulo", "Rio de Janeiro")
5. Clique em **"🔍 Buscar"** ou pressione Enter
6. Visualize os dados atuais e a previsão dos próximos 3 dias

### 📊 Dados Exibidos

#### **Dados Atuais (Hoje):**
- **Temperatura**: Em graus Celsius
- **Descrição**: Condição climática em português
- **Umidade**: Percentual de umidade do ar
- **Vento**: Velocidade em metros por segundo
- **Ícone**: Representação visual do clima

#### **Previsão dos Próximos 3 Dias:**
- **Data**: Formato DD/MM
- **Temperatura**: Máxima e mínima em graus Celsius
- **Descrição**: Condição climática prevista
- **Ícone**: Representação visual do clima previsto

### 💡 Dicas Agrícolas

A interface também exibe dicas úteis para agricultura:
- Temperatura ideal para plantio: 15°C - 30°C
- Umidade alta (acima de 70%) favorece o crescimento
- Ventos fortes podem danificar plantas jovens
- Evite regar em dias muito úmidos

### 🔍 Exemplos de Cidades para Teste

- São Paulo, SP
- Rio de Janeiro, RJ
- Brasília, DF
- Salvador, BA
- Fortaleza, CE
- Belo Horizonte, MG
- Curitiba, PR
- Recife, PE

### ⚠️ Limitações da API Gratuita

- 60 chamadas por minuto
- 1.000 chamadas por dia
- Dados de previsão limitados a 5 dias

### 🛠️ Solução de Problemas

**Erro 401 (Unauthorized)**
- A chave da API está configurada corretamente
- Se necessário, aguarde algumas horas após criar a chave

**Erro 404 (City not found)**
- Verifique se o nome da cidade está correto
- Tente usar nomes em inglês para cidades internacionais

**Erro de conexão**
- Verifique sua conexão com a internet
- Verifique se o firewall não está bloqueando as conexões

### 🎉 Pronto para Uso!

A funcionalidade está **100% operacional** e integrada ao sistema PlantCare. Agora você pode:
- Ver o clima atual de qualquer cidade
- Planejar suas atividades agrícolas com base na previsão de 3 dias
- Tomar decisões informadas sobre plantio e colheita

Basta executar o projeto e testar! 🌤️✨ 