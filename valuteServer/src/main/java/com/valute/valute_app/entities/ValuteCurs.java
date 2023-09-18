package com.valute.valute_app.entities;

import com.valute.xmldtos.DTOs.ValuteCursXml;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "valute_curses", schema = "valute_schema")
public class ValuteCurs {
    @Id
    @EmbeddedId
    private ValuteCursCompositeKey valuteCursCompositeKey;
    private Double value;
    @ManyToOne
    @JoinColumn(name = "id")
    @MapsId("id")
    private ValuteType valute;

    public ValuteCurs(ValuteCursXml valuteCursXml, Date date) {
        value = valuteCursXml.getValue();
        valuteCursCompositeKey = new ValuteCursCompositeKey(valuteCursXml.getId(), date);
        valute = new ValuteType();
        valute.setId(valuteCursXml.getId());
    }
}
