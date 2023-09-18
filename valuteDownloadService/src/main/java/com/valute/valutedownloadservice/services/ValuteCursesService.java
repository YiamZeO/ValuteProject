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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

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

    private class ValuteCursesTask extends RecursiveTask<List<ValuteCurseListXml>> {

        private final Date startDate;
        private final Date endDate;
        private int taskLevel;

        public ValuteCursesTask(Date startDate, Date endDate, int taskLevel) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.taskLevel = taskLevel;
        }

        private Map<String, Integer> calendarDiff(Calendar startC, Calendar endC){
            Map<String, Integer> diff = new HashMap<>();
            int yearsDiff = endC.get(Calendar.YEAR) - startC.get(Calendar.YEAR);
            int monthsDiff = endC.get(Calendar.MONTH) - startC.get(Calendar.MONTH);
            int daysDiff = endC.get(Calendar.DAY_OF_MONTH) - startC.get(Calendar.DAY_OF_MONTH);
            if(daysDiff < 0){
                monthsDiff--;
                daysDiff += endC.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            if (monthsDiff < 0){
                yearsDiff--;
                monthsDiff += endC.getActualMaximum(Calendar.MONTH);
            }
            if (yearsDiff == 1){
                yearsDiff--;
                monthsDiff += endC.getActualMaximum(Calendar.MONTH);
            }
            if (monthsDiff == 1){
                monthsDiff--;
                daysDiff += endC.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            diff.put("years", yearsDiff);
            diff.put("months", monthsDiff);
            diff.put("days", daysDiff);
            return  diff;
        }

        @Override
        protected List<ValuteCurseListXml> compute() {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            Map<String, Integer> cDiff = calendarDiff(startCalendar, endCalendar);
            if (taskLevel < 3 || (cDiff.get("days") < 8 && cDiff.get("months") == 0
                    && cDiff.get("years") == 0)) {
                List<ValuteCurseListXml> allXmlCurses = new ArrayList<>();
                ValuteCurseListXml chunkOfXmlCurses;
                OkHttpClient client = new OkHttpClient();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                while (startCalendar.before(endCalendar) || startCalendar.equals(endCalendar)) {
                    HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.
                            parse("https://www.cbr.ru/scripts/XML_daily.asp")).newBuilder();
                    urlBuilder.addQueryParameter("date_req", dateFormat.format(startCalendar.getTime()));
                    String url = urlBuilder.build().toString();
                    Request request = new Request.Builder().url(url).build();
                    try (Response response = client.newCall(request).execute()) {
                        chunkOfXmlCurses = parseXMLResponse(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                    if (Objects.requireNonNull(chunkOfXmlCurses).getValuteCursXmls().stream()
                            .anyMatch(a -> a.getValue() == null))
                        System.out.println(chunkOfXmlCurses);
                    allXmlCurses.add(chunkOfXmlCurses);
                    startCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                }
                return allXmlCurses;
            } else {
                Calendar partCalendar = (Calendar) startCalendar.clone();
                partCalendar.add(Calendar.YEAR,
                        partCalendar.get(Calendar.YEAR) + cDiff.get("years") / 2);
                partCalendar.add(Calendar.MONTH,
                        partCalendar.get(Calendar.MONTH) + cDiff.get("months") / 2);
                partCalendar.add(Calendar.DAY_OF_MONTH,
                        partCalendar.get(Calendar.DAY_OF_MONTH) + cDiff.get("days") / 2);
                ValuteCursesTask subTask1 = new ValuteCursesTask(startCalendar.getTime(),
                        partCalendar.getTime(), taskLevel + 1);
                ValuteCursesTask subTask2 = new ValuteCursesTask(partCalendar.getTime(),
                        endCalendar.getTime(), taskLevel + 1);
                subTask1.fork();
                subTask2.fork();
                List<ValuteCurseListXml> allXmlCurses = subTask1.join();
                allXmlCurses.addAll(subTask2.join());
                return allXmlCurses;
            }
        }
    }

    public List<ValuteCurseListXml> downloadValuteCurses(Date startDate, Date endDate) {
        List<ValuteCurseListXml> allXmlCurses;
        try (ForkJoinPool forkJoinPool = new ForkJoinPool()) {
            ValuteCursesTask task = new ValuteCursesTask(startDate, endDate, 0);
            allXmlCurses = forkJoinPool.invoke(task);
            return allXmlCurses;
        }
    }
}
