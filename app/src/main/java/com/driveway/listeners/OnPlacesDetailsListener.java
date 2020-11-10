package com.driveway.listeners;

import com.driveway.Model.PlaceDetails;

public interface OnPlacesDetailsListener {

    void onPlaceDetailsFetched(PlaceDetails placeDetails);
    /**
     * Triggered when there is an error and passes the error message along as a parameter
     */
    void onError(String errorMessage);
}
