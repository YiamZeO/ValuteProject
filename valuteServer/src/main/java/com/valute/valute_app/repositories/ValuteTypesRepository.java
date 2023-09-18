package com.valute.valute_app.repositories;

import com.valute.valute_app.entities.ValuteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ValuteTypesRepository extends JpaRepository<ValuteType, Long>, JpaSpecificationExecutor<ValuteType> {
}
