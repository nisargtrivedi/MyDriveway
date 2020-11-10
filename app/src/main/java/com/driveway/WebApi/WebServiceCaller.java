package com.driveway.WebApi;

import android.os.StrictMode;
import android.util.Log;

import com.driveway.APIResponse.ForgotPasswordResponse;
import com.driveway.APIResponse.GetPropertyResponse;
import com.driveway.APIResponse.LoginResponse;
import com.driveway.APIResponse.RegistrationResponse;
import com.driveway.Activity.BaseActivity;
import com.driveway.Activity.MainNavigationScreen;
import com.driveway.Activity.MainScreen;
import com.driveway.Activity.MyDriveWayApplication;
import com.driveway.Utility.AppPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.OPTIONS;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public class WebServiceCaller {
    private static ApiInterface ApiInterface;

//    private static String FileName="webApi.php";

    public static ApiInterface getClient() {
        if (ApiInterface == null) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", MainNavigationScreen.Token.isEmpty()? MainScreen.TOKEN:MainNavigationScreen.Token)
                            //.addHeader("App-Key", "nE28E~]EeP.a")
                            .build();
                    return chain.proceed(newRequest);
                }
            }).build();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(WebUtility.BASE_URL)
                    .client(okHttpClient.newBuilder()
                            .addInterceptor(logging).build())
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
            ApiInterface = client.create(ApiInterface.class);
        }
        return ApiInterface;
    }


    public interface ApiInterface {

        String FileName="webApi.php";

//        @GET("runroute/{driverId}")
//        Call<JsonElement> getRoutes(@Path("driverId") int driverId);
//
//
//
//
//        @POST("markcomplete/{runNumber}/{stopNumber}/{initials}")
//        Call<JsonElement> markDeliveryComplete(@Path("runNumber") String runNumber,
//                                               @Path("stopNumber") int stopNumber,
//                                               @Path("initials") String initials,
//                                               @Body RequestBody acceptedids);





//        @FormUrlEncoded
//        @POST("api.php")
//        Call<LoginResponse> login(@Field("action") String action, @Field("phone") String PhoneNumber, @Field("token") String Token);



        @FormUrlEncoded
        @POST(FileName)
        Call<RegistrationResponse> register(@Field("action") String action, @Field("first_name") String FirstName, @Field("last_name") String LastName,@Field("gender") String Gender,@Field("bod") String BirthDate,@Field("device_token") String DeviceToken,@Field("password") String Password,@Field("zip_code") String ZipCode,@Field("email") String Email);

        @FormUrlEncoded
        @POST(FileName)
        Call<LoginResponse> login(@Field("action") String action, @Field("email") String Email, @Field("password") String Password, @Field("device_token") String Token);

        @FormUrlEncoded
        @POST(FileName)
        Call<LoginResponse> editProfile(@Field("action") String action, @Field("first_name") String FirstName, @Field("last_name") String LastName, @Field("gender") String Gender,@Field("bod") String BirthDate,@Field("zip_code") String ZipCode,@Field("user_id") String UserID);

        @FormUrlEncoded
        @POST(FileName)
        Call<LoginResponse> switchAccount(@Field("action") String action, @Field("user_id") String UserID, @Field("type") String Type);

        @FormUrlEncoded
        @POST(FileName)
        Call<ForgotPasswordResponse> forgotPassword(@Field("action") String action, @Field("email") String Email);


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getProperty(@Field("action") String action, @Field("user_id") String UserID);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getCars(@Field("action") String action, @Field("user_id") String UserID);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> deleteCars(@Field("action") String action, @Field("user_id") String UserID,@Field("car_id") String carID);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> deleteProperty(@Field("action") String action, @Field("user_id") String UserID,@Field("property_id") String ID);


        @Multipart
        @POST(FileName)
        Call<JsonObject> addCars(@Part("action") RequestBody action,
                                 @Part("user_id") RequestBody UserID,
                                 @Part("reg_number") RequestBody RegisterNumber,
                                 @Part("model") RequestBody Model,
                                 @Part("make_year") RequestBody MakingYear,
                                 @Part MultipartBody.Part image1,
                                 @Part("car_id") RequestBody CarId,
                                 @Part("is_default") RequestBody is_default);

        @Multipart
        @POST(FileName)
        Call<JsonObject> addCars(@Part("action") RequestBody action,
                                 @Part("user_id") RequestBody UserID,
                                 @Part("reg_number") RequestBody RegisterNumber,
                                 @Part("model") RequestBody Model,
                                 @Part("make_year") RequestBody MakingYear,
                                 @Part("car_id") RequestBody CarId,
                                 @Part("is_default") RequestBody is_default);

        @Multipart
        @POST(FileName)
        Call<JsonElement> addProperty(@Part("action") RequestBody action,
                                         @Part("user_id") RequestBody UserID,
                                         @Part MultipartBody.Part image1,
                                         @Part MultipartBody.Part image2,
                                         @Part("title") RequestBody PropertyTitle,
                                         @Part("address") RequestBody PropertyAddress,
                                         @Part("area1") RequestBody AreaWidth,
                                         @Part("area2") RequestBody AreaHeight,
                                         @Part("max_car") RequestBody MaxCar,
                                         @Part("about") RequestBody AboutProperty,
                                         @Part("parking_type") RequestBody PropertyType,
                                         @Part("sunday") RequestBody SundayAvailability,
                                         @Part("monday") RequestBody MondayAvailability,
                                         @Part("tuesday") RequestBody TuesdayAvailability,
                                         @Part("wednesday") RequestBody WednesdayAvailability,
                                         @Part("thursday") RequestBody ThursdayAvailability,
                                         @Part("friday") RequestBody FridayAvailability,
                                         @Part("saturday") RequestBody SaturdayAvailability,
                                         @Part("short_time") RequestBody ShortTime,
                                         @Part("hourly_time") RequestBody HourTime,
                                         @Part("daily_time") RequestBody DailyTime,
                                         @Part("weekly_time") RequestBody WeeklyTime,
                                         @Part("monthly_time") RequestBody MonthlyTime,
                                         @Part("lat") RequestBody Latitude,
                                         @Part("lng") RequestBody Longitude,
                                         @Part("property_id") RequestBody PropertyID
        );
        @Multipart
        @POST(FileName)
        Call<JsonElement> addProperty(@Part("action") RequestBody action,
                                      @Part("user_id") RequestBody UserID,
                                      @Part MultipartBody.Part image1,
                                      @Part("title") RequestBody PropertyTitle,
                                      @Part("address") RequestBody PropertyAddress,
                                      @Part("area1") RequestBody AreaWidth,
                                      @Part("area2") RequestBody AreaHeight,
                                      @Part("max_car") RequestBody MaxCar,
                                      @Part("about") RequestBody AboutProperty,
                                      @Part("parking_type") RequestBody PropertyType,
                                      @Part("sunday") RequestBody SundayAvailability,
                                      @Part("monday") RequestBody MondayAvailability,
                                      @Part("tuesday") RequestBody TuesdayAvailability,
                                      @Part("wednesday") RequestBody WednesdayAvailability,
                                      @Part("thursday") RequestBody ThursdayAvailability,
                                      @Part("friday") RequestBody FridayAvailability,
                                      @Part("saturday") RequestBody SaturdayAvailability,
                                      @Part("short_time") RequestBody ShortTime,
                                      @Part("hourly_time") RequestBody HourTime,
                                      @Part("daily_time") RequestBody DailyTime,
                                      @Part("weekly_time") RequestBody WeeklyTime,
                                      @Part("monthly_time") RequestBody MonthlyTime,
                                      @Part("lat") RequestBody Latitude,
                                      @Part("lng") RequestBody Longitude,
                                      @Part("property_id") RequestBody PropertyID
        );


        @Multipart
        @POST(FileName)
        Call<JsonElement> addProperty(@Part("action") RequestBody action,
                                      @Part("user_id") RequestBody UserID,
                                      @Part("img1") String image1,
                                      @Part MultipartBody.Part image2,
                                      @Part("title") RequestBody PropertyTitle,
                                      @Part("address") RequestBody PropertyAddress,
                                      @Part("area1") RequestBody AreaWidth,
                                      @Part("area2") RequestBody AreaHeight,
                                      @Part("max_car") RequestBody MaxCar,
                                      @Part("about") RequestBody AboutProperty,
                                      @Part("parking_type") RequestBody PropertyType,
                                      @Part("sunday") RequestBody SundayAvailability,
                                      @Part("monday") RequestBody MondayAvailability,
                                      @Part("tuesday") RequestBody TuesdayAvailability,
                                      @Part("wednesday") RequestBody WednesdayAvailability,
                                      @Part("thursday") RequestBody ThursdayAvailability,
                                      @Part("friday") RequestBody FridayAvailability,
                                      @Part("saturday") RequestBody SaturdayAvailability,
                                      @Part("short_time") RequestBody ShortTime,
                                      @Part("hourly_time") RequestBody HourTime,
                                      @Part("daily_time") RequestBody DailyTime,
                                      @Part("weekly_time") RequestBody WeeklyTime,
                                      @Part("monthly_time") RequestBody MonthlyTime,
                                      @Part("lat") RequestBody Latitude,
                                      @Part("lng") RequestBody Longitude,
                                      @Part("property_id") RequestBody PropertyID
        );

        @Multipart
        @POST(FileName)
        Call<JsonElement> addProperty(@Part("action") RequestBody action,
                                      @Part("user_id") RequestBody UserID,
                                      @Part MultipartBody.Part image1,
                                      @Part("img2") String image2,
                                      @Part("title") RequestBody PropertyTitle,
                                      @Part("address") RequestBody PropertyAddress,
                                      @Part("area1") RequestBody AreaWidth,
                                      @Part("area2") RequestBody AreaHeight,
                                      @Part("max_car") RequestBody MaxCar,
                                      @Part("about") RequestBody AboutProperty,
                                      @Part("parking_type") RequestBody PropertyType,
                                      @Part("sunday") RequestBody SundayAvailability,
                                      @Part("monday") RequestBody MondayAvailability,
                                      @Part("tuesday") RequestBody TuesdayAvailability,
                                      @Part("wednesday") RequestBody WednesdayAvailability,
                                      @Part("thursday") RequestBody ThursdayAvailability,
                                      @Part("friday") RequestBody FridayAvailability,
                                      @Part("saturday") RequestBody SaturdayAvailability,
                                      @Part("short_time") RequestBody ShortTime,
                                      @Part("hourly_time") RequestBody HourTime,
                                      @Part("daily_time") RequestBody DailyTime,
                                      @Part("weekly_time") RequestBody WeeklyTime,
                                      @Part("monthly_time") RequestBody MonthlyTime,
                                      @Part("lat") RequestBody Latitude,
                                      @Part("lng") RequestBody Longitude,
                                      @Part("property_id") RequestBody PropertyID
        );

        @Multipart
        @POST(FileName)
        Call<JsonElement> addProperty(@Part("action") RequestBody action,
                                      @Part("user_id") RequestBody UserID,
                                      @Part("img1") String image1,
                                      @Part("img2") String image2,
                                      @Part("title") RequestBody PropertyTitle,
                                      @Part("address") RequestBody PropertyAddress,
                                      @Part("area1") RequestBody AreaWidth,
                                      @Part("area2") RequestBody AreaHeight,
                                      @Part("max_car") RequestBody MaxCar,
                                      @Part("about") RequestBody AboutProperty,
                                      @Part("parking_type") RequestBody PropertyType,
                                      @Part("sunday") RequestBody SundayAvailability,
                                      @Part("monday") RequestBody MondayAvailability,
                                      @Part("tuesday") RequestBody TuesdayAvailability,
                                      @Part("wednesday") RequestBody WednesdayAvailability,
                                      @Part("thursday") RequestBody ThursdayAvailability,
                                      @Part("friday") RequestBody FridayAvailability,
                                      @Part("saturday") RequestBody SaturdayAvailability,
                                      @Part("short_time") RequestBody ShortTime,
                                      @Part("hourly_time") RequestBody HourTime,
                                      @Part("daily_time") RequestBody DailyTime,
                                      @Part("weekly_time") RequestBody WeeklyTime,
                                      @Part("monthly_time") RequestBody MonthlyTime,
                                      @Part("lat") RequestBody Latitude,
                                      @Part("lng") RequestBody Longitude,
                                      @Part("property_id") RequestBody PropertyID
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> addCard(@Field("action") String action,
                                 @Field("user_id") String UserID,
                                 @Field("title") String CardHolderName,
                                 @Field("number") String CardNumber,
                                 @Field("months") String ExpiryMonth,
                                 @Field("years") String ExpiryYear,
                                 @Field("cvv") String CVV,
                                 @Field("card_id") String CardID,
                                 @Field("is_default") String is_default);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getCards(@Field("action") String action, @Field("user_id") String UserID);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> deleteCards(@Field("action") String action, @Field("user_id") String UserID,@Field("card_id") String CardID);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> addReport(@Field("action") String action, @Field("from_user_id") String UserID,@Field("report_desc") String Resident,@Field("booking_id") String bookingID);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> addReport(@Field("action") String action, @Field("from_user_id") String UserID,@Field("to_user_id") String ToUserID,@Field("report_desc") String Resident,@Field("booking_id") String bookingId);



        @FormUrlEncoded
        @POST("api/getNearByProperty/")
        Call<JsonObject> getNearByProperty(@Field("action") String action,
                                           @Field("property_type") String PropertyType,
                                           @Field("placeid") String PlaceID,
                                           @Field("rating") String Rating,
                                           @Field("lat") String Lantitude,
                                           @Field("lng") String Longitude,
                                           @Field("availability_date") String adate,
                                           @Field("availability_time") String atime,
                                           @Field("short_stay_price") String short_stay_price,
                                           @Field("hourly_stay_price") String hourly_stay_price,
                                           @Field("daily_stay_price") String daily_stay_price,
                                           @Field("weekly_stay_price") String weekly_stay_price,
                                           @Field("monthly_stay_price") String monthly_stay_price,
                                           @Field("your_location") String your_location,
                                           @Field("search_location") String search_location
                                           );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> addBooking(@Field("action") String action,
                                    @Field("user_id") String UserID,
                                    @Field("property_id") String PropertyID,
                                    @Field("category_name") String StayTime,
                                    @Field("slot_name") String SlotTime,
                                    @Field("hourlydatetime") String hourDateTime,
                                    @Field("booking_start_date") String BookingStartDate,
                                    @Field("booking_end_date") String BookingEndDate,
                                    @Field("booking_start_time") String BookingStartTime,
                                    @Field("booking_end_time") String BookingEndTime,
                                    @Field("distance") String Distance,
                                    @Field("duration") String Duration,
                                    @Field("booking_id") String BookingId

        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getBookings(@Field("action") String action, @Field("user_id") String UserID,@Field("filter") String filter);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getMaxPrice(@Field("action") String action);


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> setNotificationOnOff(@Field("action") String action, @Field("user_id") String UserID,@Field("is_notify") String is_notify);


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getBookingsList(@Field("action") String action,@Field("property_id") String propertyID,@Field("filter") String filter);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getDateBookingsList(@Field("action") String action,@Field("property_id") String propertyID,@Field("filter") String filter);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getDateBookings(@Field("action") String action,
                                         @Field("user_id") String UserID,
                                         @Field("filter_date") String filter_date,
                                         @Field("filter") String filter);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> cancelBookings(@Field("action") String action, @Field("booking_id") String BookingID);

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> cancelExtendBookings(@Field("action") String action, @Field("booking_extend_id") String BookingID);



        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> exportEmail(@Field("action") String action,
                                    @Field("user_id") String UserID,
                                    @Field("email") String Email
        );

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> exportPDF(@Field("action") String action,
                                     @Field("user_id") String UserID,
                                     @Field("filter") String Filter,
                                     @Field("start_date") String StartDate,
                                     @Field("end_date") String EndDate
        );

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> exportPDFYear(@Field("action") String action,
                                   @Field("user_id") String UserID,
                                   @Field("filter") String Filter,
                                   @Field("month") String Month,
                                   @Field("year") String Year
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> exportPDFMonth(@Field("action") String action,
                                   @Field("user_id") String UserID,
                                   @Field("filter") String Filter,
                                   @Field("months") String StartMonth,
                                   @Field("year") String EndMonth
        );

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> makePayment(@Field("action") String action,
                                        @Field("booking_id") String BookingID,
                                        @Field("card_id") String CardID,
                                        @Field("stripeToken") String StripeToken,
                                        @Field("booking_extend_id") String ExtendID,
                                     @Field("car_id") String CarID

        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> rating(@Field("action") String action,
                                     @Field("property_id") String propertyID,
                                     @Field("user_id") String UserID,
                                     @Field("rating") String Ratings
        );

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> extendBooking(@Field("action") String action,
                                @Field("property_id") String propertyID,
                                @Field("user_id") String UserID,
                                @Field("category_name") String CategoryName,
                                       @Field("slot_name") String SlotName,
                                       @Field("booking_start_date") String booking_start_date,
                                       @Field("booking_end_date") String booking_end_date,
                                       @Field("booking_start_time") String booking_start_time,
                                       @Field("booking_end_time") String booking_end_time,
                                       @Field("distance") String distance,
                                       @Field("duration") String duration,
                                       @Field("booking_id") String booking_id,
                                       @Field("extend_booking") int extendBooking


        );

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getNotification(@Field("action") String action,
                                       @Field("user_id") String UserID
        );

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> terms_and_condition(@Field("action") String action,
                                             @Field("pagecode") String terms
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> affiliation(@Field("action") String action
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> changePassword(@Field("action") String action,
                                        @Field("password") String Password,
                                        @Field("user_id") String UserID,
                                        @Field("old_password") String OldPassword
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> logoutSession(@Field("action") String action,
                                        @Field("token_key") String token_key,
                                       @Field("device_token") String device_token,
                                       @Field("user_id") String userid
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> addFeedback(@Field("action") String action,
                                       @Field("user_id") String userid,
                                     @Field("experience_content") String experience_content,
                                     @Field("feature_content") String feature_content
        );


        @GET(FileName)
        Call<JsonObject> howAppWork(@Query("action") String action
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> addBank(@Field("action") String action,
                                     @Field("user_id") String userid,
                                     @Field("bsb") String bsb,
                                     @Field("account_name") String account_name,
                                     @Field("account_number") String account_number,
                                     @Field("bank_id") String bank_id
        );

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getBank(@Field("action") String action,
                                 @Field("user_id") String userid
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getPropertyDetail( @Field("action") String action,
                                            @Field("user_id") String userid,
                                            @Field("property_id") String property_id
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> setDefaultCard( @Field("action") String action,
                                            @Field("user_id") String userid,
                                            @Field("card_id") String card_id
        );

        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getProfile( @Field("action") String action,
                                         @Field("user_id") String userid
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> setDefaultCar( @Field("action") String action,
                                     @Field("user_id") String userid,@Field("car_id") String car_id
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getWithDrawAmount( @Field("action") String action,
                                     @Field("user_id") String userid,
                                     @Field("wallet_amount") String walletamount
        );


        @FormUrlEncoded
        @POST(FileName)
        Call<JsonObject> getBookedCarList( @Field("action") String action,
                                            @Field("date") String date,
                                            @Field("property_id") String propertyID,
                                           @Field("slot_name") String slot_name
        );


    }
}
