package com.valute.xmldtos.DTOs;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ValuteTypeXml {
    @XmlAttribute(name = "ID")
    private String id;
    @XmlElement(name = "Name")
    private String name;
    @XmlElement(name = "EngName")
    private String engName;
    @XmlElement(name = "Nominal")
    private Long nominal;
}
