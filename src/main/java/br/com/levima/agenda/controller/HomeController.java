package br.com.levima.agenda.controller;

import br.com.levima.agenda.model.Contato;
import br.com.levima.agenda.model.CadastroTemporario;
import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.service.VerificacaoService;
import br.com.levima.agenda.service.AgendamentoService;
import br.com.levima.agenda.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
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

    // Lista temporária para armazenar contatos (em produção, usar banco de dados)
    private static List<Contato> contatos = new ArrayList<>();

    // Mapa para armazenar cadastros temporários durante verificação
    private static Map<String, CadastroTemporario> cadastrosTemporarios = new HashMap<>();

    // Variável para armazenar o usuário logado
    private static Map<String, String> usuariosLogados = new HashMap<>();

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String realizarLogin(
            @RequestParam String email,
            @RequestParam String senha,
            Model model) {

        try {
            // Validações básicas
            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("erro", "Email é obrigatório");
                return "login";
            }
            if (senha == null || senha.trim().isEmpty()) {
                model.addAttribute("erro", "Senha é obrigatória");
                return "login";
            }

            // Verificar se é login de admin
            if (adminService.validarLogin(email, senha)) {
                model.addAttribute("isAdmin", true);
                return "redirect:/admin";
            }

            // Buscar contato com email e senha correspondentes
            Contato contatoEncontrado = contatos.stream()
                    .filter(c -> c.getEmail().equals(email) && c.getSenha().equals(senha))
                    .findFirst()
                    .orElse(null);

            if (contatoEncontrado == null) {
                model.addAttribute("erro", "Email ou senha incorretos");
                return "login";
            }

            // Extrair primeiro nome
            String nomeCompleto = contatoEncontrado.getNome();
            String primeiroNome = nomeCompleto.split(" ")[0];

            // Armazenar usuário logado
            usuariosLogados.put("email", email);
            usuariosLogados.put("nome", nomeCompleto);

            // Login bem-sucedido - redirecionar direto para agendamentos
            return "redirect:/agenda";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao realizar login: " + e.getMessage());
            return "login";
        }
    }

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
            // Validações básicas
            if (nome == null || nome.trim().isEmpty()) {
                model.addAttribute("erro", "Nome é obrigatório");
                return "cadastro";
            }
            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("erro", "Email é obrigatório");
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

            // Validar formato de email simples
            if (!email.contains("@")) {
                model.addAttribute("erro", "Email inválido");
                return "cadastro";
            }

            // Validar se as senhas são iguais
            if (!senha.equals(confirmaSenha)) {
                model.addAttribute("erro", "As senhas não conferem! Por favor, tente novamente.");
                return "cadastro";
            }

            // Validar comprimento mínimo da senha
            if (senha.length() < 6) {
                model.addAttribute("erro", "A senha deve ter no mínimo 6 caracteres!");
                return "cadastro";
            }

            // Armazenar cadastro temporário sem celular ainda
            CadastroTemporario cadastroTemp = new CadastroTemporario(nome, email, "", senha, "");
            cadastrosTemporarios.put(email, cadastroTemp);

            // Redirecionar para tela de pedir celular
            return "redirect:/pedir-celular?email=" + email;

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar contato: " + e.getMessage());
            return "cadastro";
        }
    }

    @GetMapping("/pedir-celular")
    public String mostrarTelaPedirCelular(
            @RequestParam String email,
            Model model) {

        CadastroTemporario cadastroTemp = cadastrosTemporarios.get(email);

        if (cadastroTemp == null) {
            return "redirect:/cadastro";
        }

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
            if (email == null || email.isEmpty()) {
                model.addAttribute("erro", "Erro: email não informado");
                return "pedirCelular";
            }

            CadastroTemporario cadastroTemp = cadastrosTemporarios.get(email);

            if (cadastroTemp == null) {
                model.addAttribute("erro", "Sessão expirada. Por favor, faça o cadastro novamente.");
                return "redirect:/cadastro";
            }

            if (celular == null || celular.trim().isEmpty()) {
                model.addAttribute("erro", "Celular é obrigatório");
                model.addAttribute("email", email);
                model.addAttribute("nome", cadastroTemp.getNome());
                return "pedirCelular";
            }

            // Validar celular (remover caracteres especiais)
            String celularLimpo = celular.replaceAll("[^0-9]", "");
            if (celularLimpo.length() != 11) {
                model.addAttribute("erro", "Celular deve ter 11 dígitos (DDD + número)");
                model.addAttribute("email", email);
                model.addAttribute("nome", cadastroTemp.getNome());
                return "pedirCelular";
            }

            // Gerar código de verificação aleatório (4 dígitos)
            String codigoVerificacao = String.format("%04d", new Random().nextInt(10000));

            // Atualizar cadastro temporário com celular e código
            cadastroTemp.setCelular(celular);
            cadastroTemp.setCodigoVerificacao(codigoVerificacao);
            cadastroTemp.setTimestampCodigo(System.currentTimeMillis());

            // Enviar email com o código
            boolean envioSucesso = verificacaoService.enviarCodigoVerificacao(email, celularLimpo, codigoVerificacao);

            if (!envioSucesso) {
                model.addAttribute("erro", "Erro ao enviar email de verificação. Tente novamente em alguns momentos.");
                model.addAttribute("email", email);
                model.addAttribute("nome", cadastroTemp.getNome());
                return "pedirCelular";
            }

            return "redirect:/verificar-codigo?email=" + email;

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao processar celular: " + e.getMessage());
            return "pedirCelular";
        }
    }

    @GetMapping("/verificar-codigo")
    public String mostrarTelaVerificacao(
            @RequestParam String email,
            Model model) {

        CadastroTemporario cadastroTemp = cadastrosTemporarios.get(email);

        if (cadastroTemp == null) {
            return "redirect:/cadastro";
        }

        // Formatar celular para exibição
        String celular = cadastroTemp.getCelular();
        String celularMascarado = celular.replaceAll("\\D", "");
        if (celularMascarado.length() == 11) {
            celularMascarado = "(" + celularMascarado.substring(0, 2) + ") " +
                             celularMascarado.substring(2, 7) + "-" +
                             celularMascarado.substring(7);
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
                model.addAttribute("erro", "Sessão expirada. Por favor, faça o cadastro novamente.");
                return "redirect:/cadastro";
            }

            // Validar se o código não expirou
            if (!cadastroTemp.isCodigoValido()) {
                model.addAttribute("erro", "Código expirado. Por favor, faça o cadastro novamente.");
                cadastrosTemporarios.remove(email);
                return "redirect:/cadastro";
            }

            // Validar código
            if (!codigo.equals(cadastroTemp.getCodigoVerificacao())) {
                model.addAttribute("erro", "Código incorreto. Tente novamente.");
                model.addAttribute("celularMascarado", cadastroTemp.getCelular());
                return "verificarCodigo";
            }

            // Código correto - salvar contato
            Contato contato = new Contato(
                    cadastroTemp.getNome(),
                    cadastroTemp.getEmail(),
                    cadastroTemp.getCelular(),
                    cadastroTemp.getSenha()
            );
            contatos.add(contato);

            // Limpar dados temporários
            cadastrosTemporarios.remove(email);

            // Redirecionar para login com mensagem de sucesso
            model.addAttribute("sucesso", true);
            model.addAttribute("primeiroNome", contato.getNome().split(" ")[0]);

            return "cadastroSucesso";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao verificar código: " + e.getMessage());
            return "verificarCodigo";
        }
    }

    @GetMapping("/contatos")
    public String listarContatos(Model model) {
        model.addAttribute("contatos", contatos);
        return "listaContatos";
    }

    // ================ ROTAS DE AGENDAMENTO ================

    @GetMapping("/agenda")
    public String mostrarAgenda(Model model) {
        String emailUsuario = usuariosLogados.get("email");

        if (emailUsuario == null) {
            return "redirect:/login";
        }

        List<Agendamento> agendamentos = agendamentoService.getAgendamentosUsuario(emailUsuario);
        model.addAttribute("agendamentos", agendamentos);
        model.addAttribute("nomeUsuario", usuariosLogados.get("nome"));

        return "agenda";
    }

    @GetMapping("/agendar")
    public String mostrarFormularioAgendamento(Model model) {
        String emailUsuario = usuariosLogados.get("email");

        if (emailUsuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("nomeUsuario", usuariosLogados.get("nome"));
        model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
        model.addAttribute("diasDisponiveis", adminService.getDiasDisponiveis());

        return "agendar";
    }

    @PostMapping("/agendar")
    public String criarAgendamento(
            @RequestParam String data,
            @RequestParam String horario,
            @RequestParam String descricao,
            Model model) {

        try {
            String emailUsuario = usuariosLogados.get("email");
            String nomeUsuario = usuariosLogados.get("nome");

            if (emailUsuario == null) {
                return "redirect:/login";
            }

            // Validações
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

            // Validar se a data é futura
            if (dataAgendamento.isBefore(LocalDate.now())) {
                model.addAttribute("erro", "A data deve ser futura");
                model.addAttribute("nomeUsuario", nomeUsuario);
                model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
                return "agendar";
            }

            // Criar agendamento
            Agendamento agendamento = agendamentoService.criarAgendamento(
                    emailUsuario, nomeUsuario, dataAgendamento, horarioAgendamento,
                    descricao != null ? descricao : "");

            if (agendamento == null) {
                model.addAttribute("erro", "Esse horário já está ocupado. Por favor, escolha outro.");
                model.addAttribute("nomeUsuario", nomeUsuario);
                model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
                return "agendar";
            }

            model.addAttribute("sucesso", true);
            model.addAttribute("nomeUsuario", nomeUsuario);
            return "agendoSucesso";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao criar agendamento: " + e.getMessage());
            model.addAttribute("nomeUsuario", usuariosLogados.get("nome"));
            model.addAttribute("dataMinima", LocalDate.now().plusDays(1).toString());
            return "agendar";
        }
    }

    @GetMapping("/api/datas-disponiveis")
    public org.springframework.http.ResponseEntity<?> getDataDisponivel() {
        try {
            java.util.Set<LocalDate> diasDisponiveis = adminService.getDiasDisponiveis();
            java.util.List<String> datas = new java.util.ArrayList<>();

            for (LocalDate data : diasDisponiveis) {
                datas.add(data.toString());
            }

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("datas", datas);

            return org.springframework.http.ResponseEntity.ok(response);

        } catch (Exception e) {
            java.util.Map<String, Object> error = new java.util.HashMap<>();
            error.put("erro", e.getMessage());

            return org.springframework.http.ResponseEntity.ok(error);
        }
    }

    @GetMapping("/api/horarios-disponiveis")
    public org.springframework.http.ResponseEntity<?> getHorariosDisponiveisPorData(
            @RequestParam String data) {

        try {
            LocalDate dataAgendamento = LocalDate.parse(data);
            List<String> horarios = adminService.getHorariosPorData(dataAgendamento);

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("disponivel", !horarios.isEmpty());
            response.put("horarios", horarios);

            return org.springframework.http.ResponseEntity.ok(response);

        } catch (Exception e) {
            java.util.Map<String, Object> error = new java.util.HashMap<>();
            error.put("disponivel", false);
            error.put("mensagem", "Erro ao buscar horários: " + e.getMessage());

            return org.springframework.http.ResponseEntity.ok(error);
        }
    }

    @PostMapping("/cancelar-agendamento")
    public String cancelarAgendamento(
            @RequestParam String id,
            Model model) {

        String emailUsuario = usuariosLogados.get("email");

        if (emailUsuario == null) {
            return "redirect:/login";
        }

        Agendamento agendamento = agendamentoService.getAgendamentoPorId(id);

        if (agendamento != null && agendamento.getEmailUsuario().equals(emailUsuario)) {
            agendamentoService.cancelarAgendamento(id);
            model.addAttribute("sucesso", true);
            model.addAttribute("mensagem", "Agendamento cancelado com sucesso!");
        } else {
            model.addAttribute("erro", "Não foi possível cancelar o agendamento");
        }

        return "redirect:/agenda";
    }

    // ================ ROTAS DE ADMINISTRADOR ================

    @GetMapping("/admin")
    public String mostrarPainelAdmin(Model model) {
        if (!adminService.estaLogado()) {
            return "redirect:/login";
        }

        // Obter todos os agendamentos
        List<Agendamento> todosAgendamentos = agendamentoService.getTodosAgendamentos();
        
        model.addAttribute("agendamentos", todosAgendamentos);
        model.addAttribute("diasDisponiveis", adminService.getDiasDisponiveis());
        model.addAttribute("horariosDisponiveisPorData", adminService.getHorariosDisponiveisPorData());

        return "admin";
    }

    @GetMapping("/admin/contatos")
    public String verContatosAdmin(Model model) {
        if (!adminService.estaLogado()) {
            return "redirect:/login";
        }

        // Obter todos os contatos cadastrados
        model.addAttribute("contatos", contatos);

        return "listaContatos";
    }

    @PostMapping("/admin/logout")
    public String logoutAdmin() {
        adminService.logout();
        return "redirect:/login";
    }

    @PostMapping("/admin/gerar-horarios")
    public String gerarHorariosAutomaticos(
            @RequestParam(value = "datas[]", required = false) List<String> datas,
            @RequestParam int horaInicio,
            @RequestParam int horaFim,
            @RequestParam int intervalo,
            Model model) {

        if (!adminService.estaLogado()) {
            return "redirect:/login";
        }

        if (datas != null && !datas.isEmpty()) {
            adminService.gerarHorariosAutomaticos(datas, horaInicio, horaFim, intervalo);
            model.addAttribute("sucesso", true);
            model.addAttribute("mensagem", "Horários gerados com sucesso!");
        } else {
            model.addAttribute("erro", "Selecione pelo menos uma data");
        }

        return "redirect:/admin";
    }
}


