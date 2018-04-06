package com.fossickpoint.Constants;

/**
 * Created by dennisdarwis on 22/1/18.
 */

public class Commons {
    public static final String EMPTY_STRING = "";

    public static final String PLACEHOLDER_IMG = "http://lorempixel.com/200/400/";

    public static final String QUOTE_URL = "http://api.forismatic.com/api/1.0/?method=getQuote&key=457653&format=json&lang=en";

    public static final String FOSSICK_URL = "http://18.220.130.171:8000/mobile_api/";
    public static final String LOGIN_URL = FOSSICK_URL+"login/";
    public static final String REGISTER_URL = FOSSICK_URL+"register/";
    public static final String CHECK_USER_URL = FOSSICK_URL+"profile_detail/";
    public static final String REDEEM_CODE = FOSSICK_URL+"redeemcode/";
    public static final String SCHEDULE = FOSSICK_URL+"user_schedule/";
    public static final int SUCCESS = 1;
    public static final int FAILURE = -1;
}
