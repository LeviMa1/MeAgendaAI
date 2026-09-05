package br.com.levima.agenda.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public AppUserDetails getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails user) {
            return user;
        }
        return null;
    }

    public String getEmailLogado() {
        AppUserDetails user = getUsuarioLogado();
        return user != null ? user.getUsername() : null;
    }

    public String getNomeLogado() {
        AppUserDetails user = getUsuarioLogado();
        return user != null ? user.getNome() : null;
    }

    public boolean isAdmin() {
        AppUserDetails user = getUsuarioLogado();
        return user != null && user.isAdmin();
    }
}
