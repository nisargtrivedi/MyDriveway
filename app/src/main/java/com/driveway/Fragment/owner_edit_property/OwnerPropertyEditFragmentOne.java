package com.driveway.Fragment.owner_edit_property;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.driveway.Activity.OwnerEditProperty.OwnerPropertyEditScreen;
import com.driveway.Activity.OwnerPropertyAddScreen;
import com.driveway.Activity.OwnerPropertyDetailScreen;
import com.driveway.Component.BButton;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyDetail;
import com.driveway.Fragment.OwnerPropertyDetailsFragment;
import com.driveway.Model.PlaceDetails;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.DataContext;
import com.driveway.Utility.KeyBoardHandling;
import com.driveway.Utility.LocationProvider;
import com.driveway.WebApi.WebUtility;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class OwnerPropertyEditFragmentOne extends Fragment implements View.OnClickListener, LocationProvider.LocationCallback {

    private static final String LOG_TAG = "Google Places Autocomplete";
   public EEditText edtPropertyName,edtWidth,edtHeight,edtMaxCars,edtAboutProperty;
    TTextView Driveway,Carport,Grass;
    BButton btnNext;
    public androidx.appcompat.widget.AppCompatAutoCompleteTextView edtSearch;
    String ParkingType="";
    DataContext dataContext;
    LocationProvider provider;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private final String API_KEY = WebUtility.GOOGLE_MAP_KEY;
    ArrayList<PlaceDetails> resultList= new ArrayList<>();
    GooglePlacesAutocompleteAdapter placesAutocompleteAdapter;


    AppPreferences appPreferences;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreferences=new AppPreferences(getActivity());
        provider = new LocationProvider(getActivity(), this);
        provider.connect();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.owner_add_property_fragment_one, container, false);
        edtPropertyName=v.findViewById(R.id.edtPropertyName);
        edtSearch=v.findViewById(R.id.edtSearch);
        edtWidth=v.findViewById(R.id.edtWidth);
        edtHeight=v.findViewById(R.id.edtHeight);
        edtMaxCars=v.findViewById(R.id.edtMaxCars);
        edtAboutProperty=v.findViewById(R.id.edtAboutProperty);
        Driveway=v.findViewById(R.id.Driveway);
        Carport=v.findViewById(R.id.Carport);
        Grass=v.findViewById(R.id.Grass);
        btnNext=v.findViewById(R.id.btnNext);

        btnNext.setOnClickListener(this);
        Driveway.setOnClickListener(this);
        Carport.setOnClickListener(this);
        Grass.setOnClickListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
         //For show Logs
        //LogData();
        setData();
        KeyBoardHandling.hideSoftKeyboard(getActivity());
        super.onStart();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placesAutocompleteAdapter=new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.location_address);
        edtSearch.setAdapter(placesAutocompleteAdapter);
        edtSearch.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(edtSearch.getText().length()>0) {
                    if (event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        edtSearch.setText("");
                        edtSearch.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                        edtSearch.setText("");
                        edtSearch.dismissDropDown();
                        placesAutocompleteAdapter.getFilter().filter(edtSearch.getText().toString());
                        //KeyBoardHandling.hideSoftKeyboard(ParkerDashboardScreen.this);
                        return true;
                    }
                }
            }
            return false;
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drawable_red_close,0);
                    placesAutocompleteAdapter.getFilter().filter(s.toString());
                    edtSearch.dismissDropDown();
                }else{
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    edtSearch.dismissDropDown();
                    placesAutocompleteAdapter.getFilter().filter(edtSearch.getText().toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setData(){
        dataContext=new DataContext(getActivity());

        if(OwnerPropertyDetailScreen.parkingSpace!=null){

            try {
                //dataContext.parkingSpaceObjectSet.fill("parkingid = ?",new String[]{((OwnerPropertyEditScreen) getActivity()).parkingSpace.ParkingID},null);


//                edtPropertyName.setText(((OwnerPropertyEditScreen) getActivity()).parkingSpace.ParkingTitle);
//                edtAboutProperty.setText(((OwnerPropertyEditScreen) getActivity()).parkingSpace.AboutParking);
//                edtHeight.setText(((OwnerPropertyEditScreen) getActivity()).parkingSpace.Height);
//                edtWidth.setText(((OwnerPropertyEditScreen) getActivity()).parkingSpace.Width);
//                edtMaxCars.setText(((OwnerPropertyEditScreen) getActivity()).parkingSpace.MaximumCar);
//                edtSearch.setText(((OwnerPropertyEditScreen) getActivity()).parkingSpace.ParkingAddress);
//                ParkingType=((OwnerPropertyEditScreen) getActivity()).parkingSpace.ParkingTypes;

                edtPropertyName.setText(OwnerPropertyDetailScreen.parkingSpace.ParkingTitle);
                edtAboutProperty.setText(OwnerPropertyDetailScreen.parkingSpace.AboutParking);
                edtHeight.setText(OwnerPropertyDetailScreen.parkingSpace.Height);
                edtWidth.setText(OwnerPropertyDetailScreen.parkingSpace.Width);
                edtMaxCars.setText(OwnerPropertyDetailScreen.parkingSpace.MaximumCar);
                edtSearch.setText(OwnerPropertyDetailScreen.parkingSpace.ParkingAddress);
                ParkingType=(OwnerPropertyDetailScreen.parkingSpace.ParkingTypes);

                if(ParkingType.equalsIgnoreCase("Grass")){
                    selectionType(Grass,Driveway,Carport);
                }
                else if(ParkingType.equalsIgnoreCase("Driveway")){
                    selectionType(Driveway,Grass,Carport);
                }
                else if(ParkingType.equalsIgnoreCase("Carport")){
                    selectionType(Carport,Grass,Driveway);
                }

                        OwnerPropertyDetailScreen.parkingSpace.ParkingTitle=edtPropertyName.getText().toString().trim();
                        OwnerPropertyDetailScreen.parkingSpace.ParkingAddress=edtSearch.getText().toString().trim();
                        OwnerPropertyDetailScreen.parkingSpace.Height=edtHeight.getText().toString().trim();
                        OwnerPropertyDetailScreen.parkingSpace.Width=edtWidth.getText().toString().trim();
                        OwnerPropertyDetailScreen.parkingSpace.MaximumCar=edtMaxCars.getText().toString().trim();
                        OwnerPropertyDetailScreen.parkingSpace.ParkingTypes=ParkingType;
                        OwnerPropertyDetailScreen.parkingSpace.AboutParking=edtAboutProperty.getText().toString().trim();

//                try {
//                    try {
//                        dataContext.propertyDetailObjectSet.fill();
//                        if(dataContext.propertyDetailObjectSet.size()>0){
//                            dataContext.propertyDetailObjectSet.removeAll();
//                            dataContext.propertyDetailObjectSet.save();
//                        }
//                        tblPropertyDetail detail=new tblPropertyDetail();
//                        detail.PropertyName=edtPropertyName.getText().toString().trim();
//                        detail.PropertyAddress=edtSearch.getText().toString().trim();
//                        detail.PropertyWidth=edtHeight.getText().toString().trim();
//                        detail.PropertyHeight=edtWidth.getText().toString().trim();
//                        detail.PropertyMaxCars=edtMaxCars.getText().toString().trim();
//                        detail.PropertyParkingType=ParkingType;
//                        detail.PropertyAbout=edtAboutProperty.getText().toString().trim();
//                        detail.setStatus(Entity.STATUS_NEW);
//                        dataContext.propertyDetailObjectSet.save(detail);
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNext:
                try {
                    inputData();
                } catch (AdaFrameworkException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.Grass: selectionType(Grass,Driveway,Carport);break;
            case R.id.Driveway: selectionType(Driveway,Grass,Carport);break;
            case R.id.Carport: selectionType(Carport,Driveway,Grass);break;


        }
    }

    private void inputData() throws AdaFrameworkException {
        if(TextUtils.isEmpty(edtPropertyName.getText().toString().trim())){
            Utility.showAlert(getActivity(),"Please enter property name");
        }
        else if(TextUtils.isEmpty(edtSearch.getText().toString().trim())){
            Utility.showAlert(getActivity(),"Please enter property address");
        }
        else if(TextUtils.isEmpty(edtHeight.getText().toString().trim())){
            Utility.showAlert(getActivity(),"Please enter property long area");
        }
        else if(TextUtils.isEmpty(edtWidth.getText().toString().trim())){
            Utility.showAlert(getActivity(),"Please enter property wide area");
        }
        else if(TextUtils.isEmpty(edtMaxCars.getText().toString().trim())){
            Utility.showAlert(getActivity(),"Please select maximum cars");
        }
        else if(TextUtils.isEmpty(edtAboutProperty.getText().toString().trim())){
            Utility.showAlert(getActivity(),"Please enter about property");
        }
        else if(TextUtils.isEmpty(ParkingType)){
            Utility.showAlert(getActivity(),"Please select property type");
        }else{
            try {
                try {
//                    dataContext.propertyDetailObjectSet.fill();
//                    if(dataContext.propertyDetailObjectSet.size()>0){
//                        dataContext.propertyDetailObjectSet.removeAll();
//                        dataContext.propertyDetailObjectSet.save();
//                    }
//                    tblPropertyDetail detail=new tblPropertyDetail();
//                    detail.PropertyName=edtPropertyName.getText().toString().trim();
//                    detail.PropertyAddress=edtSearch.getText().toString().trim();
//                    detail.PropertyWidth=edtHeight.getText().toString().trim();
//                    detail.PropertyHeight=edtWidth.getText().toString().trim();
//                    detail.PropertyMaxCars=edtMaxCars.getText().toString().trim();
//                    detail.PropertyParkingType=ParkingType;
//                    detail.PropertyAbout=edtAboutProperty.getText().toString().trim();
//                    detail.setStatus(Entity.STATUS_NEW);
//                    dataContext.propertyDetailObjectSet.save(detail);

                    OwnerPropertyDetailScreen.parkingSpace.ParkingTitle=edtPropertyName.getText().toString().trim();
                    OwnerPropertyDetailScreen.parkingSpace.ParkingAddress=edtSearch.getText().toString().trim();
                    OwnerPropertyDetailScreen.parkingSpace.Height=edtHeight.getText().toString().trim();
                    OwnerPropertyDetailScreen.parkingSpace.Width=edtWidth.getText().toString().trim();
                    OwnerPropertyDetailScreen.parkingSpace.MaximumCar=edtMaxCars.getText().toString().trim();
                    OwnerPropertyDetailScreen.parkingSpace.ParkingTypes=ParkingType;
                    OwnerPropertyDetailScreen.parkingSpace.AboutParking=edtAboutProperty.getText().toString().trim();



                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
                    //For Show Logs
                    ((OwnerPropertyEditScreen)getActivity()).navigation(1);
        }

    }

    @Override
    public void handleNewLocation(Location location) {
        appPreferences.set("lat",location.getLatitude()+"");
        appPreferences.set("lng",location.getLongitude()+"");
    }

    private void selectionType(TTextView tv,TTextView tv2,TTextView tv3){
        switch (tv.getId()){
            case R.id.Grass:
                ParkingType="Grass";
                tv.setBackgroundResource(R.drawable.add_property_type_bg);
                tv2.setBackgroundResource(R.drawable.border_button_add_property);
                tv3.setBackgroundResource(R.drawable.border_button_add_property);
                break;
            case R.id.Driveway:
                ParkingType="Driveway";
                tv.setBackgroundResource(R.drawable.add_property_type_bg);
                tv2.setBackgroundResource(R.drawable.border_button_add_property);
                tv3.setBackgroundResource(R.drawable.border_button_add_property);
                break;
            case R.id.Carport:
                ParkingType="Carport";
                tv.setBackgroundResource(R.drawable.add_property_type_bg);
                tv2.setBackgroundResource(R.drawable.border_button_add_property);
                tv3.setBackgroundResource(R.drawable.border_button_add_property);
                break;
        }

    }

    public ArrayList<PlaceDetails> autocomplete(String input) {
        ArrayList<PlaceDetails> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&radius=100000");
            sb.append("&location="+appPreferences.getString("lat")+","+appPreferences.getString("lng"));

//            sb.append("&location="+appPreferences.getString("latitude")+","+appPreferences.getString("longitude"));
//            sb.append("&strictBounds=true&radius=5000000");
            //sb.append("&components=country:au");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            System.out.println("URL=====>"+sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            in.close();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            //System.out.println("SEARCH RESULT===="+jsonObj.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                PlaceDetails details=new PlaceDetails();
                details.PlaceDescription=predsJsonArray.getJSONObject(i).getString("description")!=null?predsJsonArray.getJSONObject(i).getString("description"):"";
                details.PLaceID=predsJsonArray.getJSONObject(i).getString("place_id")!=null?predsJsonArray.getJSONObject(i).getString("place_id"):"";
                details.PlaceMainText=predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text")!=null?predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"):"";
                //details.PlaceSecondaryText=predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text")!=null?predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text"):"";

//                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
//                System.out.println("============================================================");
                resultList.add(details);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<PlaceDetails> implements Filterable {


        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, R.layout.location_address);
        }

        @Override
        public int getCount() {
            return resultList!=null?resultList.size():0;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v=convertView;
            GooglePlacesAutocompleteAdapter.ViewHolder holder;
            if(v==null){
                v=LayoutInflater.from(getContext()).inflate(R.layout.location_address,parent,false);
                holder=new GooglePlacesAutocompleteAdapter.ViewHolder(v);
                v.setTag(holder);
            }else{
                holder= (GooglePlacesAutocompleteAdapter.ViewHolder) v.getTag();
            }
            if(resultList!=null) {
                if(resultList.size()>0) {
                    holder.tv.setText(resultList.get(position).PlaceDescription);
                    holder.tv2.setText(resultList.get(position).PlaceMainText);
                    holder.lvItem.setOnClickListener(v1 -> {

                        //System.out.println("PLACE ID========>"+resultList.get(position).PLaceID);
                        //Toast.makeText(getContext(), resultList.get(position).PLaceID, Toast.LENGTH_SHORT).show();
                        edtSearch.setText(resultList.get(position).PlaceDescription);
                        edtSearch.dismissDropDown();
                        //nearByLocation(resultList.get(position).PLaceID);
                    });
                }
            }
            return v;
        }

        @Override
        public PlaceDetails getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        class ViewHolder{
            TTextView tv,tv2;
            LinearLayout lvItem;
            public ViewHolder(View v){
                tv=v.findViewById(R.id.autocompleteText);
                tv2=v.findViewById(R.id.location_two);
                lvItem=v.findViewById(R.id.lvItem);
            }
        }
    }
}
