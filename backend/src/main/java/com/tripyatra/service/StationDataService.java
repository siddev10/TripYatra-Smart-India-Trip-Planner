package com.tripyatra.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Station Data Service — provides station code and coordinate lookups.
 * Contains static reference data for Indian railway stations.
 * Demonstrates: Singleton Service, HashMap data structures, Encapsulation
 */
@Service
public class StationDataService {

    /** Station code lookup: City Name → IRCTC Station Code */
    private final Map<String, String> stationCodes = new HashMap<>();

    /** Station coordinates: City Name → [latitude, longitude] */
    private final Map<String, double[]> stationCoords = new HashMap<>();

    /** Constructor — initializes lookup tables */
    public StationDataService() {
        initializeStationCodes();
        initializeCoordinates();
    }

    private void initializeStationCodes() {
        // Maharashtra
        stationCodes.put("MUMBAI", "CSTM");    stationCodes.put("BOMBAY", "CSTM");
        stationCodes.put("MUMBAI CST", "CSTM"); stationCodes.put("MUMBAI CENTRAL", "BCT");
        stationCodes.put("BANDRA", "BDTS");     stationCodes.put("DADAR", "DR");
        stationCodes.put("THANE", "TNA");       stationCodes.put("PUNE", "PUNE");
        stationCodes.put("NAGPUR", "NGP");      stationCodes.put("NASHIK", "NK");
        stationCodes.put("AURANGABAD", "AWB");  stationCodes.put("SOLAPUR", "SUR");
        stationCodes.put("KOLHAPUR", "KOP");    stationCodes.put("NANDED", "NED");
        stationCodes.put("LATUR", "LUR");       stationCodes.put("AKOLA", "AK");
        stationCodes.put("AMRAVATI", "AMI");

        // Delhi / NCR
        stationCodes.put("DELHI", "NDLS");      stationCodes.put("NEW DELHI", "NDLS");
        stationCodes.put("OLD DELHI", "DLI");   stationCodes.put("HAZRAT NIZAMUDDIN", "NZM");
        stationCodes.put("GURGAON", "GGN");     stationCodes.put("NOIDA", "NDLS");
        stationCodes.put("FARIDABAD", "FDB");

        // Rajasthan
        stationCodes.put("JAIPUR", "JP");       stationCodes.put("JODHPUR", "JU");
        stationCodes.put("UDAIPUR", "UDZ");     stationCodes.put("AJMER", "AII");
        stationCodes.put("KOTA", "KOTA");       stationCodes.put("BIKANER", "BKN");
        stationCodes.put("ALWAR", "AWR");

        // UP / Uttarakhand
        stationCodes.put("AGRA", "AGC");        stationCodes.put("LUCKNOW", "LKO");
        stationCodes.put("VARANASI", "BSB");    stationCodes.put("ALLAHABAD", "ALD");
        stationCodes.put("KANPUR", "CNB");      stationCodes.put("MATHURA", "MTJ");
        stationCodes.put("MEERUT", "MTC");      stationCodes.put("GORAKHPUR", "GKP");
        stationCodes.put("DEHRADUN", "DDN");    stationCodes.put("HARIDWAR", "HW");

        // Punjab / Haryana
        stationCodes.put("AMRITSAR", "ASR");    stationCodes.put("LUDHIANA", "LDH");
        stationCodes.put("CHANDIGARH", "CDG");  stationCodes.put("AMBALA", "UMB");
        stationCodes.put("PATHANKOT", "PTK");   stationCodes.put("JALANDHAR", "JUC");

        // Gujarat
        stationCodes.put("AHMEDABAD", "ADI");   stationCodes.put("SURAT", "ST");
        stationCodes.put("VADODARA", "BRC");    stationCodes.put("RAJKOT", "RJT");
        stationCodes.put("BHAVNAGAR", "BVC");   stationCodes.put("JAMNAGAR", "JAM");

        // Karnataka
        stationCodes.put("BANGALORE", "SBC");   stationCodes.put("BENGALURU", "SBC");
        stationCodes.put("MYSORE", "MYS");      stationCodes.put("HUBLI", "UBL");
        stationCodes.put("MANGALORE", "MAQ");   stationCodes.put("BELGAUM", "BGM");

        // Tamil Nadu
        stationCodes.put("CHENNAI", "MAS");     stationCodes.put("MADRAS", "MAS");
        stationCodes.put("COIMBATORE", "CBE");  stationCodes.put("MADURAI", "MDU");
        stationCodes.put("TRICHY", "TPJ");      stationCodes.put("SALEM", "SA");
        stationCodes.put("TIRUNELVELI", "TEN"); stationCodes.put("ERODE", "ED");

        // Kerala
        stationCodes.put("KOCHI", "ERS");       stationCodes.put("COCHIN", "ERS");
        stationCodes.put("TRIVANDRUM", "TVC");  stationCodes.put("THIRUVANANTHAPURAM", "TVC");
        stationCodes.put("KOZHIKODE", "CLT");   stationCodes.put("CALICUT", "CLT");
        stationCodes.put("THRISSUR", "TCR");    stationCodes.put("KANNUR", "CAN");

        // Andhra / Telangana
        stationCodes.put("HYDERABAD", "HYB");   stationCodes.put("SECUNDERABAD", "SC");
        stationCodes.put("VISAKHAPATNAM", "VSKP"); stationCodes.put("VIJAYAWADA", "BZA");
        stationCodes.put("TIRUPATI", "TPTY");   stationCodes.put("GUNTUR", "GNT");
        stationCodes.put("WARANGAL", "WL");

        // West Bengal
        stationCodes.put("KOLKATA", "KOAA");    stationCodes.put("CALCUTTA", "KOAA");
        stationCodes.put("HOWRAH", "HWH");      stationCodes.put("SEALDAH", "SDAH");
        stationCodes.put("DURGAPUR", "DGR");    stationCodes.put("ASANSOL", "ASN");
        stationCodes.put("SILIGURI", "SGUJ");

        // Bihar / Jharkhand
        stationCodes.put("PATNA", "PNBE");      stationCodes.put("GAYA", "GAYA");
        stationCodes.put("RANCHI", "RNC");       stationCodes.put("JAMSHEDPUR", "TATA");
        stationCodes.put("DHANBAD", "DHN");      stationCodes.put("MUZAFFARPUR", "MFP");

        // Odisha & Others
        stationCodes.put("BHUBANESWAR", "BBS"); stationCodes.put("CUTTACK", "CTC");
        stationCodes.put("PURI", "PURI");       stationCodes.put("GUWAHATI", "GHY");
        stationCodes.put("BHOPAL", "BPL");      stationCodes.put("INDORE", "INDB");
        stationCodes.put("JABALPUR", "JBP");    stationCodes.put("RAIPUR", "R");
        stationCodes.put("GOA", "MAO");         stationCodes.put("MARGAO", "MAO");
        stationCodes.put("PANAJI", "MAO");      stationCodes.put("JAMMU", "JAT");
        stationCodes.put("SHIMLA", "SML");
    }

    private void initializeCoordinates() {
        stationCoords.put("MUMBAI", new double[]{19.0760, 72.8777});
        stationCoords.put("BOMBAY", new double[]{19.0760, 72.8777});
        stationCoords.put("DELHI", new double[]{28.6139, 77.2090});
        stationCoords.put("NEW DELHI", new double[]{28.6139, 77.2090});
        stationCoords.put("KOLKATA", new double[]{22.5726, 88.3639});
        stationCoords.put("CALCUTTA", new double[]{22.5726, 88.3639});
        stationCoords.put("CHENNAI", new double[]{13.0827, 80.2707});
        stationCoords.put("MADRAS", new double[]{13.0827, 80.2707});
        stationCoords.put("BANGALORE", new double[]{12.9716, 77.5946});
        stationCoords.put("BENGALURU", new double[]{12.9716, 77.5946});
        stationCoords.put("HYDERABAD", new double[]{17.3850, 78.4867});
        stationCoords.put("PUNE", new double[]{18.5204, 73.8567});
        stationCoords.put("JAIPUR", new double[]{26.9124, 75.7873});
        stationCoords.put("LUCKNOW", new double[]{26.8467, 80.9462});
        stationCoords.put("AHMEDABAD", new double[]{23.0225, 72.5714});
        stationCoords.put("GOA", new double[]{15.2993, 74.1240});
        stationCoords.put("VARANASI", new double[]{25.3176, 82.9739});
        stationCoords.put("NAGPUR", new double[]{21.1458, 79.0882});
        stationCoords.put("PATNA", new double[]{25.5941, 85.1376});
        stationCoords.put("BHOPAL", new double[]{23.2599, 77.4126});
        stationCoords.put("SURAT", new double[]{21.1702, 72.8311});
        stationCoords.put("KOCHI", new double[]{9.9312, 76.2673});
        stationCoords.put("AGRA", new double[]{27.1767, 78.0081});
        stationCoords.put("AMRITSAR", new double[]{31.6340, 74.8723});
        stationCoords.put("CHANDIGARH", new double[]{30.7333, 76.7794});
        stationCoords.put("COIMBATORE", new double[]{11.0168, 76.9558});
    }

    /**
     * Get IRCTC station code for a city name.
     * Falls back to first 4 characters of the name if not found.
     */
    public String getStationCode(String cityName) {
        if (cityName == null) return "";
        String cleanName = cityName.contains("-") ? cityName.split("-")[0] : cityName;
        String upper = cleanName.toUpperCase().trim();
        return stationCodes.getOrDefault(upper, upper.substring(0, Math.min(4, upper.length())));
    }

    /**
     * Get geographic coordinates for a city.
     * Returns empty array if city not found.
     */
    public double[] getCoordinates(String cityName) {
        if (cityName == null) return new double[]{};
        String cleanName = cityName.contains("-") ? cityName.split("-")[0] : cityName;
        String upper = cleanName.toUpperCase().trim();
        return stationCoords.getOrDefault(upper, new double[]{});
    }

    /** Get the total number of station codes in the lookup */
    public int getStationCodeCount() {
        return stationCodes.size();
    }
}
