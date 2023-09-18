package com.valute.valute_app.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valute.valute_app.entities.ValuteType;
import com.valute.valute_app.repositories.ValuteTypesRepository;
import com.valute.xmldtos.DTOs.ValuteTypeXml;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

@Service
public class ValuteTypesService {
    private final ValuteTypesRepository valuteTypesRepository;

    @Autowired
    public ValuteTypesService(ValuteTypesRepository valuteTypesRepository) {
        this.valuteTypesRepository = valuteTypesRepository;
    }

    @Transactional
    public void deleteAll() {
        valuteTypesRepository.deleteAll();
    }

    @Transactional
    public void downloadValuteTypes() {
        List<ValuteTypeXml> allXmlValutes;
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.
                parse("http://localhost:8081/valute_types/download_valute_types")).newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            allXmlValutes = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        valuteTypesRepository.saveAll(allXmlValutes.stream().map(ValuteType::new).toList());
    }

    public List<ValuteType> getValuteTypes() {
        return valuteTypesRepository.findAll();
    }

    public byte[] getValuteTypesXml() {
        byte[] bytes = {};
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Валюты");
            createHeaderRowValuteTypes(sheet);
            createDataRowsValuteTypes(sheet, valuteTypesRepository.findAll());
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                bytes = outputStream.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private void createHeaderRowValuteTypes(Sheet sheet) {
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
        sheet.setColumnWidth(1, 30 * 256);
        sheet.setColumnWidth(2, 30 * 256);
        sheet.setColumnWidth(3, 10 * 256);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("ID");
        headerCell.setCellStyle(headerCellStyle);
        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Название");
        headerCell.setCellStyle(headerCellStyle);
        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("Англ. название");
        headerCell.setCellStyle(headerCellStyle);
        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("Номинал");
        headerCell.setCellStyle(headerCellStyle);
    }

    private void createDataRowsValuteTypes(Sheet sheet, List<ValuteType> valuteTypes) {
        int rowNum = 1;
        CellStyle dataCellStyle = sheet.getWorkbook().createCellStyle();
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        dataCellStyle.setBorderRight(BorderStyle.THIN);
        dataCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        for (ValuteType valuteType : valuteTypes) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(valuteType.getId());
            cell.setCellStyle(dataCellStyle);
            cell = row.createCell(1);
            cell.setCellValue(valuteType.getName());
            cell.setCellStyle(dataCellStyle);
            cell = row.createCell(2);
            cell.setCellValue(valuteType.getEngName());
            cell.setCellStyle(dataCellStyle);
            cell = row.createCell(3);
            cell.setCellValue(valuteType.getNominal());
            cell.setCellStyle(dataCellStyle);
        }
    }
}
