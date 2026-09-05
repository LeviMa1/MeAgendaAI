package br.com.levima.agenda.security;

import br.com.levima.agenda.repository.ContatoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final ContatoRepository contatoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPasswordPlain;

    private String adminPasswordHash;

    public AppAuthenticationProvider(ContatoRepository contatoRepository, PasswordEncoder passwordEncoder) {
        this.contatoRepository = contatoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    void initAdminPassword() {
        if (adminPasswordPlain != null && !adminPasswordPlain.isBlank()) {
            if (adminPasswordPlain.startsWith("$2a$") || adminPasswordPlain.startsWith("$2b$")) {
                adminPasswordHash = adminPasswordPlain;
            } else {
                adminPasswordHash = passwordEncoder.encode(adminPasswordPlain);
            }
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String presented = authentication.getCredentials().toString();
        if (!passwordEncoder.matches(presented, userDetails.getPassword())) {
            throw new BadCredentialsException("Credenciais invalidas");
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        if (adminUsername.equals(username)) {
            if (adminPasswordHash == null || adminPasswordHash.isBlank()) {
                throw new BadCredentialsException("Admin nao configurado");
            }
            return new AppUserDetails(adminUsername, adminPasswordHash, "Administrador", null, "ROLE_ADMIN");
        }

        return contatoRepository.findByEmail(username)
                .map(contato -> new AppUserDetails(
                        contato.getEmail(),
                        contato.getSenha(),
                        contato.getNome(),
                        contato.getCelular(),
                        "ROLE_USER"))
                .orElseThrow(() -> new BadCredentialsException("Usuario nao encontrado"));
    }
}
