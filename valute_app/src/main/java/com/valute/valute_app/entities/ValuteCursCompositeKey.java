package com.valute.valute_app.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
public class ValuteCursCompositeKey implements Serializable {
    private Long valuteTypeId;
    private Date date;
}
