package com.valute.valute_app.entities;

import com.valute.xmldtos.DTOs.ValuteTypeXml;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "valute_types", schema = "valute_schema")
@Data
@NoArgsConstructor
public class ValuteType {
    @Id
    private String id;
    private String name;
    private String engName;
    private Long nominal;
    @OneToMany(mappedBy = "valute", fetch = FetchType.LAZY)
    private Set<ValuteCurs> valute;

    public ValuteType(ValuteTypeXml valuteTypeXml) {
        id = valuteTypeXml.getId();
        name = valuteTypeXml.getName();
        engName = valuteTypeXml.getEngName();
        nominal = valuteTypeXml.getNominal();
    }
}
