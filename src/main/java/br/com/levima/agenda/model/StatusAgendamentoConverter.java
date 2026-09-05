package br.com.levima.agenda.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusAgendamentoConverter implements AttributeConverter<StatusAgendamento, String> {

    @Override
    public String convertToDatabaseColumn(StatusAgendamento attribute) {
        return attribute != null ? attribute.getValor() : null;
    }

    @Override
    public StatusAgendamento convertToEntityAttribute(String dbData) {
        return StatusAgendamento.fromValor(dbData);
    }
}
