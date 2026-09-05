package br.com.levima.agenda.model;

import java.util.ArrayList;
import java.util.List;

public class CatalogoData {

    private String titulo;
    private String subtitulo;
    private String pdfUrl;
    private List<CatalogoServico> servicos = new ArrayList<>();

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    public List<CatalogoServico> getServicos() { return servicos; }
    public void setServicos(List<CatalogoServico> servicos) { this.servicos = servicos; }
}
