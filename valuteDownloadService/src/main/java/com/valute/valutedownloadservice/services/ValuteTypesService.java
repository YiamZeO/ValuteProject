package com.valute.valutedownloadservice.services;

import com.valute.xmldtos.DTOs.ValuteTypeListXml;
import com.valute.xmldtos.DTOs.ValuteTypeXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ValuteTypesService {

    private List<ValuteTypeXml> parseXMLResponse(Response response) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ValuteTypeListXml.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ValuteTypeListXml valuteList = (ValuteTypeListXml) unmarshaller.unmarshal(Objects.
                    requireNonNull(response.body()).byteStream());
            return valuteList.getValuteTypeXmls();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ValuteTypeXml> downloadValuteTypes() {
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
                throw new RuntimeException(e);
            }
            d++;
        } while (chunkOfXmlValutes != null);
        return allXmlValutes;
    }
}
