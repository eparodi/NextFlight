package status;

/**
 * Created by Martin on 18/11/2016.
 */

public class Airline {

    private String airline_id;
    private String airline_name;
    //private Object logo;

    public Airline(String airline_id, String airline_name) {
        this.airline_id = airline_id;
        this.airline_name = airline_name;
    }

    public String getAirlineId() {
        return airline_id;
    }

    public void setAirlineId(String airline_id) {
        this.airline_id = airline_id;
    }

    public String getAirlineName() {
        return airline_name;
    }

    public void setAirlineName(String airline_name) {
        this.airline_name = airline_name;
    }

}
