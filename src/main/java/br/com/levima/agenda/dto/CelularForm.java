package br.com.levima.agenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CelularForm {

    @NotBlank(message = "Email e obrigatorio")
    private String email;

    @NotBlank(message = "Celular e obrigatorio")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$|^\\d{11}$", message = "Celular invalido")
    private String celular;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
}
