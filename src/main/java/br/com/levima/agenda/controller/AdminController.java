package br.com.levima.agenda.controller;

import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.model.Contato;
import br.com.levima.agenda.model.StatusAgendamento;
import br.com.levima.agenda.service.AdminService;
import br.com.levima.agenda.service.AgendamentoService;
import br.com.levima.agenda.service.ContatoService;
import br.com.levima.agenda.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final AgendamentoService agendamentoService;
    private final ContatoService contatoService;
    private final ExcelExportService excelExportService;

    public AdminController(AdminService adminService,
                           AgendamentoService agendamentoService,
                           ContatoService contatoService,
                           ExcelExportService excelExportService) {
        this.adminService = adminService;
        this.agendamentoService = agendamentoService;
        this.contatoService = contatoService;
        this.excelExportService = excelExportService;
    }

    @GetMapping
    public String painel(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Agendamento> agendamentos = agendamentoService.getTodosAgendamentos(PageRequest.of(page, 15));

        model.addAttribute("agendamentos", agendamentos.getContent());
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", agendamentos.getTotalPages());
        model.addAttribute("diasDisponiveis", adminService.getDiasDisponiveis());
        model.addAttribute("horariosDisponiveisPorData", adminService.getHorariosDisponiveisPorData());
        model.addAttribute("totalConfirmados", agendamentoService.contarPorStatus(StatusAgendamento.CONFIRMADO));
        model.addAttribute("totalPendentes", agendamentoService.contarPorStatus(StatusAgendamento.PENDENTE));
        model.addAttribute("totalCancelados", agendamentoService.contarPorStatus(StatusAgendamento.CANCELADO));
        return "admin";
    }

    @GetMapping("/contatos")
    public String contatos(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Contato> contatos = contatoService.listarTodos(PageRequest.of(page, 20));
        model.addAttribute("contatos", contatos.getContent());
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", contatos.getTotalPages());
        return "listaContatos";
    }

    @PostMapping("/gerar-horarios")
    public String gerarHorarios(@RequestParam(value = "datas[]", required = false) List<String> datas,
                                @RequestParam int horaInicio,
                                @RequestParam int horaFim,
                                @RequestParam int intervalo,
                                RedirectAttributes redirect) {
        if (datas != null && !datas.isEmpty()) {
            adminService.gerarHorariosAutomaticos(datas, horaInicio, horaFim, intervalo);
            redirect.addFlashAttribute("sucesso", true);
            redirect.addFlashAttribute("mensagem", "Horarios gerados com sucesso!");
        }
        return "redirect:/admin";
    }

    @PostMapping("/remover-data")
    public String removerData(@RequestParam String data, RedirectAttributes redirect) {
        try {
            adminService.removerDataDisponivel(data);
            redirect.addFlashAttribute("sucesso", true);
            redirect.addFlashAttribute("mensagem", "Data removida com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/agendamento/confirmar")
    public String confirmarAgendamento(@RequestParam String id, RedirectAttributes redirect) {
        try {
            if (agendamentoService.confirmarAgendamento(id)) {
                redirect.addFlashAttribute("sucesso", true);
                redirect.addFlashAttribute("mensagem", "Agendamento confirmado!");
            } else {
                redirect.addFlashAttribute("erro", "Nao foi possivel confirmar o agendamento.");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/agendamento/rejeitar")
    public String rejeitarAgendamento(@RequestParam String id, RedirectAttributes redirect) {
        if (agendamentoService.rejeitarAgendamento(id)) {
            redirect.addFlashAttribute("sucesso", true);
            redirect.addFlashAttribute("mensagem", "Agendamento rejeitado.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/agendamento/remover")
    public String removerAgendamento(@RequestParam String id, RedirectAttributes redirect) {
        agendamentoService.removerAgendamento(id);
        redirect.addFlashAttribute("sucesso", true);
        redirect.addFlashAttribute("mensagem", "Agendamento removido.");
        return "redirect:/admin";
    }

    @GetMapping("/export/agendamentos.xlsx")
    public void exportarAgendamentos(HttpServletResponse response) throws IOException {
        List<Agendamento> agendamentos = agendamentoService.getTodosAgendamentos();
        excelExportService.exportarAgendamentos(agendamentos, response);
    }

    @GetMapping("/export/contatos.xlsx")
    public void exportarContatos(HttpServletResponse response) throws IOException {
        List<Contato> contatos = contatoService.listarTodos(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        excelExportService.exportarContatos(contatos, response);
    }
}
