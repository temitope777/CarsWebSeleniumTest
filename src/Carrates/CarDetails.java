package Carrates;

import java.net.URL;

public class CarDetails {

    private String registration;
    // Getter
    public String getRegistration() {
        return registration;
    }
    // Setter
    public void setRegistration(String c) {
        this.registration = c;
    }

    // make
    private String make;
    // Getter
    public String getMake() {
        return make;
    }
    // Setter
    public void setMake(String c) {
        this.make = c;
    }

    //////

    // model
    private String model;
    // Getter
    public String getModel() {
        return model;
    }
    // Setter
    public void setModel(String c) {
        this.model = c;
    }

    // color
    private String colour;
    // Getter
    public String getColour() {
        return colour;
    }
    // Setter
    public void setColour(String c) {
        this.colour = c;
    }

    // year
    private String year;
    // Getter
    public String getYear() {
        return year;
    }
    // Setter
    public void setYear(String c) {
        this.year = c;
    }

    // registered
    private String registered;
    // Getter
    public String getRegistered() {
        return registered;
    }
    // Setter
    public void setRegistered(String c) {
        this.registered = c;
    }

    // Vc5issued
    private String vc5issued;
    // Getter
    public String getVc5issued() {
        return vc5issued;
    }
    // Setter
    public void setVc5issued(String c) {
        this.vc5issued = c;
    }

    public  String GetFiles()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("car_input.txt");
        String path = resource.getPath();
        java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("car_input.txt");
        return  "";
    }
}
