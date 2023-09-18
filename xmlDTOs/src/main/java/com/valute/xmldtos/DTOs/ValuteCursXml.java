package com.valute.xmldtos.DTOs;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.text.NumberFormat;
import java.util.Locale;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ValuteCursXml {
    @XmlAttribute(name = "ID")
    private String id;
    @XmlElement(name = "Value")
    @XmlJavaTypeAdapter(DoubleAdapter.class)
    private Double value;

    public static class DoubleAdapter extends XmlAdapter<String, Double> {
        private final NumberFormat numberFormat;

        public DoubleAdapter() {
            numberFormat = NumberFormat.getInstance(Locale.getDefault());
        }

        @Override
        public Double unmarshal(String value) throws Exception {
            return numberFormat.parse(value).doubleValue();
        }

        @Override
        public String marshal(Double value) {
            return numberFormat.format(value);
        }
    }
}
