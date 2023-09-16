package com.valute.valute_app.xmlDTOs;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "Valuta")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValuteTypeListXml {
    @XmlElement(name = "Item")
    private List<ValuteTypeXml> valuteTypeXmls;
}
