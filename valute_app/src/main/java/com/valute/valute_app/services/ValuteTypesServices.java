package com.valute.valute_app.services;

import com.valute.valute_app.repositories.ValuteTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ValuteTypesServices {
    private final ValuteTypesRepository valuteTypesRepository;

    @Autowired
    public ValuteTypesServices(ValuteTypesRepository valuteTypesRepository) {
        this.valuteTypesRepository = valuteTypesRepository;
    }
    @Transactional
    public void deleteAll(){
        valuteTypesRepository.deleteAll();
    }
    @Transactional
    public String downloadValuteTypes(){
        try{

        }catch (Exception e){
            return "\n<---Ошибка: Загрузка не удалась--->\n";
        }
        return "\n<---Загрузка выполнена успешно--->\n";
    }
}
