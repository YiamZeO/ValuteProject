package com.valute.valute_app.services;

import com.valute.valute_app.entities.ValuteType;
import com.valute.valute_app.repositories.ValuteTypesRepository;
import com.valute.valute_app.xmlDTOs.ValuteTypeListXml;
import com.valute.valute_app.xmlDTOs.ValuteTypeXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ValuteTypesServices {
    private final ValuteTypesRepository valuteTypesRepository;

    @Autowired
    public ValuteTypesServices(ValuteTypesRepository valuteTypesRepository) {
        this.valuteTypesRepository = valuteTypesRepository;
    }

    @Transactional
    public void deleteAll() {
        valuteTypesRepository.deleteAll();
    }

    private List<ValuteTypeXml> parseXMLResponse(Response response) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ValuteTypeListXml.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ValuteTypeListXml valuteList = (ValuteTypeListXml) unmarshaller.unmarshal(Objects.
                    requireNonNull(response.body()).byteStream());
            return valuteList.getValuteTypeXmls();
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public void downloadValuteTypes() {
        List<ValuteTypeXml> allXmlValutes = new ArrayList<>();
        List<ValuteTypeXml> chunkOfXmlValutes = new ArrayList<>();
        int d = 0;
        OkHttpClient client = new OkHttpClient();
        do {
            allXmlValutes.addAll(chunkOfXmlValutes);
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.
                    parse("https://www.cbr.ru/scripts/XML_val.asp")).newBuilder();
            urlBuilder.addQueryParameter("d", String.valueOf(d));
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                chunkOfXmlValutes = parseXMLResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            d++;
        } while (chunkOfXmlValutes != null);
        valuteTypesRepository.saveAll(allXmlValutes.stream().map(ValuteType::new).toList());
    }

    public byte[] getValuteTypesXml() {
        byte[] bytes = {};
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Валюты");
            createHeaderRowProducts(sheet);
            createDataRowsProducts(sheet, valuteTypesRepository.findAll());
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                bytes = outputStream.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private void createHeaderRowProducts(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
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

    private void createDataRowsProducts(Sheet sheet, List<ValuteType> valuteTypes) {
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
