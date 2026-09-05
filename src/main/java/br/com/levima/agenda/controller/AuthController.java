package br.com.levima.agenda.controller;

import br.com.levima.agenda.dto.CadastroForm;
import br.com.levima.agenda.dto.CelularForm;
import br.com.levima.agenda.dto.RecuperarSenhaForm;
import br.com.levima.agenda.model.CadastroPendente;
import br.com.levima.agenda.service.CadastroPendenteService;
import br.com.levima.agenda.service.ContatoService;
import br.com.levima.agenda.service.VerificacaoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class AuthController {

    private final ContatoService contatoService;
    private final CadastroPendenteService cadastroPendenteService;
    private final VerificacaoService verificacaoService;

    public AuthController(ContatoService contatoService,
                          CadastroPendenteService cadastroPendenteService,
                          VerificacaoService verificacaoService) {
        this.contatoService = contatoService;
        this.cadastroPendenteService = cadastroPendenteService;
        this.verificacaoService = verificacaoService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) Boolean erro,
                        @RequestParam(required = false) Boolean logout,
                        Model model) {
        if (Boolean.TRUE.equals(erro)) {
            model.addAttribute("erro", "Email/usuario ou senha incorretos");
        }
        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute("sucesso", "Logout realizado com sucesso");
        }
        return "login";
    }

    @GetMapping("/cadastro")
    public String mostrarCadastro(Model model) {
        model.addAttribute("cadastroForm", new CadastroForm());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String salvarCadastro(@Valid CadastroForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("erro", result.getAllErrors().get(0).getDefaultMessage());
            return "cadastro";
        }
        if (!form.getSenha().equals(form.getConfirmaSenha())) {
            model.addAttribute("erro", "As senhas nao conferem.");
            return "cadastro";
        }
        if (contatoService.emailJaCadastrado(form.getEmail().trim())) {
            model.addAttribute("erro", "Este email ja esta cadastrado.");
            return "cadastro";
        }

        CadastroPendente pendente = new CadastroPendente(
                form.getNome().trim(), form.getEmail().trim(), form.getSenha(),
                CadastroPendente.TipoPendente.CADASTRO);
        cadastroPendenteService.salvar(pendente);

        return "redirect:/pedir-celular?email=" + URLEncoder.encode(form.getEmail().trim(), StandardCharsets.UTF_8);
    }

    @GetMapping("/pedir-celular")
    public String pedirCelular(@RequestParam String email, Model model) {
        return cadastroPendenteService.buscarPorEmail(email.trim())
                .map(p -> {
                    CelularForm celularForm = new CelularForm();
                    celularForm.setEmail(email.trim());
                    model.addAttribute("email", email.trim());
                    model.addAttribute("nome", p.getNome());
                    model.addAttribute("celularForm", celularForm);
                    return "pedirCelular";
                })
                .orElse("redirect:/cadastro");
    }

    @PostMapping("/pedir-celular")
    public String salvarCelular(@RequestParam String email,
                                @RequestParam String celular,
                                Model model) {
        String emailNormalizado = email != null ? email.trim() : "";
        CadastroPendente pendente = cadastroPendenteService.buscarPorEmail(emailNormalizado)
                .orElse(null);
        if (pendente == null) {
            model.addAttribute("erro", "Sessao expirada. Faca o cadastro novamente.");
            return "redirect:/cadastro";
        }

        if (celular == null || celular.trim().isEmpty()) {
            model.addAttribute("erro", "Celular e obrigatorio.");
            model.addAttribute("email", emailNormalizado);
            model.addAttribute("nome", pendente.getNome());
            model.addAttribute("celularForm", new CelularForm());
            return "pedirCelular";
        }

        String celularLimpo = celular.replaceAll("[^0-9]", "");
        if (celularLimpo.length() != 11) {
            model.addAttribute("erro", "Celular deve ter 11 digitos (DDD + numero).");
            model.addAttribute("email", emailNormalizado);
            model.addAttribute("nome", pendente.getNome());
            CelularForm celularForm = new CelularForm();
            celularForm.setEmail(emailNormalizado);
            model.addAttribute("celularForm", celularForm);
            return "pedirCelular";
        }

        try {
            pendente.setCelular(celular.trim());
            String codigo = cadastroPendenteService.gerarECodificar(pendente);
            verificacaoService.enviarCodigoVerificacao(emailNormalizado, celularLimpo, codigo);
        } catch (IllegalStateException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("email", emailNormalizado);
            model.addAttribute("nome", pendente.getNome());
            CelularForm celularForm = new CelularForm();
            celularForm.setEmail(emailNormalizado);
            model.addAttribute("celularForm", celularForm);
            return "pedirCelular";
        }

        return "redirect:/verificar-codigo?email=" + URLEncoder.encode(emailNormalizado, StandardCharsets.UTF_8);
    }

    @GetMapping("/verificar-codigo")
    public String verificarCodigoGet(@RequestParam String email, Model model) {
        return cadastroPendenteService.buscarPorEmail(email)
                .map(p -> {
                    String celularMascarado = mascararCelular(p.getCelular());
                    model.addAttribute("celularMascarado", celularMascarado);
                    model.addAttribute("email", email);
                    return "verificarCodigo";
                })
                .orElse("redirect:/cadastro");
    }

    @PostMapping("/verificar-codigo")
    public String verificarCodigoPost(@RequestParam String codigo,
                                      @RequestParam String email,
                                      Model model) {
        try {
            if (!cadastroPendenteService.verificarCodigo(email, codigo)) {
                model.addAttribute("erro", "Codigo incorreto. Tente novamente.");
                model.addAttribute("email", email);
                cadastroPendenteService.buscarPorEmail(email).ifPresent(p ->
                        model.addAttribute("celularMascarado", mascararCelular(p.getCelular())));
                return "verificarCodigo";
            }

            CadastroPendente pendente = cadastroPendenteService.buscarPorEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Sessao expirada."));

            if (pendente.getTipo() == CadastroPendente.TipoPendente.RECUPERACAO_SENHA) {
                contatoService.alterarSenha(email, pendente.getSenha());
                cadastroPendenteService.remover(email);
                model.addAttribute("sucesso", true);
                model.addAttribute("mensagem", "Senha alterada com sucesso! Faca login.");
                return "recuperarSenhaSucesso";
            }

            contatoService.criarContato(pendente.getNome(), email, pendente.getCelular(), pendente.getSenha());
            cadastroPendenteService.remover(email);
            model.addAttribute("sucesso", true);
            model.addAttribute("primeiroNome", pendente.getNome().split(" ")[0]);
            return "cadastroSucesso";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "redirect:/cadastro";
        }
    }

    @GetMapping("/recuperar-senha")
    public String recuperarSenhaGet(Model model) {
        model.addAttribute("recuperarForm", new RecuperarSenhaForm());
        return "recuperarSenha";
    }

    @PostMapping("/recuperar-senha")
    public String recuperarSenhaPost(@Valid RecuperarSenhaForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("erro", result.getAllErrors().get(0).getDefaultMessage());
            return "recuperarSenha";
        }

        var contato = contatoService.buscarPorEmail(form.getEmail().trim());
        if (contato.isEmpty()) {
            model.addAttribute("erro", "Email nao encontrado.");
            return "recuperarSenha";
        }

        if (form.getNovaSenha() == null || form.getNovaSenha().length() < 6) {
            model.addAttribute("erro", "Informe a nova senha com no minimo 6 caracteres.");
            return "recuperarSenha";
        }
        if (!form.getNovaSenha().equals(form.getConfirmaSenha())) {
            model.addAttribute("erro", "As senhas nao conferem.");
            return "recuperarSenha";
        }

        CadastroPendente pendente = new CadastroPendente(
                contato.get().getNome(), form.getEmail().trim(), form.getNovaSenha(),
                CadastroPendente.TipoPendente.RECUPERACAO_SENHA);
        pendente.setCelular(contato.get().getCelular());
        cadastroPendenteService.salvar(pendente);

        try {
            String codigo = cadastroPendenteService.gerarECodificar(pendente);
            String celular = contato.get().getCelular().replaceAll("[^0-9]", "");
            verificacaoService.enviarCodigoVerificacao(form.getEmail(), celular, codigo);
        } catch (IllegalStateException e) {
            model.addAttribute("erro", e.getMessage());
            return "recuperarSenha";
        }

        return "redirect:/verificar-codigo?email=" + URLEncoder.encode(form.getEmail().trim(), StandardCharsets.UTF_8);
    }

    private String mascararCelular(String celular) {
        if (celular == null) return "";
        String limpo = celular.replaceAll("\\D", "");
        if (limpo.length() == 11) {
            return "(" + limpo.substring(0, 2) + ") " + limpo.substring(2, 7) + "-" + limpo.substring(7);
        }
        return celular;
    }
}
