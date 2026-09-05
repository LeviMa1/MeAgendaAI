package br.com.levima.agenda.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "cadastro_pendente")
public class CadastroPendente {

    @Id
    private String email;

    @Column(nullable = false)
    private String nome;

    private String celular;

    @Column(nullable = false)
    private String senha;

    private String codigoVerificacao;

    private Instant timestampCodigo;

    @Column(nullable = false)
    private int tentativas = 0;

    @Column(nullable = false)
    private int enviosUltimaHora = 0;

    private Instant ultimoEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPendente tipo = TipoPendente.CADASTRO;

    public enum TipoPendente {
        CADASTRO, RECUPERACAO_SENHA
    }

    public CadastroPendente() {
    }

    public CadastroPendente(String nome, String email, String senha, TipoPendente tipo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.timestampCodigo = Instant.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public Instant getTimestampCodigo() {
        return timestampCodigo;
    }

    public void setTimestampCodigo(Instant timestampCodigo) {
        this.timestampCodigo = timestampCodigo;
    }

    public int getTentativas() {
        return tentativas;
    }

    public void setTentativas(int tentativas) {
        this.tentativas = tentativas;
    }

    public int getEnviosUltimaHora() {
        return enviosUltimaHora;
    }

    public void setEnviosUltimaHora(int enviosUltimaHora) {
        this.enviosUltimaHora = enviosUltimaHora;
    }

    public Instant getUltimoEnvio() {
        return ultimoEnvio;
    }

    public void setUltimoEnvio(Instant ultimoEnvio) {
        this.ultimoEnvio = ultimoEnvio;
    }

    public TipoPendente getTipo() {
        return tipo;
    }

    public void setTipo(TipoPendente tipo) {
        this.tipo = tipo;
    }

    public void incrementarTentativa() {
        this.tentativas++;
    }

    public void registrarEnvio() {
        Instant agora = Instant.now();
        if (ultimoEnvio == null || ultimoEnvio.isBefore(agora.minusSeconds(3600))) {
            enviosUltimaHora = 1;
        } else {
            enviosUltimaHora++;
        }
        ultimoEnvio = agora;
    }

    public boolean podeEnviarCodigo(int maxEnviosPorHora) {
        if (ultimoEnvio == null || ultimoEnvio.isBefore(Instant.now().minusSeconds(3600))) {
            return true;
        }
        return enviosUltimaHora < maxEnviosPorHora;
    }

    public boolean isCodigoValido(int validadeMinutos) {
        if (timestampCodigo == null) {
            return false;
        }
        return timestampCodigo.isAfter(Instant.now().minusSeconds(validadeMinutos * 60L));
    }
}
