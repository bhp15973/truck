package eti.bartek.sqlite.model;

public class RoutePath {
    
    private Integer TruckID;
    private Integer destination;
    private String pathString;
    
    public Integer getTruckID() {
        return TruckID;
    }
    
    public Integer getDestination() {
        return destination;
    }
    public String getPathString() {
        return pathString;
    }
}
