package br.com.levima.agenda.service;

import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.model.Contato;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATA_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void exportarAgendamentos(List<Agendamento> agendamentos, HttpServletResponse response) throws IOException {
        configurarResposta(response, "agendamentos.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Agendamentos");
            CellStyle headerStyle = criarEstiloCabecalho(workbook);

            String[] colunas = {"Data", "Horario", "Servico", "Cliente", "Email", "Status", "Descricao"};
            criarCabecalho(sheet, colunas, headerStyle);

            int rowNum = 1;
            for (Agendamento a : agendamentos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(a.getData().format(DATA_FORMAT));
                row.createCell(1).setCellValue(a.getHorario().toString());
                row.createCell(2).setCellValue(a.getServicoNome() != null ? a.getServicoNome() : "");
                row.createCell(3).setCellValue(a.getNomeUsuario());
                row.createCell(4).setCellValue(a.getEmailUsuario());
                row.createCell(5).setCellValue(a.getStatus().getValor());
                row.createCell(6).setCellValue(a.getDescricao() != null ? a.getDescricao() : "");
            }

            autoAjustarColunas(sheet, colunas.length);
            workbook.write(response.getOutputStream());
        }
    }

    public void exportarContatos(List<Contato> contatos, HttpServletResponse response) throws IOException {
        configurarResposta(response, "contatos.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contatos");
            CellStyle headerStyle = criarEstiloCabecalho(workbook);

            String[] colunas = {"Nome", "Email", "Celular"};
            criarCabecalho(sheet, colunas, headerStyle);

            int rowNum = 1;
            for (Contato c : contatos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(c.getNome());
                row.createCell(1).setCellValue(c.getEmail());
                row.createCell(2).setCellValue(c.getCelular() != null ? c.getCelular() : "");
            }

            autoAjustarColunas(sheet, colunas.length);
            workbook.write(response.getOutputStream());
        }
    }

    private void configurarResposta(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void criarCabecalho(Sheet sheet, String[] colunas, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void autoAjustarColunas(Sheet sheet, int totalColunas) {
        for (int i = 0; i < totalColunas; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
