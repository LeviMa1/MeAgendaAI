package br.com.levima.agenda.controller;

import br.com.levima.agenda.model.Contato;
import br.com.levima.agenda.model.CadastroTemporario;
import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.repository.ContatoRepository;
import br.com.levima.agenda.service.VerificacaoService;
import br.com.levima.agenda.service.AgendamentoService;
import br.com.levima.agenda.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
public class HomeController {

    @Autowired
    private VerificacaoService verificacaoService;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Cadastros temporários durante o fluxo de verificação (apenas em memória)
    private static Map<String, CadastroTemporario> cadastrosTemporarios = new HashMap<>();

    // ================ ROTAS BASE ================

    @GetMapping("/")
    public String home(HttpSession session) {
        String emailUsuario = (String) session.getAttribute("email");
        if (emailUsuario != null) return "redirect:/agenda";
        return "redirect:/login";
    }

    // ================ LOGIN / LOGOUT ================

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String realizarLogin(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session,
            Model model) {

        try {
            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("erro", "Email/usuário é obrigatório");
                return "login";
            }
            if (senha == null || senha.trim().isEmpty()) {
                model.addAttribute("erro", "Senha é obrigatória");
                return "login";
            }

            // Verificar se é login de admin
            if (adminService.validarLogin(email.trim(), senha, session)) {
                return "redirect:/admin";
            }

            // Buscar contato pelo email
            Contato contatoEncontrado = contatoRepository.findByEmail(email.trim()).orElse(null);

            if (contatoEncontrado == null || !passwordEncoder.matches(senha, contatoEncontrado.getSenha())) {
                model.addAttribute("erro", "Email ou senha incorretos");
                return "login";
            }

            // Armazenar usuário na sessão HTTP (isolado por browser)
            session.setAttribute("email", contatoEncontrado.getEmail());
            session.setAttribute("nome", contatoEncontrado.getNome());

            return "redirect:/agenda";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao realizar login: " + e.getMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ================ CADASTRO ================

    @GetMapping("/cadastro")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("contato", new Contato());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String salvarContato(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmaSenha,
            Model model) {

        try {
            if (nome == null || nome.trim().isEmpty()) {
                model.addAttribute("erro", "Nome é obrigatório");
                return "cadastro";
            }
            if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                model.addAttribute("erro", "Email inválido");
                return "cadastro";
            }
            if (senha == null || senha.trim().isEmpty()) {
                model.addAttribute("erro", "Senha é obrigatória");
                return "cadastro";
            }
            if (confirmaSenha == null || confirmaSenha.trim().isEmpty()) {
                model.addAttribute("erro", "Confirmação de senha é obrigatória");
                return "cadastro";
            }
            if (!senha.equals(confirmaSenha)) {
                model.addAttribute("erro", "As senhas não conferem! Por favor, tente novamente.");
                return "cadastro";
            }
            if (senha.length() < 6) {
                model.addAttribute("erro", "A senha deve ter no mínimo 6 caracteres!");
                return "cadastro";
            }

            // Verificar se email já cadastrado
            if (contatoRepository.findByEmail(email.trim()).isPresent()) {
                model.addAttribute("erro", "Este email já está cadastrado.");
                return "cadastro";
            }

            // Guardar temporariamente (sem salvar no banco ainda)
            CadastroTemporario cadastroTemp = new CadastroTemporario(nome.trim(), email.trim(), "", senha, "");
            cadastrosTemporarios.put(email.trim(), cadastroTemp);

            return "redirect:/pedir-celular?email=" + URLEncoder.encode(email.trim(), StandardCharsets.UTF_8);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao processar cadastro: " + e.getMessage());
            return "cadastro";
        }
    }

    // ================ VERIFICAÇÃO DE CELULAR ================

    @GetMapping("/pedir-celular")
    public String mostrarTelaPedirCelular(@RequestParam String email, Model model) {
        CadastroTemporario cadastroTemp = cadastrosTemporarios.get(email);
        if (cadastroTemp == null) return "redirect:/cadastro";
        model.addAttribute("email", email);
        model.addAttribute("nome", cadastroTemp.getNome());
        return "pedirCelular";
    }

    @PostMapping("/pedir-celular")
    public String salvarCelular(
            @RequestParam String email,
            @RequestParam String celular,
            Model model) {

        try {
            CadastroTemporario cadastroTemp = cadastrosTemporarios.get(email);
            if (cadastroTemp == null) {
                model.addAttribute("erro", "Sessão expirada. Faça o cadastro novamente.");
                return "redirect:/cadastro";
            }
            if (celular == null || celular.trim().isEmpty()) {
                model.addAttribute("erro", "Celular é obrigatório");
                model.addAttribute("email", email);
                model.addAttribute("nome", cadastroTemp.getNome());
                return "pedirCelular";
            }

            String celularLimpo = celular.replaceAll("[^0-9]", "");
            if (celularLimpo.length() != 11) {
                model.addAttribute("erro", "Celular deve ter 11 dígitos (DDD + número)");
                model.addAttribute("email", email);
                model.addAttribute("nome", cadastroTemp.getNome());
                return "pedirCelular";
            }

            String codigoVerificacao = String.format("%04d", new Random().nextInt(10000));
            cadastroTemp.setCelular(celular);
            cadastroTemp.setCodigoVerificacao(codigoVerificacao);
            cadastroTemp.setTimestampCodigo(System.currentTimeMillis());

            boolean envioSucesso = verificacaoService.enviarCodigoVerificacao(email, celularLimpo, codigoVerificacao);
            if (!envioSucesso) {
                model.addAttribute("erro", "Erro ao enviar código de verificação. Tente novamente.");
                model.addAttribute("email", email);
                model.addAttribute("nome", cadastroTemp.getNome());
                return "pedirCelular";
            }

            return "redirect:/verificar-codigo?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao processar celular: " + e.getMessage());
            return "pedirCelular";
        }
    }

    @GetMapping("/verificar-codigo")
    public String mostrarTelaVerificacao(@RequestParam String email, Model model) {
        CadastroTemporario cadastroTemp = cadastrosTemporarios.get(email);
        if (cadastroTemp == null) return "redirect:/cadastro";

        String celular = cadastroTemp.getCelular();
        String celularMascarado = celular.replaceAll("\\D", "");
        if (celularMascarado.length() == 11) {
            celularMascarado = "(" + celularMascarado.substring(0, 2) + ") " +
                    celularMascarado.substring(2, 7) + "-" + celularMascarado.substring(7);
        }

        model.addAttribute("celularMascarado", celularMascarado);
        model.addAttribute("email", email);
        return "verificarCodigo";
    }

    @PostMapping("/verificar-codigo")
    public String verificarCodigo(
            @RequestParam String codigo,
            @RequestParam(required = false) String email,
            Model model) {

        try {
            if (email == null || email.isEmpty()) {
                model.addAttribute("erro", "Erro: email não informado");
                return "verificarCodigo";
            }

            CadastroTemporario cadastroTemp = cadastrosTemporarios.get(email);
            if (cadastroTemp == null) {
                model.addAttribute("erro", "Sessão expirada. Faça o cadastro novamente.");
                return "redirect:/cadastro";
            }

            if (!cadastroTemp.isCodigoValido()) {
                model.addAttribute("erro", "Código expirado. Faça o cadastro novamente.");
                cadastrosTemporarios.remove(email);
                return "redirect:/cadastro";
            }

            if (!codigo.equals(cadastroTemp.getCodigoVerificacao())) {
                model.addAttribute("erro", "Código incorreto. Tente novamente.");
                model.addAttribute("celularMascarado", cadastroTemp.getCelular());
                model.addAttribute("email", email);
                return "verificarCodigo";
            }

            // Código correto — salvar no banco com senha criptografada
            String senhaCriptografada = passwordEncoder.encode(cadastroTemp.getSenha());
            Contato contato = new Contato(
                    cadastroTemp.getNome(),
                    cadastroTemp.getEmail(),
                    cadastroTemp.getCelular(),
                    senhaCriptografada);
            contatoRepository.save(contato);
            cadastrosTemporarios.remove(email);

            model.addAttribute("sucesso", true);
            model.addAttribute("primeiroNome", contato.getNome().split(" ")[0]);
            return "cadastroSucesso";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao verificar código: " + e.getMessage());
            return "verificarCodigo";
        }
    }

    // ================ CONTATOS (Admin) ================

    @GetMapping("/contatos")
    public String listarContatos(HttpSession session, Model model) {
        if (!adminService.estaLogado(session)) return "redirect:/login";
        model.addAttribute("contatos", contatoRepository.findAll());
        return "listaContatos";
    }

    // ================ AGENDAMENTOS (Usuário) ================

    @GetMapping("/agenda")
    public String mostrarAgenda(HttpSession session, Model model) {
        String emailUsuario = (String) session.getAttribute("email");
        if (emailUsuario == null) return "redirect:/login";

        List<Agendamento> agendamentos = agendamentoService.getAgendamentosUsuario(emailUsuario);
        model.addAttribute("agendamentos", agendamentos);
        model.addAttribute("nomeUsuario", session.getAttribute("nome"));
        return "agenda";
    }

    @GetMapping("/agendar")
    public String mostrarFormularioAgendamento(HttpSession session, Model model) {
        String emailUsuario = (String) session.getAttribute("email");
        if (emailUsuario == null) return "redirect:/login";

        model.addAttribute("nomeUsuario", session.getAttribute("nome"));
        model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
        model.addAttribute("diasDisponiveis", adminService.getDiasDisponiveis());
        return "agendar";
    }

    @PostMapping("/agendar")
    public String criarAgendamento(
            @RequestParam String data,
            @RequestParam String horario,
            @RequestParam(required = false) String descricao,
            HttpSession session,
            Model model) {

        try {
            String emailUsuario = (String) session.getAttribute("email");
            String nomeUsuario = (String) session.getAttribute("nome");
            if (emailUsuario == null) return "redirect:/login";

            if (data == null || data.trim().isEmpty()) {
                model.addAttribute("erro", "Data é obrigatória");
                model.addAttribute("nomeUsuario", nomeUsuario);
                model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
                return "agendar";
            }
            if (horario == null || horario.trim().isEmpty()) {
                model.addAttribute("erro", "Horário é obrigatório");
                model.addAttribute("nomeUsuario", nomeUsuario);
                model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
                return "agendar";
            }

            LocalDate dataAgendamento = LocalDate.parse(data);
            LocalTime horarioAgendamento = LocalTime.parse(horario);

            if (!dataAgendamento.isAfter(LocalDate.now())) {
                model.addAttribute("erro", "A data deve ser futura");
                model.addAttribute("nomeUsuario", nomeUsuario);
                model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
                return "agendar";
            }

            Agendamento agendamento = agendamentoService.criarAgendamento(
                    emailUsuario, nomeUsuario, dataAgendamento, horarioAgendamento,
                    descricao != null ? descricao : "");

            if (agendamento == null) {
                model.addAttribute("erro", "Esse horário já está ocupado. Escolha outro.");
                model.addAttribute("nomeUsuario", nomeUsuario);
                model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
                return "agendar";
            }

            model.addAttribute("sucesso", true);
            model.addAttribute("nomeUsuario", nomeUsuario);
            return "agendoSucesso";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao criar agendamento: " + e.getMessage());
            model.addAttribute("nomeUsuario", session.getAttribute("nome"));
            model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
            return "agendar";
        }
    }

    @PostMapping("/cancelar-agendamento")
    public String cancelarAgendamento(@RequestParam String id, HttpSession session) {
        String emailUsuario = (String) session.getAttribute("email");
        if (emailUsuario == null) return "redirect:/login";

        Agendamento agendamento = agendamentoService.getAgendamentoPorId(id);
        if (agendamento != null && agendamento.getEmailUsuario().equals(emailUsuario)) {
            agendamentoService.cancelarAgendamento(id);
        }
        return "redirect:/agenda";
    }

    // ================ API HORÁRIOS ================

    @GetMapping("/api/datas-disponiveis")
    public org.springframework.http.ResponseEntity<?> getDatasDisponiveis() {
        try {
            List<String> datas = adminService.getDiasDisponiveis()
                    .stream()
                    .map(LocalDate::toString)
                    .collect(java.util.stream.Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("datas", datas);
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.ok(Map.of("datas", new ArrayList<>()));
        }
    }

    @GetMapping("/api/horarios-disponiveis")
    public org.springframework.http.ResponseEntity<?> getHorariosDisponiveisPorData(@RequestParam String data) {
        try {
            LocalDate dataAgendamento = LocalDate.parse(data);
            List<String> horarios = agendamentoService.getHorariosDisponiveis(dataAgendamento);
            Map<String, Object> response = new HashMap<>();
            response.put("disponivel", !horarios.isEmpty());
            response.put("horarios", horarios);
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.ok(Map.of("disponivel", false, "horarios", new ArrayList<>()));
        }
    }

    // ================ ADMIN ================

    @GetMapping("/admin")
    public String mostrarPainelAdmin(HttpSession session, Model model) {
        if (!adminService.estaLogado(session)) return "redirect:/login";

        model.addAttribute("agendamentos", agendamentoService.getTodosAgendamentos());
        model.addAttribute("diasDisponiveis", adminService.getDiasDisponiveis());
        model.addAttribute("horariosDisponiveisPorData", adminService.getHorariosDisponiveisPorData());
        return "admin";
    }

    @GetMapping("/admin/contatos")
    public String verContatosAdmin(HttpSession session, Model model) {
        if (!adminService.estaLogado(session)) return "redirect:/login";
        model.addAttribute("contatos", contatoRepository.findAll());
        return "listaContatos";
    }

    @PostMapping("/admin/logout")
    public String logoutAdmin(HttpSession session) {
        adminService.logout(session);
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/admin/gerar-horarios")
    public String gerarHorariosAutomaticos(
            @RequestParam(value = "datas[]", required = false) List<String> datas,
            @RequestParam int horaInicio,
            @RequestParam int horaFim,
            @RequestParam int intervalo,
            HttpSession session,
            Model model) {

        if (!adminService.estaLogado(session)) return "redirect:/login";

        if (datas != null && !datas.isEmpty()) {
            adminService.gerarHorariosAutomaticos(datas, horaInicio, horaFim, intervalo);
        }
        return "redirect:/admin";
    }
}

