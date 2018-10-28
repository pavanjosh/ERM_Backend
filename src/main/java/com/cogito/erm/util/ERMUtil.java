package com.cogito.erm.util;

import com.cogito.erm.exception.ServiceException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ERMUtil {

    //public static final String API_PATH = "/api/v1";

    public static final String AUTHENTICATE_URL = "/erm/api/login";

    public static final List<String> NON_AUTHENTICATION_APPLICATION_PATH_LIST=
      new ArrayList<String>(Arrays.asList("/erm/api/home","/erm/api/registerAdmin","/erm/api/login/register/admin","/erm/api/createAdminLogin","/user/forgot","/user/reset" ));
    public static final List<String> NON_AUTHENTICATION_SPRING_ACTUATOR_PATH_LIST=
      new ArrayList<String>(Arrays.asList("\"/autoconfig\"","/beans","/configprops","/env","/mappings","/metrics","/shutdown" ));

    public static final String CORELATION_ID_VALUE = "corelationId";
    public static final String HTTP_STATUS_BAD_REQUEST = "Bad Request";
    public static final String HTTP_STATUS_UNAUTHORIZED = "Unauthorized";
    public static final String SOURCE_IP = "sourceip";
    public static final String SOURCE_PORT = "sourceport";
    public static final String DESTINATION_IP = "destinationip";
    public static final String DESTINATION_PORT = "destinationport";
    public static final String PROTOCOL = "protocol";
    public static final String ACTIVITY = "activity";
    public static final String ACTIVITY_STATUS = "activitystatus";
    public static final String APPLICATION = "application";
    public static final String HOST = "host";
    public static final String ERR_INVALID_JSON_FORMAT = "Exception parsing JSON format";
    public static final String SYSTEM_NAME="ERM_COGITO_EXPERIENCE";
    public static final String EMPLOYEE_ID_FILED = "id";
    public static final String EMPLOYEE_EMPLOYEEID_FILED = "employeeId";
    public static final String EMPLOYEE_NAME_FILED = "name";
    public static final String EMPLOYEE_ACTIVE_FILED = "active";
    public static final String EMPLOYEE_ROSTER_STARTDATE_FILED = "rosterStartDate";
    public static final String EMPLOYEE_ROSTER_ENDDATE_FILED = "rosterEndDate";

    // collection names

    public static final String EMPLOYEE_DETAILS_COLLECTION = "employeeDetails";
    public static final String EMPLOYEE_LOGIN_COLLECTION = "employeeLoginDetails";
    public static final String EMPLOYEE_ROLESANDROSTER_COLLECTION = "employeeRolesAndRosters";



    public static void createAndThrowException(int httpStatus,String errorCode,String errorMessage){
        ServiceException experienceServiceException = new ServiceException();
        experienceServiceException.setErrorMessage(errorMessage);
        experienceServiceException.setErrorCode(errorCode);
        experienceServiceException.setHttpStatus(httpStatus);
        throw experienceServiceException;
    }
}
