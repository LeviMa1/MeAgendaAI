package br.com.levima.agenda.model;

public class Contato {

    private String nome;
    private String email;
    private String celular;
    private String senha;

    public Contato() {
    }

    public Contato(String nome, String email, String celular, String senha) {
        this.nome = nome;
        this.email = email;
        this.celular = celular;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Contato{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", celular='" + celular + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}

