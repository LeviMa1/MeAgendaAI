package br.com.levima.agenda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PerfilForm {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(min = 2, max = 100)
    private String nome;

    @NotBlank(message = "Celular e obrigatorio")
    private String celular;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
}
