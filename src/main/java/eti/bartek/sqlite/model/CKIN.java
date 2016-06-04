package eti.bartek.sqlite.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CKIN")
public class CKIN {

    private Integer ID;
    private String RouteNumber;
    private Integer destination;
    private Date timeStart;
    private Date timeStop;
    private Integer luggageLimit;
    private Integer delayCode;
    
    public CKIN() {
    }

    public CKIN(Integer ID, String RouteNumber, Integer destination, Date timeStart, Date timeStop, Integer luggageLimit, Integer delayCode) {
        this.ID = ID;
        this. RouteNumber = RouteNumber;
        this.destination = destination;
        this.timeStart = timeStart;
        this.timeStop = timeStop;
        this.luggageLimit = luggageLimit;
        this.delayCode = delayCode;
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

	public Integer getDestination() {
		return destination;
	}

	public void setDestination(Integer destination) {
		this.destination = destination;
	}

	public Date getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}

	public Date getTimeStop() {
		return timeStop;
	}

	public void setTimeStop(Date timeStop) {
		this.timeStop = timeStop;
	}

	public Integer getLuggageLimit() {
		return luggageLimit;
	}

	public void setLuggageLimit(Integer luggageLimit) {
		this.luggageLimit = luggageLimit;
	}

	public Integer getDelayCode() {
		return delayCode;
	}

	public void setDelayCode(Integer delayCode) {
		this.delayCode = delayCode;
	}
}