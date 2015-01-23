package de.hoell.jobcontrol.ticketlist;



/**
 * Created by Hoell on 20.11.2014.
 */
public class Tickets {

    private String id;
    private String content;

    public Tickets(String id) {
    this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString(){

        return id;

    }




}