package com.driveway.listeners;

import com.driveway.DBHelper.tblPropertyImage;
import com.driveway.Model.ParkingSpace;

public interface onImageDelete {

    void onDelete(tblPropertyImage image);
    void onEdit(tblPropertyImage image);

}
