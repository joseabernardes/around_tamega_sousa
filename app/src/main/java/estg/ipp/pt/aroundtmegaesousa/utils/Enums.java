package estg.ipp.pt.aroundtmegaesousa.utils;

import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.models.City;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;

/**
 * Created by José Bernardes on 12/01/2018.
 */

public class Enums {


    public static List<City> getCities() {
        List<City> cities = new ArrayList<>();
        cities.add(new City("amarante", "Amarante", R.raw.amarante));
        cities.add(new City("baiao", "Baião", R.raw.baiao));
        cities.add(new City("cas_paiva", "Castelo de Paiva", R.raw.cas_paiva));
        cities.add(new City("cel_bastos", "Celorico de Bastos", R.raw.cel_bastos));
        cities.add(new City("cinfaes", "Cinfães", R.raw.cinfaes));
        cities.add(new City("felgas", "Felgueiras", R.raw.felgas));
        cities.add(new City("lousada", "Lousada", R.raw.lousada));
        cities.add(new City("marco", "Marco de Canaveses", R.raw.marco));
        cities.add(new City("pacos", "Paços de Ferreira", R.raw.pacos));
        cities.add(new City("penafiel", "Penafiel", R.raw.penafiel));
        cities.add(new City("resende", "Resende", R.raw.resende));
        return cities;
    }


    public static List<TypeOfLocation> getTypeOfLocations() {
        List<TypeOfLocation> typeOfLocations = new ArrayList<>();
        typeOfLocations.add(new TypeOfLocation(0, "Parque"));
        typeOfLocations.add(new TypeOfLocation(1, "Jardim"));
        typeOfLocations.add(new TypeOfLocation(2, "Praia fluvial"));
        typeOfLocations.add(new TypeOfLocation(3, "Monumento"));
        typeOfLocations.add(new TypeOfLocation(4, "Igreja"));
        typeOfLocations.add(new TypeOfLocation(5, "Restaurante"));
        typeOfLocations.add(new TypeOfLocation(6, "Outro"));
        return typeOfLocations;
    }


}
