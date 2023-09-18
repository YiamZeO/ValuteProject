package com.valute.valutedownloadservice.services;

import com.valute.xmldtos.DTOs.ValuteCurseListXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ValuteCursesService {

    private ValuteCurseListXml parseXMLResponse(Response response) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ValuteCurseListXml.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ValuteCurseListXml cursesList = (ValuteCurseListXml) unmarshaller.unmarshal(Objects.
                    requireNonNull(response.body()).byteStream());
            return cursesList;
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ValuteCurseListXml> downloadValuteCurses(Date startDate, Date endDate) {
        List<ValuteCurseListXml> allXmlCurses = new ArrayList<>();
        ValuteCurseListXml chunkOfXmlCurses;
        Calendar date_req_start = Calendar.getInstance();
        Calendar date_req_end = Calendar.getInstance();
        date_req_start.setTime(startDate);
        date_req_end.setTime(endDate);
        OkHttpClient client = new OkHttpClient();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        while (date_req_start.before(date_req_end) || date_req_start.equals(date_req_end)) {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.
                    parse("https://www.cbr.ru/scripts/XML_daily.asp")).newBuilder();
            urlBuilder.addQueryParameter("date_req", dateFormat.format(date_req_start.getTime()));
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                chunkOfXmlCurses = parseXMLResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            allXmlCurses.add(chunkOfXmlCurses);
            date_req_start.set(Calendar.DAY_OF_MONTH, date_req_start.get(Calendar.DAY_OF_MONTH) + 1);
        }
        return allXmlCurses;
    }
}
