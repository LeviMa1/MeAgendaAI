package br.com.levima.agenda.service;

import br.com.levima.agenda.model.Contato;
import br.com.levima.agenda.repository.ContatoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final PasswordEncoder passwordEncoder;

    public ContatoService(ContatoRepository contatoRepository, PasswordEncoder passwordEncoder) {
        this.contatoRepository = contatoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Contato> buscarPorEmail(String email) {
        return contatoRepository.findByEmail(email);
    }

    public boolean emailJaCadastrado(String email) {
        return contatoRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public Contato salvar(Contato contato) {
        return contatoRepository.save(contato);
    }

    @Transactional
    public Contato criarContato(String nome, String email, String celular, String senhaPlana) {
        Contato contato = new Contato(nome, email, celular, passwordEncoder.encode(senhaPlana));
        return contatoRepository.save(contato);
    }

    @Transactional
    public void atualizarPerfil(String email, String nome, String celular) {
        Contato contato = contatoRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));
        contato.setNome(nome);
        contato.setCelular(celular);
        contatoRepository.save(contato);
    }

    @Transactional
    public void alterarSenha(String email, String novaSenha) {
        Contato contato = contatoRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));
        contato.setSenha(passwordEncoder.encode(novaSenha));
        contatoRepository.save(contato);
    }

    public Page<Contato> listarTodos(Pageable pageable) {
        return contatoRepository.findAll(pageable);
    }
}
