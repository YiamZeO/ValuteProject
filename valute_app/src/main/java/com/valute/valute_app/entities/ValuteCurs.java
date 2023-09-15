package com.valute.valute_app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "valute_curses", schema = "valute_schema")
public class ValuteCurs {
    @Id
    @EmbeddedId
    private ValuteCursCompositeKey valuteCursCompositeKey;
    @Column
    private Long value;
    @Id
    @Column
    private Date date;
    @ManyToOne
    @JoinColumn(name = "valute_type_id", nullable = false)
    private ValuteType valute;
}
