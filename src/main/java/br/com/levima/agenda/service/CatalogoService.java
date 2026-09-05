package br.com.levima.agenda.service;

import br.com.levima.agenda.model.CatalogoData;
import br.com.levima.agenda.model.CatalogoServico;
import tools.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogoService {

    private CatalogoData catalogo;

    @PostConstruct
    void carregar() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        catalogo = mapper.readValue(
                new ClassPathResource("catalogo/servicos.json").getInputStream(),
                CatalogoData.class);
    }

    public CatalogoData getCatalogo() {
        return catalogo;
    }

    public List<CatalogoServico> listarServicos() {
        return catalogo.getServicos();
    }

    public Optional<CatalogoServico> buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return catalogo.getServicos().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public CatalogoServico validarServico(String id) {
        return buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Servico selecionado invalido."));
    }
}
