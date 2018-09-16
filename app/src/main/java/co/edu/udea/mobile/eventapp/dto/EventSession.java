package co.edu.udea.mobile.eventapp.dto;



public class EventSession {

    private String id;
    private String dateSession;
    private String name;
    private int event;
    private String description;

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateSession() {
        return dateSession;
    }

    public void setDateSession(String dateSession) {
        this.dateSession = dateSession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
