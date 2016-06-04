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
import eti.bartek.sqlite.model.RoutePath;
import eti.bartek.sqlite.model.Routes;
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
    
    public static JsonArray preparePathJson(RoutePath path, Date startDate) {
        JsonArray pathArray = new JsonArray();
        List<Routes> Routes = new ArrayList<Routes>();
        List<Gate> gates = new ArrayList<>();
        Truck srcTruck = null, destTruck = null;
        List<CKIN> ckins = new ArrayList<>();
        String[] TruckIDs = path.getPathString().split("\\.");
        Date currentDate = startDate;
        try {
            for(int i = 0; i < TruckIDs.length - 1; i++) {
                if(!TruckIDs[i].equals("") && !TruckIDs[i + 1].equals("")) {
                    JsonObject singleRoute = new JsonObject();
                    JsonObject RouteData = new JsonObject();
                    JsonArray gateData = new JsonArray();
                    JsonArray ckinData = new JsonArray();
                    JsonObject srcTruckJson = new JsonObject();
                    JsonObject destTruckJson = new JsonObject();
                    Routes = HibernateController.getDataList("Routes",
                        "TruckID='" + TruckIDs[i] + "' and destination='" + TruckIDs[i+1] + "'");
                    Routes closestRoute = findClosestRoute(Routes, currentDate);
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(currentDate);
                    calendar.add(Calendar.MINUTE, RouteDurationToInt(closestRoute.getDuration()));
                    calendar.setTime(closestRoute.getDate());
                    srcTruck = HibernateController.<Truck>getSingleElement("Truck", "TruckId=" + closestRoute.getTruckID());
                    destTruck = HibernateController.<Truck>getSingleElement("Truck", "TruckId=" + closestRoute.getDestination());
                    gates = HibernateController.<Gate>getDataList("Gate", "ID=" + closestRoute.getID());
                    ckins = HibernateController.<CKIN>getDataList("CKIN", "ID=" + closestRoute.getID());
                    
                    srcTruckJson.addProperty("lat", srcTruck.getLAT());
                    srcTruckJson.addProperty("lon", srcTruck.getLON());
                    srcTruckJson.addProperty("city", srcTruck.getCity());
                    destTruckJson.addProperty("lat", destTruck.getLAT());
                    destTruckJson.addProperty("lon", destTruck.getLON());
                    destTruckJson.addProperty("city", destTruck.getCity());
                    ckinData = parseCkins(ckins);
                    gateData = parseGates(gates);
                    RouteData.addProperty("RouteNumber", closestRoute.getRouteNumber());
                    RouteData.addProperty("date", String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
                    RouteData.addProperty("freeSeats", closestRoute.getFreeSeats());
                    RouteData.addProperty("ticketPrice", closestRoute.getTicketPrice());
                    RouteData.addProperty("distance", closestRoute.getDistance());
                    RouteData.addProperty("length", parseRouteDuration(closestRoute.getDuration()));
                    
                    singleRoute.add("dest", destTruckJson);
                    singleRoute.add("src", srcTruckJson);
                    singleRoute.add("ckin", ckinData);
                    singleRoute.add("gate", gateData);
                    singleRoute.add("Route", RouteData);
                    
                    pathArray.add(singleRoute);
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
	
	private static Routes findClosestRoute(List<Routes> Routes, Date currentDate) {
	    Routes closestRoute = null;
	    long minDiff = Long.MAX_VALUE;
	    long currentDiff = 0;
	    for(Routes f : Routes) {
	        currentDiff = f.getDate().getTime() - currentDate.getTime();
	        if(currentDiff < minDiff && currentDiff > 0) {
	            closestRoute = f;
	            minDiff = currentDiff;
	        }
	    }
	    return closestRoute;
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
	
	private static String parseRouteDuration(Double duration) {
	    Integer dur = RouteDurationToInt(duration);
	    String durationStr = null;
	    int hours = dur / 60;
	    int minutes = dur % 60;
	    durationStr = String.format("%d:%02d", hours, minutes);
	    return durationStr;
	}
	
	private static Integer RouteDurationToInt(Double duration) {
	    Integer dur = (int)Math.round(duration*60);
	    return dur;
	}
}
