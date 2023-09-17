package com.valute.valute_app.specsDTOs;

import lombok.Data;

import java.util.Date;

@Data
public class ValuteCursesSpecDto {
    private Date minDate;
    private Date maxDate;
    private String id;
}
