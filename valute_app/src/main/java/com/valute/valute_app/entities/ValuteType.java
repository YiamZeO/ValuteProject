package com.valute.valute_app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "valute_types", schema = "valute_schema")
@Data
public class ValuteType {
    @Id
    @Column
    private String id;
    @Column
    private String name;
    @Column
    private String engName;
    @Column
    private Long nominal;
    @OneToMany(mappedBy = "valute", fetch = FetchType.LAZY)
    private Set<ValuteCurs> valuteCurses;
}
