package com.valute.valute_app.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "valute_curses", schema = "valute_schema")
public class ValuteCurs {
    @Id
    @EmbeddedId
    private ValuteCursCompositeKey valuteCursCompositeKey;
    @Column
    private Long value;
    @ManyToOne
    @MapsId("valuteTypeId")
    private ValuteType valute;
}
