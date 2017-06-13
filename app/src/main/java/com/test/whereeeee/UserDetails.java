package com.test.whereeeee;

public class UserDetails {
    static String username = "";
    static String password = "";
    static String chatWith = "";

    public String latitude = "";
    public String longitude = "";

    static float latitude1 = 49.2860f; // water front station
    static float longitude1 = -123.1117f; // water front station

    static float latitude2 = 49.2798f; // vancouver library
    static float longitude2 = -123.1157f; // vancouver library

//    static float latitude2 = 49.2860f; // water front station
//    static float longitude2 = -123.1117f; // water front station
//
//    static float latitude1 = 49.2798f; // vancouver library
//    static float longitude1 = -123.1157f; // vancouver library

    public UserDetails() {
    }

    public UserDetails(String username, String password, String latitude, String longitude) {
        this.username = username;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
