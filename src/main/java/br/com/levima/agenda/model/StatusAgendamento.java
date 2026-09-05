package br.com.levima.agenda.model;

public enum StatusAgendamento {
    CONFIRMADO("confirmado"),
    PENDENTE("pendente"),
    CANCELADO("cancelado");

    private final String valor;

    StatusAgendamento(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static StatusAgendamento fromValor(String valor) {
        if (valor == null) {
            return PENDENTE;
        }
        for (StatusAgendamento status : values()) {
            if (status.valor.equalsIgnoreCase(valor) || status.name().equalsIgnoreCase(valor)) {
                return status;
            }
        }
        return PENDENTE;
    }

    @Override
    public String toString() {
        return valor;
    }
}
