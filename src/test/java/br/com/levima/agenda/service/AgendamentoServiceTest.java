package br.com.levima.agenda.service;

import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.model.StatusAgendamento;
import br.com.levima.agenda.repository.AgendamentoRepository;
import br.com.levima.agenda.repository.DisponibilidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class AgendamentoServiceTest {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    @BeforeEach
    void setUp() {
        agendamentoRepository.deleteAll();
        disponibilidadeRepository.deleteAll();
        LocalDate data = LocalDate.now().plusDays(2);
        adminService.gerarHorariosAutomaticos(List.of(data.toString()), 8, 17, 60);
    }

    @Test
    void deveCriarAgendamentoComStatusPendente() {
        LocalDate data = LocalDate.now().plusDays(2);
        Agendamento ag = agendamentoService.criarAgendamento(
                "teste@email.com", "Teste", data, LocalTime.of(9, 0), "relaxamento-muscular", "Consulta");
        assertNotNull(ag);
        assertEquals(StatusAgendamento.PENDENTE, ag.getStatus());
        assertEquals("Relaxamento Muscular", ag.getServicoNome());
    }

    @Test
    void deveImpedirHorarioDuplicado() {
        LocalDate data = LocalDate.now().plusDays(2);
        agendamentoService.criarAgendamento("a@email.com", "A", data, LocalTime.of(10, 0), "relaxamento-muscular", "");
        assertThrows(IllegalStateException.class, () ->
                agendamentoService.criarAgendamento("b@email.com", "B", data, LocalTime.of(10, 0), "alivio-tencoes", ""));
    }

    @Test
    void deveRejeitarDataSemDisponibilidadeAdmin() {
        LocalDate data = LocalDate.now().plusDays(10);
        assertThrows(IllegalArgumentException.class, () ->
                agendamentoService.criarAgendamento("a@email.com", "A", data, LocalTime.of(9, 0), "relaxamento-muscular", ""));
    }

    @Test
    void deveCancelarAgendamento() {
        LocalDate data = LocalDate.now().plusDays(2);
        Agendamento ag = agendamentoService.criarAgendamento(
                "a@email.com", "A", data, LocalTime.of(11, 0), "relaxamento-muscular", "");
        assertTrue(agendamentoService.cancelarAgendamento(ag.getId()));
        assertEquals(StatusAgendamento.CANCELADO,
                agendamentoRepository.findById(ag.getId()).orElseThrow().getStatus());
    }
}
