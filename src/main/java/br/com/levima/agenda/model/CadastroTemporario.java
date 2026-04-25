package br.com.levima.agenda.model;

public class CadastroTemporario {

    private String nome;
    private String email;
    private String celular;
    private String senha;
    private String codigoVerificacao;
    private long timestampCodigo;

    public CadastroTemporario() {
    }

    public CadastroTemporario(String nome, String email, String celular, String senha, String codigoVerificacao) {
        this.nome = nome;
        this.email = email;
        this.celular = celular;
        this.senha = senha;
        this.codigoVerificacao = codigoVerificacao;
        this.timestampCodigo = System.currentTimeMillis();
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

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public long getTimestampCodigo() {
        return timestampCodigo;
    }

    public void setTimestampCodigo(long timestampCodigo) {
        this.timestampCodigo = timestampCodigo;
    }

    public boolean isCodigoValido() {
        // Código válido por 10 minutos
        long tempoDecorrido = System.currentTimeMillis() - timestampCodigo;
        return tempoDecorrido < (10 * 60 * 1000);
    }
}

