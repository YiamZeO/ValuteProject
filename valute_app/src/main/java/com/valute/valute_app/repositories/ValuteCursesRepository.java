package com.valute.valute_app.repositories;

import com.valute.valute_app.entities.ValuteCurs;
import com.valute.valute_app.entities.ValuteCursCompositeKey;
import com.valute.valute_app.entities.ValuteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ValuteCursesRepository extends JpaRepository<ValuteCurs, ValuteCursCompositeKey>,
        JpaSpecificationExecutor<ValuteType> {
}
