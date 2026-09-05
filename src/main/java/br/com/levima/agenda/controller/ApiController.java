package br.com.levima.agenda.controller;

import br.com.levima.agenda.service.AdminService;
import br.com.levima.agenda.service.AgendamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AdminService adminService;
    private final AgendamentoService agendamentoService;

    public ApiController(AdminService adminService, AgendamentoService agendamentoService) {
        this.adminService = adminService;
        this.agendamentoService = agendamentoService;
    }

    @GetMapping("/datas-disponiveis")
    public ResponseEntity<Map<String, Object>> getDatasDisponiveis() {
        List<String> datas = adminService.getDiasDisponiveis()
                .stream()
                .map(LocalDate::toString)
                .toList();
        Map<String, Object> response = new HashMap<>();
        response.put("datas", datas);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/horarios-disponiveis")
    public ResponseEntity<Map<String, Object>> getHorariosDisponiveis(@RequestParam String data) {
        try {
            LocalDate dataAgendamento = LocalDate.parse(data);
            List<String> horarios = agendamentoService.getHorariosDisponiveis(dataAgendamento);
            Map<String, Object> response = new HashMap<>();
            response.put("disponivel", !horarios.isEmpty());
            response.put("horarios", horarios);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("disponivel", false, "horarios", new ArrayList<>()));
        }
    }
}
