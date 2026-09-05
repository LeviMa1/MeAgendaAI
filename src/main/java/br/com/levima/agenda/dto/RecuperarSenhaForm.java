package br.com.levima.agenda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RecuperarSenhaForm {

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    private String email;

    @Size(min = 6, max = 100, message = "Senha deve ter no minimo 6 caracteres")
    private String novaSenha;

    @Size(min = 6, max = 100)
    private String confirmaSenha;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
    public String getConfirmaSenha() { return confirmaSenha; }
    public void setConfirmaSenha(String confirmaSenha) { this.confirmaSenha = confirmaSenha; }
}
