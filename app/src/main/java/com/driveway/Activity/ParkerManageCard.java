package com.driveway.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;

import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.CardTbl;
import com.driveway.Model.tblCard;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkerManageCard extends BaseActivity implements View.OnClickListener {

    ListView lvCards;
    TTextView btnAddCard;
    AppCompatImageView img,back;
    ArrayList<tblCard> cards=new ArrayList<>();
    cardAdapter adapter;
    String pageName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_manage_card);
        pageName=getIntent().getStringExtra("page");
        bindComponent();
        adapter=new cardAdapter(this);
        lvCards.setAdapter(adapter);
        whiteStatusBar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCardAPI();
    }

    @Override
    public void onBackPressed() {

        setResult(Activity.RESULT_OK,getIntent().putExtra("size",cards.size()).putExtra("CARD",cards.size()>0?adapter.getItem(0):null));
        finish();
    }
    private void bindComponent(){
        lvCards=findViewById(R.id.lvCards);
        btnAddCard=findViewById(R.id.btnAddCard);
        img=findViewById(R.id.img);
        back=findViewById(R.id.back);

        btnAddCard.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void getCardAPI(){
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            }else {
                Utility.showProgress(ParkerManageCard.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getCards(WebUtility.GETCARD,appPreferences.getString("USERID"));
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>"+response.body().toString());
                                    JSONObject jsonObject=new JSONObject(response.body().toString());
                                    if(jsonObject!=null){
                                        System.out.println("Error Code==>"+jsonObject.getString("error_code"));
                                        if(jsonObject.getString("error_code").equalsIgnoreCase("0")){
                                            JSONArray array=jsonObject.getJSONArray("data");
                                            if(array!=null && array.length()>0){
                                                cards.clear();
                                                for(int i=0;i<array.length();i++){
                                                    tblCard card=new tblCard();
                                                    card.CardID=array.getJSONObject(i).getString("id")!=null?array.getJSONObject(i).getString("id"):"0";
                                                    card.OwnerName=array.getJSONObject(i).getString("title")!=null?array.getJSONObject(i).getString("title"):"DummyCard";
                                                    card.CardNo=array.getJSONObject(i).getString("number")!=null?array.getJSONObject(i).getString("number"):"11111111111111111";
                                                    card.CardMonth=array.getJSONObject(i).getString("months")!=null?array.getJSONObject(i).getString("months"):"10";
                                                    card.CardYear=array.getJSONObject(i).getString("years")!=null?array.getJSONObject(i).getString("years"):"10";
                                                    card.CVV=array.getJSONObject(i).getString("cvv")!=null?array.getJSONObject(i).getString("cvv"):"100";
                                                    card.isDefault=array.getJSONObject(i).getString("is_default")!=null?array.getJSONObject(i).getString("is_default"):"0";

                                                    cards.add(card);
                                                }
                                                adapter.notifyDataSetChanged();
                                                if(cards.size()>0){
                                                    img.setVisibility(View.GONE);
                                                    btnAddCard.setVisibility(View.VISIBLE);
                                                    lvCards.setVisibility(View.VISIBLE);

                                                    Collections.sort(cards, new Comparator< tblCard >() {
                                                        @Override public int compare(tblCard p1, tblCard p2) {
                                                            return Integer.parseInt(p2.isDefault)- Integer.parseInt(p1.isDefault); // Descending
                                                        }
                                                    });

                                                }else{
                                                    img.setVisibility(View.VISIBLE);
                                                    btnAddCard.setVisibility(View.VISIBLE);
                                                    lvCards.setVisibility(View.GONE);
                                                }
                                            }else{
                                                lvCards.setVisibility(View.GONE);
                                                //Utility.showAlert(ParkerManageCard.this,"No Cards Found");
                                            }
                                        }else {
                                            if(cards.size()>0){
                                                img.setVisibility(View.GONE);
                                                btnAddCard.setVisibility(View.GONE);
                                                lvCards.setVisibility(View.VISIBLE);
                                            }else{
                                                img.setVisibility(View.VISIBLE);
                                                btnAddCard.setVisibility(View.VISIBLE);
                                                lvCards.setVisibility(View.GONE);
                                            }
                                            //Utility.showAlert(ParkerManageCard.this,"No Cards Found");
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        //System.out.println("RESPONSE IS=====" + t.getMessage());
                        Utility.hideProgress();
                    }
                });
            }
        }catch (Exception ex){}
    }


    private void setDefaultCardAPI(String cardID,int position){
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            }else {
                Utility.showProgress(ParkerManageCard.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.setDefaultCard(WebUtility.SET_DEFAULT_CARD,
                        appPreferences.getString("USERID"),
                        cardID
                );
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    JSONObject jsonObject=new JSONObject(response.body().toString());
                                    if(jsonObject!=null){

                                        int code=jsonObject.optInt("error_code");
                                        if(code==0){
                                            setResult(Activity.RESULT_OK,getIntent().putExtra("CARD",adapter.getItem(position)).putExtra("size",cards.size()));
                                            finish();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        //System.out.println("RESPONSE IS=====" + t.getMessage());
                        Utility.hideProgress();
                    }
                });
            }
        }catch (Exception ex){}
    }

    private void deleteCardAPI(String cardID){
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            }else {
                Utility.showProgress(ParkerManageCard.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.deleteCards(WebUtility.DELETECARD,appPreferences.getString("USERID"),cardID);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>"+response.body().toString());
                                    JSONObject jsonObject=new JSONObject(response.body().toString());
                                    if(jsonObject!=null){
                                        System.out.println("Error Code==>"+jsonObject.getString("error_code"));
                                        String message=jsonObject.getString("error_message")!=null?jsonObject.getString("error_message"):"";
                                        if(jsonObject.getString("error_code").equalsIgnoreCase("0")){
                                            Utility.showAlert(ParkerManageCard.this,message);
                                            adapter.notifyDataSetChanged();
                                            if(cards.size()>0){

                                                img.setVisibility(View.GONE);
                                                btnAddCard.setVisibility(View.GONE);
                                                lvCards.setVisibility(View.VISIBLE);
                                            }else{
                                                img.setVisibility(View.VISIBLE);
                                                btnAddCard.setVisibility(View.VISIBLE);
                                                lvCards.setVisibility(View.GONE);
                                            }
                                        }else {
                                            Utility.showAlert(ParkerManageCard.this,message);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println("RESPONSE IS=====" + t.getMessage());
                        Utility.hideProgress();
                    }
                });
            }
        }catch (Exception ex){}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                setResult(Activity.RESULT_OK,getIntent().putExtra("size",cards.size()).putExtra("CARD",cards.size()>0?adapter.getItem(0):null));
                finish();break;
            case R.id.btnAddCard:
                startActivity(new Intent(this,ParkerAddNewCard.class));
                break;
        }
    }

    private void addDefaultCard(CardTbl cardTbl){

        try{
            dataContext.cardTbls.fill();
            for(int i=0;i<dataContext.cardTbls.size();i++)
                dataContext.cardTbls.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.cardTbls.save();
            cardTbl.setStatus(Entity.STATUS_NEW);
            dataContext.cardTbls.save(cardTbl);
    //        finish();
        }catch (Exception ex){

        }
    }

    public class cardAdapter extends BaseAdapter{

        Context context;
        public cardAdapter(Context context){
            this.context=context;
        }
        @Override
        public int getCount() {
            return cards.size()>0?cards.size():0;
        }

        @Override
        public tblCard getItem(int position) {
            return cards.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            holder holder;
            if(convertView==null){
                convertView= LayoutInflater.from(context).inflate(R.layout.card_row,parent,false);
                holder=new holder(convertView);
                convertView.setTag(holder);
            }else{
                holder= (cardAdapter.holder) convertView.getTag();
            }
            if(cards.size()>0) {
                tblCard card = cards.get(position);
                if(card!=null) {
                    holder.cardownername.setText(card.OwnerName != null ? card.OwnerName : "");
                    if (card.CardNo != null && !card.CardNo.isEmpty()) {
                        if (card.CardNo.length() > 5 && card.CardNo.length()==16) {
                            System.out.println("CARD NO=====>"+card.CardNo);
                            holder.cardno.setText(card.CardNo.substring(12, 16));
//                            holder.tvcard1.setText(card.CardNo.substring(0, 4));
//                            holder.tvcard2.setText(card.CardNo.substring(4, 8));
//                            holder.tvcard3.setText(card.CardNo.substring(8, 12));
                        }
                        else
                            Utility.showAlert(ParkerManageCard.this, "Card No is Wrong");
                    }
                    holder.cardExpiryDate.setText("EXPIRY DATE : " +card.CardMonth+"/"+card.CardYear);

                    holder.imgDeleteCard.setOnClickListener(v -> {
                        if(card.isDefault.equalsIgnoreCase("1")){
                            new AlertDialog.Builder(ParkerManageCard.this)
                                    .setTitle("")
                                    .setCancelable(false)
                                    .setMessage("This is default card.so you can't delete this card.Please set another default card then delete this card")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();

                        }else {
                            new AlertDialog.Builder(ParkerManageCard.this)
                                    .setTitle("")
                                    .setCancelable(false)
                                    .setMessage("Are you sure you want to delete " + card.OwnerName + " credit card detail?")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (card != null && card.CardID != null)
                                                deleteCardAPI(card.CardID + "");
                                            cards.remove(position);
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }

                    });
                    holder.imgEditCard.setOnClickListener(v -> context.startActivity(new Intent(context,ParkerAddNewCard.class).putExtra("card",card)));

                    holder.rlCard.setOnClickListener(v -> {
                        CardTbl cardTbl=new CardTbl();
                        cardTbl.CardID=card.CardID;
                        cardTbl.CardOwnerName=card.OwnerName;
                        cardTbl.CardCVV=card.CVV;
                        cardTbl.CardMonth=card.CardMonth;
                        cardTbl.CardYear=card.CardYear;
                        cardTbl.CardNo=card.CardNo;
                        addDefaultCard(cardTbl);
                        if(pageName.equalsIgnoreCase("booking")) {
                            setResult(Activity.RESULT_OK, getIntent().putExtra("CARD", adapter.getItem(position)).putExtra("size", cards.size()));
                            finish();
                        }
                        //setDefaultCardAPI(card.CardID,position);

                    });
                    if(card.isDefault.equalsIgnoreCase("1")){
                        holder.default_card.setVisibility(View.VISIBLE);

                    }else{
                        holder.default_card.setVisibility(View.INVISIBLE);
                    }
                }
            }

            return convertView;
        }

        class holder{
            TTextView cardownername,cardno,tvcard1,tvcard2,tvcard3,cardExpiryDate,default_card;
            AppCompatImageView imgDeleteCard,imgEditCard;
            RelativeLayout rlCard;
//            ImageView default_card;
            public holder(View v){
                cardownername=v.findViewById(R.id.cardownername);
                cardno=v.findViewById(R.id.cardno);
                tvcard1=v.findViewById(R.id.tvcard1);
                tvcard2=v.findViewById(R.id.tvcard2);
                tvcard3=v.findViewById(R.id.tvcard3);
                cardExpiryDate=v.findViewById(R.id.cardExpiryDate);
                imgDeleteCard=v.findViewById(R.id.imgDeleteCard);
                imgEditCard=v.findViewById(R.id.imgEditCard);
                rlCard=v.findViewById(R.id.rlCard);
                default_card=v.findViewById(R.id.default_card);
            }
        }
    }
}
