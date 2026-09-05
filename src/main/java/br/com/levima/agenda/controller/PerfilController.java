package br.com.levima.agenda.controller;

import br.com.levima.agenda.dto.PerfilForm;
import br.com.levima.agenda.model.Contato;
import br.com.levima.agenda.security.SecurityUtils;
import br.com.levima.agenda.service.ContatoService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    private final ContatoService contatoService;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(ContatoService contatoService,
                            SecurityUtils securityUtils,
                            PasswordEncoder passwordEncoder) {
        this.contatoService = contatoService;
        this.securityUtils = securityUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Contato contato = contatoService.buscarPorEmail(securityUtils.getEmailLogado())
                .orElseThrow();
        PerfilForm form = new PerfilForm();
        form.setNome(contato.getNome());
        form.setCelular(contato.getCelular());
        model.addAttribute("perfilForm", form);
        model.addAttribute("email", contato.getEmail());
        return "perfil";
    }

    @PostMapping("/perfil")
    public String atualizarPerfil(@Valid PerfilForm form, BindingResult result, Model model,
                                  RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("erro", result.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("email", securityUtils.getEmailLogado());
            return "perfil";
        }

        contatoService.atualizarPerfil(securityUtils.getEmailLogado(), form.getNome().trim(), form.getCelular());
        redirect.addFlashAttribute("sucesso", true);
        redirect.addFlashAttribute("mensagem", "Perfil atualizado com sucesso!");
        return "redirect:/perfil";
    }

    @PostMapping("/perfil/alterar-senha")
    public String alterarSenha(@RequestParam String senhaAtual,
                               @RequestParam String novaSenha,
                               @RequestParam String confirmaSenha,
                               RedirectAttributes redirect) {
        Contato contato = contatoService.buscarPorEmail(securityUtils.getEmailLogado()).orElseThrow();

        if (!passwordEncoder.matches(senhaAtual, contato.getSenha())) {
            redirect.addFlashAttribute("erro", "Senha atual incorreta.");
            return "redirect:/perfil";
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            redirect.addFlashAttribute("erro", "Nova senha deve ter no minimo 6 caracteres.");
            return "redirect:/perfil";
        }
        if (!novaSenha.equals(confirmaSenha)) {
            redirect.addFlashAttribute("erro", "As senhas nao conferem.");
            return "redirect:/perfil";
        }

        contatoService.alterarSenha(contato.getEmail(), novaSenha);
        redirect.addFlashAttribute("sucesso", true);
        redirect.addFlashAttribute("mensagem", "Senha alterada com sucesso!");
        return "redirect:/perfil";
    }
}
