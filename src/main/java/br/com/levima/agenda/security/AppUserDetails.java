package br.com.levima.agenda.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AppUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final String nome;
    private final String celular;
    private final List<GrantedAuthority> authorities;

    public AppUserDetails(String username, String password, String nome, String celular, String role) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.celular = celular;
        this.authorities = List.of(new SimpleGrantedAuthority(role));
    }

    public String getNome() {
        return nome;
    }

    public String getCelular() {
        return celular;
    }

    public boolean isAdmin() {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
