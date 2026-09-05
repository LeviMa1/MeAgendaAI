package br.com.levima.agenda.controller;

import br.com.levima.agenda.dto.AgendamentoForm;
import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.model.StatusAgendamento;
import br.com.levima.agenda.security.SecurityUtils;
import br.com.levima.agenda.service.AdminService;
import br.com.levima.agenda.service.AgendamentoService;
import br.com.levima.agenda.service.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class AgendaController {

    private final AgendamentoService agendamentoService;
    private final AdminService adminService;
    private final CatalogoService catalogoService;
    private final SecurityUtils securityUtils;

    public AgendaController(AgendamentoService agendamentoService,
                            AdminService adminService,
                            CatalogoService catalogoService,
                            SecurityUtils securityUtils) {
        this.agendamentoService = agendamentoService;
        this.adminService = adminService;
        this.catalogoService = catalogoService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/agenda")
    public String mostrarAgenda(@RequestParam(defaultValue = "0") int page, Model model) {
        String email = securityUtils.getEmailLogado();
        Page<Agendamento> agendamentos = agendamentoService.getAgendamentosUsuario(email, PageRequest.of(page, 10));
        model.addAttribute("agendamentos", agendamentos.getContent());
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", agendamentos.getTotalPages());
        model.addAttribute("nomeUsuario", securityUtils.getNomeLogado());
        return "agenda";
    }

    @GetMapping("/agendar")
    public String mostrarFormularioAgendamento(@RequestParam(required = false) String servico, Model model) {
        AgendamentoForm form = new AgendamentoForm();
        if (servico != null && !servico.isBlank()) {
            form.setServicoId(servico);
        }
        model.addAttribute("nomeUsuario", securityUtils.getNomeLogado());
        model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
        model.addAttribute("agendamentoForm", form);
        model.addAttribute("servicos", catalogoService.listarServicos());
        model.addAttribute("catalogoPdf", catalogoService.getCatalogo().getPdfUrl());
        return "agendar";
    }

    @PostMapping("/agendar")
    public String criarAgendamento(@Valid AgendamentoForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("erro", result.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("nomeUsuario", securityUtils.getNomeLogado());
            model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
            model.addAttribute("servicos", catalogoService.listarServicos());
            model.addAttribute("catalogoPdf", catalogoService.getCatalogo().getPdfUrl());
            return "agendar";
        }

        try {
            agendamentoService.criarAgendamento(
                    securityUtils.getEmailLogado(),
                    securityUtils.getNomeLogado(),
                    LocalDate.parse(form.getData()),
                    LocalTime.parse(form.getHorario()),
                    form.getServicoId(),
                    form.getDescricao() != null ? form.getDescricao() : "");
            model.addAttribute("sucesso", true);
            model.addAttribute("nomeUsuario", securityUtils.getNomeLogado());
            return "agendoSucesso";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("nomeUsuario", securityUtils.getNomeLogado());
            model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
            model.addAttribute("servicos", catalogoService.listarServicos());
            model.addAttribute("catalogoPdf", catalogoService.getCatalogo().getPdfUrl());
            return "agendar";
        }
    }

    @GetMapping("/agendar/editar")
    public String editarAgendamentoGet(@RequestParam String id, Model model) {
        Agendamento agendamento = agendamentoService.getAgendamentoPorId(id);
        if (agendamento == null || !agendamento.getEmailUsuario().equals(securityUtils.getEmailLogado())) {
            return "redirect:/agenda";
        }
        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            return "redirect:/agenda";
        }

        AgendamentoForm form = new AgendamentoForm();
        form.setData(agendamento.getData().toString());
        form.setHorario(agendamento.getHorario().toString().substring(0, 5));
        form.setServicoId(agendamento.getServicoId());
        form.setDescricao(agendamento.getDescricao());

        model.addAttribute("agendamentoId", id);
        model.addAttribute("agendamentoForm", form);
        model.addAttribute("nomeUsuario", securityUtils.getNomeLogado());
        model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
        model.addAttribute("servicos", catalogoService.listarServicos());
        model.addAttribute("catalogoPdf", catalogoService.getCatalogo().getPdfUrl());
        return "editarAgendamento";
    }

    @PostMapping("/agendar/editar")
    public String editarAgendamentoPost(@RequestParam String id,
                                        @Valid AgendamentoForm form,
                                        BindingResult result,
                                        Model model,
                                        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("erro", result.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("agendamentoId", id);
            model.addAttribute("nomeUsuario", securityUtils.getNomeLogado());
            model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
            model.addAttribute("servicos", catalogoService.listarServicos());
            model.addAttribute("catalogoPdf", catalogoService.getCatalogo().getPdfUrl());
            return "editarAgendamento";
        }

        try {
            agendamentoService.editarAgendamento(
                    id, securityUtils.getEmailLogado(),
                    LocalDate.parse(form.getData()),
                    LocalTime.parse(form.getHorario()),
                    form.getServicoId(),
                    form.getDescricao());
            redirect.addFlashAttribute("sucesso", true);
            redirect.addFlashAttribute("mensagem", "Agendamento atualizado! Aguardando confirmacao do admin.");
            return "redirect:/agenda";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("agendamentoId", id);
            model.addAttribute("nomeUsuario", securityUtils.getNomeLogado());
            model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
            model.addAttribute("servicos", catalogoService.listarServicos());
            model.addAttribute("catalogoPdf", catalogoService.getCatalogo().getPdfUrl());
            return "editarAgendamento";
        }
    }

    @PostMapping("/cancelar-agendamento")
    public String cancelarAgendamento(@RequestParam String id) {
        Agendamento agendamento = agendamentoService.getAgendamentoPorId(id);
        if (agendamento != null && agendamento.getEmailUsuario().equals(securityUtils.getEmailLogado())) {
            agendamentoService.cancelarAgendamento(id);
        }
        return "redirect:/agenda";
    }
}
