package com.valute.valute_app.services;

import com.valute.valute_app.entities.ValuteCurs;
import com.valute.valute_app.repositories.ValuteCursesRepository;
import com.valute.valute_app.repositories.specs.ValuteCursesSpec;
import com.valute.valute_app.specsDTOs.ValuteCursesSpecDto;
import com.valute.valute_app.xmlDTOs.ValuteCurseListXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

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

    @Transactional
    public void downloadValuteCurses(Date startDate, Date endDate) {
        List<ValuteCurseListXml> allXmlCurses = new ArrayList<>();
        ValuteCurseListXml chunkOfXmlCurses;
        Calendar date_req_start = Calendar.getInstance();
        Calendar date_req_end = Calendar.getInstance();
        date_req_start.setTime(startDate);
        date_req_end.setTime(endDate);
        OkHttpClient client = new OkHttpClient();
        while (date_req_start.before(date_req_end) || date_req_start.equals(date_req_end)) {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.
                    parse("https://www.cbr.ru/scripts/XML_daily.asp")).newBuilder();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            urlBuilder.addQueryParameter("date_req", dateFormat.format(date_req_start.getTime()));
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                chunkOfXmlCurses = parseXMLResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            allXmlCurses.add(chunkOfXmlCurses);
            date_req_start.set(Calendar.DAY_OF_MONTH, date_req_start.get(Calendar.DAY_OF_MONTH) + 1);
        }
        valuteCursesRepository.saveAll(allXmlCurses.stream().map(xmlCurses ->
                        xmlCurses.getValuteCursXmls().stream().map(xmlCurs ->
                                new ValuteCurs(xmlCurs, xmlCurses.getDate())).toList())
                .flatMap(List::stream).toList());
    }
}
