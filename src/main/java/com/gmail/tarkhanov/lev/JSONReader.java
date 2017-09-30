package com.gmail.tarkhanov.lev;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lev Tarkhanov on 29.09.2017.
 */
public class JSONReader {

    public List<City> getJsonCityList() {

        List<City> jsonCityList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            //jsonCityList = mapper.readValue(new File("src/main/resources/city.list.json"), new TypeReference<List<City>>() { });

            jsonCityList = mapper.readValue(JSONReader.class.getResourceAsStream("/city.list.json"), new TypeReference<List<City>>() { });
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonCityList;

    }

}
