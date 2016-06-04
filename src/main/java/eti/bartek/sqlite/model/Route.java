package eti.bartek.sqlite.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Routes")
public class Routes {
	
	private Integer ID;
    private String RouteNumber;
    private Integer TruckID;
    private Integer destination;
    private Date date;
    private Integer freeSeats;
    private Integer ticketPrice;
    private Integer distance;
    private Double duration;
    
    public Routes() {
    }


    public Routes(Integer ID, String RouteNumber, Integer TruckID, Integer destination, Date date, Integer freeSeats, Integer ticketPrice, Integer distance, Double duration) {
        this.ID = ID;
        this.RouteNumber = RouteNumber;
        this.TruckID = TruckID;
        this.destination = destination;
        this.date = date;
        this.freeSeats = freeSeats;
        this.ticketPrice = ticketPrice;
        this.distance = distance;
        this.duration = duration;
    }


    @Id
	public Integer getID() {
		return ID;
	}


	public void setID(Integer iD) {
		ID = iD;
	}


	public String getRouteNumber() {
		return RouteNumber;
	}


	public void setRouteNumber(String RouteNumber) {
		this.RouteNumber = RouteNumber;
	}


	public Integer getTruckID() {
		return TruckID;
	}


	public void setTruckID(Integer TruckID) {
		this.TruckID = TruckID;
	}


	public Integer getDestination() {
		return destination;
	}


	public void setDestination(Integer destination) {
		this.destination = destination;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public Integer getFreeSeats() {
		return freeSeats;
	}


	public void setFreeSeats(Integer freeSeats) {
		this.freeSeats = freeSeats;
	}


	public Integer getTicketPrice() {
		return ticketPrice;
	}


	public void setTicketPrice(Integer ticketPrice) {
		this.ticketPrice = ticketPrice;
	}


	public Integer getDistance() {
		return distance;
	}


	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	
	public void setDuration(Double duration) {
	    this.duration = duration;
	}
	
	/**
	 * zwraca dlugosc lotu w minutach
	 */
	public Double getDuration() {
	    return this.duration;
	}
}
