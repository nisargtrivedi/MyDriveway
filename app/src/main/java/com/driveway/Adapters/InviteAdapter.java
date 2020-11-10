package com.driveway.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.driveway.Component.TTextView;
import com.driveway.Model.ChatModel;
import com.driveway.Model.ContactModel;
import com.driveway.Model.ParkerBookingList;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.Utility.DataContext;
import com.driveway.listeners.onInviteClick;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InviteAdapter extends BaseAdapter implements Filterable {

   public ArrayList<ContactModel> list;
    public List<ContactModel> listSearch;
    Context context;
    LayoutInflater inflater;
    onInviteClick click;
    public InviteAdapter(Context context, ArrayList<ContactModel> list) {
        this.context=context;
        this.list = list;
        listSearch=list;
        inflater=LayoutInflater.from(context);
    }

    public void LayoutCLickListener(onInviteClick click){
        this.click=click;
    }
    @Override
    public int getCount() {
        return listSearch.size();
    }

    @Override
    public ContactModel getItem(int position) {
        return listSearch.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;

        if(convertView==null){
            convertView=inflater.inflate(R.layout.invite_row,parent,false);
            holder=new Holder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (Holder) convertView.getTag();
        }
        ContactModel model=listSearch.get(position);
        if(model!=null){
            holder.tvName.setText(model.Name);
            holder.tvPhone.setText(model.Phone);
            holder.tv_character.setText(model.Name.charAt(0)+"");

            if(position==0){
                holder.tv_character.setBackgroundResource(R.drawable.round_character_fill_invite);
            }
            else if(position%4==0){
                holder.tv_character.setBackgroundResource(R.drawable.round_character_fill_invite_four);
            }
            else if(position%3==0){
                holder.tv_character.setBackgroundResource(R.drawable.round_character_fill_invite_three);
            }
            else if(position%2==0){
                holder.tv_character.setBackgroundResource(R.drawable.round_character_fill_invite_two);
            }else{
                    holder.tv_character.setBackgroundResource(R.drawable.round_character_fill_invite_five);
            }
        }
        if(model.isSelect==true){
            holder.rlMain.setBackgroundResource(R.drawable.round_invite);
        }else{
            holder.rlMain.setBackgroundResource(0);
        }
        holder.rlMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                click.onLongClick(model);
                return false;
            }
        });
        holder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(model);
            }
        });
        holder.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(model);
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listSearch = list;
                } else {
                    List<ContactModel> filteredList = new ArrayList<>();
                    for (ContactModel row : list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.Name.toLowerCase().contains(charString.toLowerCase()) || row.Phone.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    listSearch = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listSearch;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listSearch = (ArrayList<ContactModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Holder{

        TextView tvName,tvPhone,btnInvite,tv_character;
        RelativeLayout rlMain;
        public Holder(View v){
            tv_character=v.findViewById(R.id.tv_character);
            btnInvite=v.findViewById(R.id.btnInvite);
            rlMain=v.findViewById(R.id.rlMain);
            tvPhone=v.findViewById(R.id.tvPhone);
            tvName=v.findViewById(R.id.tvName);
            rlMain=v.findViewById(R.id.rlMain);
        }
    }
    public void selecttAll(){
        for(int i=0;i<list.size();i++){
            list.get(i).isSelect=true;
        }
        notifyDataSetChanged();
    }
    public void deselectAll(){
        for(int i=0;i<list.size();i++){
            list.get(i).isSelect=false;
        }
        notifyDataSetChanged();
    }
    public ArrayList<String> phoneNumbers(){
        ArrayList<String> allNumber=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(list.get(i).isSelect==true)
                allNumber.add(list.get(i).Phone);
        }
        return allNumber;
    }
}
