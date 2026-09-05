package br.com.levima.agenda.service;

import br.com.levima.agenda.model.CadastroPendente;
import br.com.levima.agenda.repository.CadastroPendenteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
public class CadastroPendenteService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final CadastroPendenteRepository repository;

    @Value("${app.otp.max-tentativas:5}")
    private int maxTentativas;

    @Value("${app.otp.validade-minutos:10}")
    private int validadeMinutos;

    @Value("${app.otp.max-envios-por-hora:3}")
    private int maxEnviosPorHora;

    public CadastroPendenteService(CadastroPendenteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CadastroPendente salvar(CadastroPendente pendente) {
        return repository.save(pendente);
    }

    public Optional<CadastroPendente> buscarPorEmail(String email) {
        return repository.findById(email);
    }

    @Transactional
    public String gerarECodificar(CadastroPendente pendente) {
        if (!pendente.podeEnviarCodigo(maxEnviosPorHora)) {
            throw new IllegalStateException("Limite de envios de codigo atingido. Tente novamente em 1 hora.");
        }
        String codigo = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        pendente.setCodigoVerificacao(codigo);
        pendente.setTimestampCodigo(Instant.now());
        pendente.setTentativas(0);
        pendente.registrarEnvio();
        repository.save(pendente);
        return codigo;
    }

    @Transactional
    public boolean verificarCodigo(String email, String codigoInformado) {
        CadastroPendente pendente = repository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("Sessao expirada. Refaca o cadastro."));

        if (!pendente.isCodigoValido(validadeMinutos)) {
            repository.delete(pendente);
            throw new IllegalArgumentException("Codigo expirado. Refaca o cadastro.");
        }

        if (pendente.getTentativas() >= maxTentativas) {
            repository.delete(pendente);
            throw new IllegalArgumentException("Numero maximo de tentativas excedido.");
        }

        if (!pendente.getCodigoVerificacao().equals(codigoInformado.trim())) {
            pendente.incrementarTentativa();
            repository.save(pendente);
            return false;
        }
        return true;
    }

    @Transactional
    public void remover(String email) {
        repository.deleteById(email);
    }

    @Transactional
    public void limparExpirados() {
        Instant limite = Instant.now().minusSeconds(validadeMinutos * 60L * 2L);
        repository.deleteExpirados(limite);
    }

    public int getValidadeMinutos() {
        return validadeMinutos;
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void limparExpiradosAgendado() {
        limparExpirados();
    }
}
