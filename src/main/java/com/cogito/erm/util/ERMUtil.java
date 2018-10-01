package com.cogito.erm.util;


public class ERMUtil {

    //public static final String API_PATH = "/api/v1";

    public static final String AUTHENTICATE_URL = "/erm/api/login";
    //public static final String STUFF_URL = API_PATH + "/stuff";
    public static final String USER_REGISTER_PATH = "/user/register";
    public static final String HOME_PATH = "/home";
    public static final String INDEX_PATH = "/index";
    public static final String USER_FORGOT_PASSWORD_PATH  = "/user/forgot";
    public static final String USER_RESET_PASSWORD_PATH = "/user/reset";


    // Spring Boot Actuator services
    public static final String AUTOCONFIG_ENDPOINT = "/autoconfig";
    public static final String BEANS_ENDPOINT = "/beans";
    public static final String CONFIGPROPS_ENDPOINT = "/configprops";
    public static final String ENV_ENDPOINT = "/env";
    public static final String MAPPINGS_ENDPOINT = "/mappings";
    public static final String METRICS_ENDPOINT = "/metrics";
    public static final String SHUTDOWN_ENDPOINT = "/shutdown";
}
