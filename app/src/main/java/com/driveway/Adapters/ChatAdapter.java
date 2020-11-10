package com.driveway.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.driveway.Component.TTextView;
import com.driveway.Model.ChatModel;
import com.driveway.Model.ParkerBookingList;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.DataContext;
import com.google.firebase.storage.FirebaseStorage;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends BaseAdapter {

    ArrayList<ChatModel> list;
    Context context;
    LayoutInflater inflater;
    ParkerBookingList obj;
    DataContext dataContext;
    public ChatAdapter(Context context, ArrayList<ChatModel> list, ParkerBookingList obj) {
        this.context=context;
        dataContext=new DataContext(context);
        this.list = list;
        this.obj=obj;
        inflater=LayoutInflater.from(context);
        try {
            dataContext.tblUserObjectSet.fill();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ChatModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;

        if(convertView==null){

            convertView=inflater.inflate(R.layout.chat_row,parent,false);
            holder=new Holder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (Holder) convertView.getTag();
        }

        ChatModel model=list.get(position);
       if(list.size()>0) {
           if (model != null) {

               if (position == 0) {
                   holder.ll.setVisibility(View.VISIBLE);
                   holder.tvDateTime.setText(DateFormat.format("EEEE,dd MMM yyyy", new Date(model.timeStamp * 1000)).toString() + "");
               } else {
                   if (position > 0) {
                       if (DateFormat.format("EEEE,dd MMM yyyy", new Date(model.timeStamp * 1000)).equals(DateFormat.format("EEEE,dd MMM yyyy", new Date(list.get(position - 1).timeStamp * 1000)))) {
                           holder.ll.setVisibility(View.GONE);
                       } else {
                           holder.ll.setVisibility(View.VISIBLE);
                           holder.tvDateTime.setText(DateFormat.format("EEEE,dd MMM yyyy", new Date(model.timeStamp * 1000)).toString() + "");
                       }
                   }
               }


               if (!model.messageType.equalsIgnoreCase("text")) {
                   holder.tvMessage.setVisibility(View.GONE);
                   holder.img.setVisibility(View.VISIBLE);
                   Picasso.with(context).load(model.message).fit().into(holder.img);


                   if (!model.sender.equalsIgnoreCase(dataContext.tblUserObjectSet.get(0).APIToken)) {
                       holder.rlMain.setGravity(Gravity.LEFT);
                       holder.llMain.setBackgroundColor(Color.parseColor("#A64AA56D"));
                       holder.llMain.setPadding(5, 5, 5, 5);
                       holder.UserCharacter.setVisibility(View.VISIBLE);
                   } else {
                       holder.rlMain.setGravity(Gravity.RIGHT);
                       holder.llMain.setPadding(5, 5, 5, 5);
                       holder.llMain.setBackgroundColor(Color.parseColor("#BF2F4858"));
                       holder.UserCharacter.setVisibility(View.GONE);
                   }

               } else {
                   holder.tvMessage.setVisibility(View.VISIBLE);
                   holder.img.setVisibility(View.GONE);
                   holder.tvMessage.setText(model.message);
                   holder.tvDate.setText(DateFormat.format("hh:mm a", new Date(model.timeStamp * 1000)).toString() + "");

                   if (!model.sender.equalsIgnoreCase(dataContext.tblUserObjectSet.get(0).APIToken)) {
                       holder.rlMain.setGravity(Gravity.LEFT);
                       holder.llMain.setBackgroundResource(R.drawable.chat_left);
                       holder.UserCharacter.setVisibility(View.VISIBLE);
                   } else {
                       holder.rlMain.setGravity(Gravity.RIGHT);
                       holder.llMain.setBackgroundResource(R.drawable.chat_right);
                       holder.UserCharacter.setVisibility(View.GONE);
                   }
               }
               holder.tvDate.setText(DateFormat.format("hh:mm a", new Date(model.timeStamp * 1000)).toString() + "");
               if (dataContext.tblUserObjectSet.get(0).APIToken != null)
                   holder.UserCharacter.setText(dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(obj.owneruser.APIToken) ? obj.user.FullName.charAt(0) + "" : obj.owneruser.FullName.charAt(0) + "");
           }
       }
        return convertView;
    }
    public class Holder{
        TTextView tvMessage,tvDate,UserCharacter,tvDateTime;
        RelativeLayout rlMain;
        ImageView img;
        LinearLayout llMain,ll;
        public Holder(View v){
            tvMessage=v.findViewById(R.id.tvMessage);
            tvDate=v.findViewById(R.id.tvDate);
            rlMain=v.findViewById(R.id.rlMain);
            llMain=v.findViewById(R.id.llMain);
            UserCharacter=v.findViewById(R.id.UserCharacter);
            tvDateTime=v.findViewById(R.id.tvDateTime);
            ll=v.findViewById(R.id.ll);
            img=v.findViewById(R.id.img);
        }
    }
}
