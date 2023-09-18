package com.valute.valute_app.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valute.valute_app.entities.ValuteCurs;
import com.valute.valute_app.repositories.ValuteCursesRepository;
import com.valute.valute_app.repositories.specs.ValuteCursesSpec;
import com.valute.valute_app.specsDTOs.ValuteCursesSpecDto;
import com.valute.xmldtos.DTOs.ValuteCurseListXml;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ValuteCursesService {
    private final ValuteCursesRepository valuteCursesRepository;

    @Autowired
    public ValuteCursesService(ValuteCursesRepository valuteCursesRepository) {
        this.valuteCursesRepository = valuteCursesRepository;
    }

    private Specification<ValuteCurs> createSpec(ValuteCursesSpecDto valuteCursesSpecDto) {
        Specification<ValuteCurs> spec = Specification.where(null);
        if (valuteCursesSpecDto.getId() != null && !valuteCursesSpecDto.getId().isEmpty())
            spec = spec.and(ValuteCursesSpec.idIs(valuteCursesSpecDto.getId()));
        if (valuteCursesSpecDto.getMinDate() != null)
            spec = spec.and(ValuteCursesSpec.dateGrThenOrEq(valuteCursesSpecDto.getMinDate()));
        if (valuteCursesSpecDto.getMaxDate() != null)
            spec = spec.and(ValuteCursesSpec.dateLeThenOrEq(valuteCursesSpecDto.getMaxDate()));
        return spec;
    }

    @Transactional
    public void deleteBySpec(ValuteCursesSpecDto valuteCursesSpecDto) {
        valuteCursesRepository.delete(createSpec(valuteCursesSpecDto));
    }

    @Transactional
    public void downloadValuteCurses(Date startDate, Date endDate) {
        List<ValuteCurseListXml> allXmlCurses;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();;
        HttpUrl.Builder urlBuilder = (Objects.requireNonNull(HttpUrl.
                parse("http://localhost:8081/valute_curses/download_valute_curses"))).newBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        urlBuilder.addQueryParameter("startDate", dateFormat.format(startDate));
        urlBuilder.addQueryParameter("endDate", dateFormat.format(endDate));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            ObjectMapper objectMapper = new ObjectMapper();
            allXmlCurses = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        valuteCursesRepository.saveAll(allXmlCurses.stream().map(xmlCurses ->
                        xmlCurses.getValuteCursXmls().stream().map(xmlCurs ->
                                new ValuteCurs(xmlCurs, xmlCurses.getDate())).toList())
                .flatMap(List::stream).toList());
    }

    public List<Map<String, Object>> getValuteCursesBySpec(ValuteCursesSpecDto valuteCursesSpecDto) {
        List<ValuteCurs> curses = valuteCursesRepository.findAll(createSpec(valuteCursesSpecDto));
        List<Map<String, Object>> res = new ArrayList<>();
        curses.stream().forEach(c -> {
            Map<String, Object> el = new HashMap<>();
            el.put("id", c.getValuteCursCompositeKey().getId());
            el.put("name", c.getValute().getName());
            el.put("date", c.getValuteCursCompositeKey().getDate());
            el.put("value", c.getValue());
            res.add(el);
        });
        return res;
    }

    public byte[] getValuteCursesBySpecXml(ValuteCursesSpecDto valuteCursesSpecDto) {
        byte[] bytes = {};
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Курсы");
            createHeaderRowValuteCurses(sheet);
            createDataRowsValuteCurses(sheet, valuteCursesRepository.findAll(createSpec(valuteCursesSpecDto)));
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                bytes = outputStream.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private void createHeaderRowValuteCurses(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);
        sheet.setColumnWidth(0, 10 * 256);
        sheet.setColumnWidth(1, 50 * 256);
        sheet.setColumnWidth(2, 12 * 256);
        sheet.setColumnWidth(3, 10 * 256);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("ID");
        headerCell.setCellStyle(headerCellStyle);
        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Название");
        headerCell.setCellStyle(headerCellStyle);
        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("Дата");
        headerCell.setCellStyle(headerCellStyle);
        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("Значение");
        headerCell.setCellStyle(headerCellStyle);
    }

    private void createDataRowsValuteCurses(Sheet sheet, List<ValuteCurs> valuteCurses) {
        int rowNum = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("MSK"));
        CellStyle dataCellStyle = sheet.getWorkbook().createCellStyle();
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        dataCellStyle.setBorderRight(BorderStyle.THIN);
        dataCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        for (ValuteCurs valuteCurs : valuteCurses) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(valuteCurs.getValuteCursCompositeKey().getId());
            cell.setCellStyle(dataCellStyle);
            cell = row.createCell(1);
            cell.setCellValue(valuteCurs.getValute().getName());
            cell.setCellStyle(dataCellStyle);
            cell = row.createCell(2);
            cell.setCellValue(dateFormat.format(valuteCurs.getValuteCursCompositeKey().getDate()));
            cell.setCellStyle(dataCellStyle);
            cell = row.createCell(3);
            cell.setCellValue(valuteCurs.getValue());
            cell.setCellStyle(dataCellStyle);
        }
    }
}
