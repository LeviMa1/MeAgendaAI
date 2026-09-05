package br.com.levima.agenda.controller;

import br.com.levima.agenda.service.CatalogoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) String selecionar, Model model) {
        model.addAttribute("catalogo", catalogoService.getCatalogo());
        model.addAttribute("modoSelecao", selecionar != null);
        return "catalogo";
    }
}
