package com.driveway.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Component.RoundCornersImageView;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.DBHelper.tblPropertyImage;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.Utility.DataContext;
import com.driveway.listeners.onImageDelete;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class OwnerParkingImageAdapter extends  RecyclerView.Adapter<OwnerParkingImageAdapter.ViewHolder> {


    public List<tblPropertyImage> list;
    Context context;
    public String name;
    public onImageDelete ondelete;

    DataContext dataContext;
    public OwnerParkingImageAdapter(Context context, List<tblPropertyImage> list,onImageDelete delete) {
        this.context = context;
        this.list = list;
        this.ondelete=delete;
        dataContext=new DataContext(context);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public AppCompatImageView img_edit,img_delete;
        public RoundCornersImageView imgParkingSpace;

        public ViewHolder(View view) {
            super(view);
            imgParkingSpace = view.findViewById(R.id.img);
            img_edit = view.findViewById(R.id.img_edit);
            img_delete = view.findViewById(R.id.img_delete);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.parking_image_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//         setFadeAnimation(holder.itemView);


        final tblPropertyImage task = list.get(position);
        if(task!=null) {
            try {
                if (task.ImageType != null && !task.ImageType.isEmpty()) {
                    if (task.ImageType.equalsIgnoreCase("gallery")) {
                        holder.imgParkingSpace.setImageBitmap(BitmapFactory.decodeFile(task.ImagePath));
                        holder.img_edit.setVisibility(View.VISIBLE);
                        holder.img_delete.setVisibility(View.VISIBLE);
                        //Picasso.with(context).load(Uri.parse(task.ImagePath)).fit().into(holder.imgParkingSpace);
                    } else if (task.ImageType.equalsIgnoreCase("online")) {
                        if(context.getClass().getName().equalsIgnoreCase("com.driveway.Activity.OwnerPropertyDetailScreen")) {
                            holder.img_edit.setVisibility(View.GONE);
                            holder.img_delete.setVisibility(View.GONE);
                        }else{
                            holder.img_edit.setVisibility(View.VISIBLE);
                            holder.img_delete.setVisibility(View.VISIBLE);
                        }
                        if(!task.ImagePath.isEmpty()) {
                            if (task.ImagePath.startsWith("http") || task.ImagePath.startsWith("https"))
                                Picasso.with(context).load(Uri.parse(task.ImagePath)).into(holder.imgParkingSpace);
                            else {
                                holder.imgParkingSpace.setImageBitmap(BitmapFactory.decodeFile(task.ImagePath));
                            }
                        }
                    } else {
                        holder.imgParkingSpace.setImageBitmap(BitmapFactory.decodeFile(task.ImagePath));
                        holder.img_edit.setVisibility(View.VISIBLE);
                        holder.img_delete.setVisibility(View.VISIBLE);
                    }
                    holder.img_delete.setOnClickListener(v -> ondelete.onDelete(task));
                    holder.img_edit.setOnClickListener(v -> ondelete.onEdit(task));
                }
            } catch (Exception ex) {

            }

        }


    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

    public void add(tblPropertyImage img){
        list.add(img);
        notifyDataSetChanged();
    }
    public void remove(tblPropertyImage img){
        list.remove(img);
        notifyDataSetChanged();
    }
}
