package br.com.levima.agenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AgendamentoForm {

    @NotBlank(message = "Data e obrigatoria")
    private String data;

    @NotBlank(message = "Horario e obrigatorio")
    private String horario;

    @NotBlank(message = "Selecione um servico")
    private String servicoId;

    @Size(max = 500, message = "Descricao deve ter no maximo 500 caracteres")
    private String descricao;

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    public String getServicoId() { return servicoId; }
    public void setServicoId(String servicoId) { this.servicoId = servicoId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
