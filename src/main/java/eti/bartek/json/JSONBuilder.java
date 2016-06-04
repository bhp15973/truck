package eti.bartek.json;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.controller.HibernateController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eti.bartek.sqlite.model.Truck;
import eti.bartek.sqlite.model.CKIN;
import eti.bartek.sqlite.model.FlightPath;
import eti.bartek.sqlite.model.Flights;
import eti.bartek.sqlite.model.Gate;

public class JSONBuilder {

	public static String prepareAutocompleteJSON(List<Truck> Trucks) {
		JsonObject autocomplete = new JsonObject();
    	JsonArray jArray = new JsonArray();
    	String resultJson = null;
    	for(Truck element : Trucks) {
    		jArray.add(element.getCity());
    	}
    	autocomplete.add("autocomplete", jArray);
    	resultJson = autocomplete.toString();
    	return resultJson;
    }
    
    public static String prepareMarkersJSON(List<Truck> Trucks) {
        String resultJson = null;
        JsonObject markers = new JsonObject();
        JsonArray jArray = new JsonArray();
        
        for(Truck Truck : Trucks) {
            JsonObject element  = new JsonObject();
            element.addProperty("city", Truck.getCity());
            element.addProperty("lon", Truck.getLON());
            element.addProperty("lat", Truck.getLAT());
            jArray.add(element);
        }
        markers.add("markers", jArray);
        resultJson = markers.toString();
        return resultJson;
    }
    
    public static JsonArray preparePathJson(FlightPath path, Date startDate) {
        JsonArray pathArray = new JsonArray();
        List<Flights> flights = new ArrayList<Flights>();
        List<Gate> gates = new ArrayList<>();
        Truck srcTruck = null, destTruck = null;
        List<CKIN> ckins = new ArrayList<>();
        String[] TruckIDs = path.getPathString().split("\\.");
        Date currentDate = startDate;
        try {
            for(int i = 0; i < TruckIDs.length - 1; i++) {
                if(!TruckIDs[i].equals("") && !TruckIDs[i + 1].equals("")) {
                    JsonObject singleFlight = new JsonObject();
                    JsonObject flightData = new JsonObject();
                    JsonArray gateData = new JsonArray();
                    JsonArray ckinData = new JsonArray();
                    JsonObject srcTruckJson = new JsonObject();
                    JsonObject destTruckJson = new JsonObject();
                    flights = HibernateController.getDataList("Flights",
                        "TruckID='" + TruckIDs[i] + "' and destination='" + TruckIDs[i+1] + "'");
                    Flights closestFlight = findClosestFlight(flights, currentDate);
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(currentDate);
                    calendar.add(Calendar.MINUTE, flightDurationToInt(closestFlight.getDuration()));
                    calendar.setTime(closestFlight.getDate());
                    srcTruck = HibernateController.<Truck>getSingleElement("Truck", "TruckId=" + closestFlight.getTruckID());
                    destTruck = HibernateController.<Truck>getSingleElement("Truck", "TruckId=" + closestFlight.getDestination());
                    gates = HibernateController.<Gate>getDataList("Gate", "ID=" + closestFlight.getID());
                    ckins = HibernateController.<CKIN>getDataList("CKIN", "ID=" + closestFlight.getID());
                    
                    srcTruckJson.addProperty("lat", srcTruck.getLAT());
                    srcTruckJson.addProperty("lon", srcTruck.getLON());
                    srcTruckJson.addProperty("city", srcTruck.getCity());
                    destTruckJson.addProperty("lat", destTruck.getLAT());
                    destTruckJson.addProperty("lon", destTruck.getLON());
                    destTruckJson.addProperty("city", destTruck.getCity());
                    ckinData = parseCkins(ckins);
                    gateData = parseGates(gates);
                    flightData.addProperty("flightNumber", closestFlight.getFlightNumber());
                    flightData.addProperty("date", String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
                    flightData.addProperty("freeSeats", closestFlight.getFreeSeats());
                    flightData.addProperty("ticketPrice", closestFlight.getTicketPrice());
                    flightData.addProperty("distance", closestFlight.getDistance());
                    flightData.addProperty("length", parseFlightDuration(closestFlight.getDuration()));
                    
                    singleFlight.add("dest", destTruckJson);
                    singleFlight.add("src", srcTruckJson);
                    singleFlight.add("ckin", ckinData);
                    singleFlight.add("gate", gateData);
                    singleFlight.add("flight", flightData);
                    
                    pathArray.add(singleFlight);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return pathArray;
    }
    
    public static String mergePaths(List<JsonArray> pathJsons) {
        String resultJson = null;
        JsonArray jArray = new JsonArray();
        JsonObject root = new JsonObject();
        for(JsonArray pathJson : pathJsons) {
            jArray.add(pathJson);
        }
        root.add("path", jArray);
        resultJson = root.toString();
        return resultJson;
    }
	
	private static Flights findClosestFlight(List<Flights> flights, Date currentDate) {
	    Flights closestFlight = null;
	    long minDiff = Long.MAX_VALUE;
	    long currentDiff = 0;
	    for(Flights f : flights) {
	        currentDiff = f.getDate().getTime() - currentDate.getTime();
	        if(currentDiff < minDiff && currentDiff > 0) {
	            closestFlight = f;
	            minDiff = currentDiff;
	        }
	    }
	    return closestFlight;
	}
	
	private static JsonArray parseCkins(List<CKIN> ckins) {
	    JsonArray jArray = new JsonArray();
	    Calendar calendar = GregorianCalendar.getInstance();
	    for(CKIN ckin : ckins) {
	       JsonObject singleCkin = new JsonObject();
	       calendar.setTime(ckin.getTimeStart());
	       singleCkin.addProperty("timeStart", String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY ), calendar.get(Calendar.MINUTE)));
	       calendar.setTime(ckin.getTimeStop());
	       singleCkin.addProperty("timeStop", String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
	       singleCkin.addProperty("luggageLimit", ckin.getLuggageLimit());
	       jArray.add(singleCkin);
	    }
	    return jArray;
	}
	
	private static JsonArray parseGates(List<Gate> gates) {
	    JsonArray jArray = new JsonArray();
	    Calendar calendar = Calendar.getInstance();
	    for(Gate gate : gates) {
	       JsonObject singleGate = new JsonObject();
	       calendar.setTime(gate.getTimeStart());
	       singleGate.addProperty("timeStart", String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
	       calendar.setTime(gate.getTimeStop());
	       singleGate.addProperty("timeStop", String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
	       jArray.add(singleGate);
	    }
	    return jArray;
	}
	
	private static String parseFlightDuration(Double duration) {
	    Integer dur = flightDurationToInt(duration);
	    String durationStr = null;
	    int hours = dur / 60;
	    int minutes = dur % 60;
	    durationStr = String.format("%d:%02d", hours, minutes);
	    return durationStr;
	}
	
	private static Integer flightDurationToInt(Double duration) {
	    Integer dur = (int)Math.round(duration*60);
	    return dur;
	}
}
