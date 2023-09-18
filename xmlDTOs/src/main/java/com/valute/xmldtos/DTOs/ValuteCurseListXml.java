package com.valute.xmldtos.DTOs;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Data
@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValuteCurseListXml {
    @XmlElement(name = "Valute")
    private List<ValuteCursXml> valuteCursXmls;
    @XmlAttribute(name = "Date")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date date;

    public static class DateAdapter extends XmlAdapter<String, Date> {
        private final SimpleDateFormat dateFormat;

        public DateAdapter() {
            dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        }

        @Override
        public Date unmarshal(String value) throws Exception {
            return dateFormat.parse(value);
        }

        @Override
        public String marshal(Date value) {
            return dateFormat.format(value);
        }
    }
}
