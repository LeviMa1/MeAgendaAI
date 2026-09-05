package br.com.levima.agenda.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    private String id;

    @Column(nullable = false)
    private String emailUsuario;

    @Column(nullable = false)
    private String nomeUsuario;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horario;

    private String descricao;

    @Column(length = 50)
    private String servicoId;

    @Column(length = 120)
    private String servicoNome;

    @Convert(converter = StatusAgendamentoConverter.class)
    @Column(nullable = false, length = 20)
    private StatusAgendamento status;

    private long timestamp;

    @Column(nullable = false)
    private boolean lembrete24hEnviado = false;

    @Column(nullable = false)
    private boolean lembrete1hEnviado = false;

    @Version
    private Long version;

    public Agendamento() {
        this.id = java.util.UUID.randomUUID().toString();
        this.status = StatusAgendamento.PENDENTE;
        this.timestamp = System.currentTimeMillis();
    }

    public Agendamento(String emailUsuario, String nomeUsuario, LocalDate data, LocalTime horario,
                       String servicoId, String servicoNome, String descricao) {
        this();
        this.emailUsuario = emailUsuario;
        this.nomeUsuario = nomeUsuario;
        this.data = data;
        this.horario = horario;
        this.servicoId = servicoId;
        this.servicoNome = servicoNome;
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getServicoId() {
        return servicoId;
    }

    public void setServicoId(String servicoId) {
        this.servicoId = servicoId;
    }

    public String getServicoNome() {
        return servicoNome;
    }

    public void setServicoNome(String servicoNome) {
        this.servicoNome = servicoNome;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLembrete24hEnviado() {
        return lembrete24hEnviado;
    }

    public void setLembrete24hEnviado(boolean lembrete24hEnviado) {
        this.lembrete24hEnviado = lembrete24hEnviado;
    }

    public boolean isLembrete1hEnviado() {
        return lembrete1hEnviado;
    }

    public void setLembrete1hEnviado(boolean lembrete1hEnviado) {
        this.lembrete1hEnviado = lembrete1hEnviado;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean isAtivo() {
        return status == StatusAgendamento.CONFIRMADO || status == StatusAgendamento.PENDENTE;
    }

    @Override
    public String toString() {
        return "Agendamento{id='" + id + "', email='" + emailUsuario + "', data=" + data + ", horario=" + horario + ", status=" + status + "}";
    }
}
