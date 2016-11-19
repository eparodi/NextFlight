package status;

public class Airline {

    private String id;
    private String name;
    //private Object logo;

    public Airline(String airline_id, String airline_name) {
        this.id = airline_id;
        this.name = airline_name;
    }

    public String getAirlineId() {
        return id;
    }

    public void setAirlineId(String airline_id) {
        this.id = airline_id;
    }

    public String getAirlineName() {
        return name;
    }

    public void setAirlineName(String airline_name) {
        this.name = airline_name;
    }

}
