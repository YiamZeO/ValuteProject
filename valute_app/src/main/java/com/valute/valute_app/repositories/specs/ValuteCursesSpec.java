package com.valute.valute_app.repositories.specs;

import com.valute.valute_app.entities.ValuteCurs;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class ValuteCursesSpec {
    public static Specification<ValuteCurs> dateGrThenOrEq(Date date) {
        return (root, criteriaQuery, criteriaBuldier) -> {
            return criteriaBuldier.greaterThanOrEqualTo(root.get("valuteCursCompositeKey")
                    .get("date"), date);
        };
    }

    public static Specification<ValuteCurs> dateLeThenOrEq(Date date) {
        return (root, criteriaQuery, criteriaBuldier) -> {
            return criteriaBuldier.lessThanOrEqualTo(root.get("valuteCursCompositeKey")
                    .get("date"), date);
        };
    }
}
