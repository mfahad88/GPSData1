package com.example.bipl.gpsdata;

/**
 * Created by fahad on 7/18/2017.
 */

public class AddressBean {
    private String AddressLine;
    private String Locality;
    private String CountryName;

    public String getAddressLine() {
        return AddressLine;
    }

    public void setAddressLine(String addressLine) {
        AddressLine = addressLine;
    }

    public String getLocality() {
        return Locality;
    }

    public void setLocality(String locality) {
        Locality = locality;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }
}
