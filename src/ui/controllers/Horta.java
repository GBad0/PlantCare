package ui.controllers;

public class Horta {
    private String nome;
    private String plantacao;
    private int quantidade;
    private String responsavel;
    private String dataPlantacao;
    private String localizacao;

    public Horta(String nome, String plantacao, int quantidade, 
                 String responsavel, String dataPlantacao, String localizacao) {
        this.nome = nome;
        this.plantacao = plantacao;
        this.quantidade = quantidade;
        this.responsavel = responsavel;
        this.dataPlantacao = dataPlantacao;
        this.localizacao = localizacao;
    }

    // Getters e Setters (necessários para o TableView)
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getPlantacao() { return plantacao; }
    public void setPlantacao(String plantacao) { this.plantacao = plantacao; }
    
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    
    public String getDataPlantacao() { return dataPlantacao; }
    public void setDataPlantacao(String dataPlantacao) { this.dataPlantacao = dataPlantacao; }
    
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
}