package br.com.levima.agenda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(nullable = false)
    private String status; // "confirmado", "pendente", "cancelado"

    private long timestamp;

    public Agendamento() {
        this.id = java.util.UUID.randomUUID().toString();
        this.status = "confirmado";
        this.timestamp = System.currentTimeMillis();
    }

    public Agendamento(String emailUsuario, String nomeUsuario, LocalDate data, LocalTime horario, String descricao) {
        this.id = java.util.UUID.randomUUID().toString();
        this.emailUsuario = emailUsuario;
        this.nomeUsuario = nomeUsuario;
        this.data = data;
        this.horario = horario;
        this.descricao = descricao;
        this.status = "confirmado";
        this.timestamp = System.currentTimeMillis();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id='" + id + '\'' +
                ", emailUsuario='" + emailUsuario + '\'' +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", data=" + data +
                ", horario=" + horario +
                ", descricao='" + descricao + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
