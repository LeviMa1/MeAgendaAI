package br.com.levima.agenda.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "disponibilidade_admin")
public class DisponibilidadeAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private LocalDate data;

    /**
     * Horários armazenados como string separada por vírgula (ex: "08:00,08:30,09:00").
     * Simplifica o armazenamento sem precisar de tabela extra.
     */
    @Column(name = "horarios", length = 2000)
    private String horariosStr;

    public DisponibilidadeAdmin() {}

    public DisponibilidadeAdmin(LocalDate data, List<String> horarios) {
        this.data = data;
        setHorariosList(horarios);
    }

    public Long getId() { return id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public List<String> getHorariosList() {
        if (horariosStr == null || horariosStr.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(horariosStr.split(",")));
    }

    public void setHorariosList(List<String> horarios) {
        this.horariosStr = (horarios == null || horarios.isEmpty()) ? "" : String.join(",", horarios);
    }
}

