package org.techtown.ColorfulCard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    Intent intent;
    ArrayList<MemberStore> mealMemberStore;
    ArrayList<MemberStore> sideMealMemberStore;
    ArrayList<MemberStore> eduMemberStore;
    static ArrayList<MemberStore> favorMemberStore;
    Marker gagulMarker=null;
    static String userID;
    private GoogleMap googleMap;
    View card_view;
    Button mealBtn, sideBtn, eduBtn, favorBtn;
    ImageButton call, emptyStar, fullStar;
    ImageView searchimage;
    ArrayList<Marker> mealMarker = new ArrayList<Marker>();
    ArrayList<Marker> sideMealMarker = new ArrayList<Marker>();
    ArrayList<Marker> eduMarker = new ArrayList<Marker>();
    ArrayList<Marker> favorMarker = new ArrayList<Marker>();
    Boolean mealBtnFlag =false;
    Boolean sideBtnFlag =false;
    Boolean eduBtnFlag =false;
    Boolean favorBtnFlag =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        intent = getIntent();
        mealMemberStore=intent.getParcelableArrayListExtra("mealMemberStores"); //???????????? arraylist??? ????????? ??? ?????????
        sideMealMemberStore=intent.getParcelableArrayListExtra("sideMealMemberStores");
        eduMemberStore=intent.getParcelableArrayListExtra("eduMemberStores");
        favorMemberStore=intent.getParcelableArrayListExtra("favorMemberStores");
        userID= intent.getStringExtra("userID");

        for( int i=0;i<favorMemberStore.size();i++) {

            int favorSid= favorMemberStore.get(i).getSid();

            for( int j=0;j<mealMemberStore.size();j++){
                if( mealMemberStore.get(j).getSid()==favorSid){
                    mealMemberStore.remove(j);
                    Log.d("tag",favorSid+"?????? ???????????? ?????????");
                    break;
                }
            }
            for( int j=0;j<sideMealMemberStore.size();j++){
                if( sideMealMemberStore.get(j).getSid()==favorSid){
                    sideMealMemberStore.remove(j);
                    break;
                }
            }
            for( int j=0;j<eduMemberStore.size();j++){
                if( eduMemberStore.get(j).getSid()==favorSid){
                    eduMemberStore.remove(j);
                    break;
                }
            }
        }
        for(MemberStore store : sideMealMemberStore)  //?????????
        {
            System.out.println("??????: " + store.getStype());
            System.out.println("??????: " + store.getSaddress());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        card_view=findViewById(R.id.card_view);
        mealBtn = (Button) findViewById(R.id.btn1);  //????????????
        sideBtn = (Button) findViewById(R.id.btn2);  //????????????
        eduBtn = (Button) findViewById(R.id.btn3);  //????????????
        favorBtn = (Button) findViewById(R.id.btn4); //????????????

        searchimage = (ImageView) findViewById(R.id.sv_location);
        call=(ImageButton)findViewById(R.id.call);
        emptyStar=(ImageButton)findViewById(R.id.emptyStar);
        fullStar=(ImageButton)findViewById(R.id.fullStar);

        searchimage.getBackground().setAlpha(140);

        searchimage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SearchStoreActivity.class);
                startActivity(i);
            }


        });

        //???????????? ????????? ?????? ????????????!
        //?????? ???????????? ??????. ?????? ?????? ????????? ?????? ?????? ???????????? ??????????????? ????????? ??????????????? ?????? ???????????? ????????? ????????????.
        //?????? ?????? ?????? ????????? ?????? ??????????????? ????????????. ????????? ??????,?????? ????????? ????????? ???????????? ?????? ???????????? ????????????.

        mealBtn.setOnClickListener(new View.OnClickListener() {  //???????????????
            @Override
            public void onClick(View v) {

                mealBtnFlag =!mealBtnFlag;
                if(mealBtnFlag) //true??? ????????? ?????????
                {
                    if(sideBtnFlag==true){      //?????? ??????????????? ??????????????? ???????????? ????????? ????????? ??? ????????????. ????????? ????????? ????????? ????????? ??????
                        sideBtnFlag=false;
                        sideBtn.setBackgroundColor(Color.parseColor("#16A085"));
                    }
                    if(eduBtnFlag==true){
                        eduBtnFlag=false;
                        eduBtn.setBackgroundColor(Color.parseColor("#FFDB58"));
                    }
                    if(favorBtnFlag==true){
                        favorBtnFlag=false;
                        favorBtn.setBackgroundColor(Color.parseColor("#ff4040"));
                    }

                    for (Marker marker : mealMarker) {
                        marker.setVisible(true);
                    }
                    for (Marker marker : sideMealMarker) {
                        marker.setVisible(false);
                    }
                    for (Marker marker : eduMarker) {
                        marker.setVisible(false);
                    }
                    for (Marker marker : favorMarker) {

                        //?????? ????????? ???????????? ????????? ????????????.
                        MemberStore store= (MemberStore) marker.getTag();
                        if(store.getStype().equals("??????")){
                            Log.d("tag",store.getSname()+"  "+store.getSid());
                            marker.setVisible(true);
                        }else{
                            marker.setVisible(false);
                        }
                    }
                    mealBtn.setBackgroundColor(Color.parseColor("#133A55"));   //????????? ??? ??????
                }
                else //false??? ?????? ??? ?????????
                {
                    if(sideBtnFlag ==false && eduBtnFlag ==false) {

                        for (Marker marker : sideMealMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : eduMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : mealMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : favorMarker) {
                            marker.setVisible(true);
                        }
                    }
                    mealBtn.setBackgroundColor(Color.parseColor("#2980B9"));  //????????? ??? ????????????
                }
            }
        });


        sideBtn.setOnClickListener(new View.OnClickListener() { //???????????????
            @Override
            public void onClick(View v) {

                sideBtnFlag =!sideBtnFlag;
                if(sideBtnFlag)
                {
                    if(mealBtnFlag==true){
                        mealBtnFlag=false;
                        mealBtn.setBackgroundColor(Color.parseColor("#2980B9"));
                    }
                    if(eduBtnFlag==true){
                        eduBtnFlag=false;
                        eduBtn.setBackgroundColor(Color.parseColor("#FFDB58"));
                    }
                    if(favorBtnFlag==true){
                        favorBtnFlag=false;
                        favorBtn.setBackgroundColor(Color.parseColor("#ff4040"));
                    }

                    for(Marker marker : sideMealMarker) {
                        marker.setVisible(true);
                    }
                    for(Marker marker : mealMarker) {
                        marker.setVisible(false);
                    }
                    for(Marker marker : eduMarker){
                        marker.setVisible(false);
                    }
                    for (Marker marker : favorMarker) {
                        //?????? ????????? ???????????? ????????? ????????????.
                        MemberStore store= (MemberStore) marker.getTag();
                        //  Log.d("tag",store.getStype()+"");
                        if(store.getStype().equals("??????")){
                            marker.setVisible(true);
                        }else{
                            marker.setVisible(false);
                        }
                    }
                    sideBtn.setBackgroundColor(Color.parseColor("#0B4D40"));
                }
                else{
                    if(mealBtnFlag ==false && eduBtnFlag ==false) {

                        for(Marker marker : sideMealMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : mealMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : eduMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : favorMarker) {
                            marker.setVisible(true);
                        }
                    }
                    sideBtn.setBackgroundColor(Color.parseColor("#16A085"));

                }
            }

        });

        eduBtn.setOnClickListener(new View.OnClickListener() { //???????????????
            @Override
            public void onClick(View v) {

                eduBtnFlag = !eduBtnFlag;
                if (eduBtnFlag) {

                    if(sideBtnFlag==true){      //?????? ??????????????? ??????????????? ???????????? ????????? ????????? ??? ????????????. ????????? ????????? ????????? ????????? ??????
                        sideBtnFlag=false;
                        sideBtn.setBackgroundColor(Color.parseColor("#16A085"));
                    }
                    if(mealBtnFlag==true){
                        mealBtnFlag=false;
                        mealBtn.setBackgroundColor(Color.parseColor("#2980B9"));
                    }
                    if(favorBtnFlag==true){
                        favorBtnFlag=false;
                        favorBtn.setBackgroundColor(Color.parseColor("#ff4040"));
                    }

                    for (Marker marker : eduMarker) {
                        marker.setVisible(true);
                    }
                    for (Marker marker : mealMarker) {
                        marker.setVisible(false);
                    }
                    for (Marker marker : sideMealMarker) {
                        marker.setVisible(false);
                    }
                    for (Marker marker : favorMarker) {
                        //?????? ????????? ???????????? ????????? ????????????.
                        MemberStore store= (MemberStore) marker.getTag();
                        if(store.getStype().equals("??????")){
                            marker.setVisible(true);
                        }else{
                            marker.setVisible(false);
                        }
                    }
                    eduBtn.setBackgroundColor(Color.parseColor("#D9A800"));

                }else{

                    if(mealBtnFlag ==false&& sideBtnFlag ==false) {
                        for (Marker marker : mealMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : sideMealMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : favorMarker) {
                            marker.setVisible(true);
                        }

                        for (Marker marker : eduMarker) {
                            marker.setVisible(true);
                        }
                    }

                    eduBtn.setBackgroundColor(Color.parseColor("#FFDB58"));
                }

            }
        });

        favorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                favorBtnFlag = !favorBtnFlag;
                if (favorBtnFlag) {   //??????????????? ????????????

                    if(sideBtnFlag==true){      //?????? ??????????????? ??????????????? ???????????? ????????? ????????? ??? ????????????. ??????????????? ????????? ????????? ????????? ??????
                        sideBtnFlag=false;
                        sideBtn.setBackgroundColor(Color.parseColor("#16A085"));
                    }
                    if(mealBtnFlag==true){
                        mealBtnFlag=false;
                        mealBtn.setBackgroundColor(Color.parseColor("#2980B9"));
                    }
                    if(eduBtnFlag==true){
                        eduBtnFlag=false;
                        eduBtn.setBackgroundColor(Color.parseColor("#FFDB58"));
                    }

                    for (Marker marker : eduMarker) {
                        marker.setVisible(false);
                    }
                    for (Marker marker : mealMarker) {
                        marker.setVisible(false);
                    }
                    for (Marker marker : sideMealMarker) {
                        marker.setVisible(false);
                    }
                    for (Marker marker : favorMarker) {
                        MemberStore store= (MemberStore) marker.getTag();

                        System.out.println(store.getSid()+ " "+ store.getSname());
                        marker.setVisible(true);

                    }

                    favorBtn.setBackgroundColor(Color.parseColor("#A82929"));
                }
                else {

                    if (mealBtnFlag == false && sideBtnFlag == false && eduBtnFlag == false) {
                        for (Marker marker : mealMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : sideMealMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : eduMarker) {
                            marker.setVisible(true);
                        }
                        for (Marker marker : favorMarker) {
                            MemberStore store= (MemberStore) marker.getTag();
                            System.out.println(store.getSid()+ " "+ store.getSname());
                            marker.setVisible(true);
                        }

                    }
                    favorBtn.setBackgroundColor(Color.parseColor("#ff4040"));
                }
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //?????? ?????? ??????
        mapFragment.getMapAsync(this);
        //??????????????? onMapReadyCallback????????? ?????????


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //map??? ???????????? ???????????? ??????
        this.googleMap = googleMap;
        // 35.8691036023011, 128.59554606027856 ????????? ?????????
        LatLng latLng = new LatLng(35.8691036023011, 128.59554606027856);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        //????????? ???????????? ????????? ??????????????? ?????? ????????? ????????????


        //??????????????? ??????
        BitmapDrawable bitmapDraw=(BitmapDrawable)getResources().getDrawable(R.drawable.bluemarker);
        Bitmap b=bitmapDraw.getBitmap();
        Bitmap mealCustomMarker = Bitmap.createScaledBitmap(b, 60, 60, false);

        for (MemberStore store : mealMemberStore) {

            MarkerOptions markerOptions = new MarkerOptions();
            //????????? ?????? ????????? ?????? ?????? ??????
            markerOptions.position(new LatLng(store.getLatitude(), store.getSlongitude()))
                    .title(store.getSname())
                    .icon(BitmapDescriptorFactory.fromBitmap(mealCustomMarker));

            Marker marker =googleMap.addMarker(markerOptions);
            marker.setTag(store);
            mealMarker.add(marker);
        }
        //??????????????? ??????
        BitmapDrawable bitmapDraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.greenmarker);
        Bitmap b2=bitmapDraw2.getBitmap();
        Bitmap sideCustomMarker = Bitmap.createScaledBitmap(b2, 60, 60, false);

        for(MemberStore store: sideMealMemberStore) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(store.getLatitude(),store.getSlongitude()))
                    .title(store.getSname())
                    .icon(BitmapDescriptorFactory.fromBitmap(sideCustomMarker));

            Marker marker =googleMap.addMarker(markerOptions);
            marker.setTag(store);
            sideMealMarker.add(marker);
        }


        //??????????????? ??????
        BitmapDrawable bitmapDraw3=(BitmapDrawable)getResources().getDrawable(R.drawable.yellowmarker);
        Bitmap b3=bitmapDraw3.getBitmap();
        Bitmap eduCustomMarker = Bitmap.createScaledBitmap(b3, 60, 60, false);

        for(MemberStore store: eduMemberStore) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(store.getLatitude(),store.getSlongitude()))
                    .title(store.getSname())
                    .icon(BitmapDescriptorFactory.fromBitmap(eduCustomMarker));

            Marker marker =googleMap.addMarker(markerOptions);
            marker.setTag(store);
            eduMarker.add(marker);
        }

        //??????????????? ??????
        BitmapDrawable bitmapDraw4=(BitmapDrawable)getResources().getDrawable(R.drawable.favormarker);
        Bitmap b4=bitmapDraw4.getBitmap();
        Bitmap favorCustomMarker = Bitmap.createScaledBitmap(b4, 60, 60, false);

        for(MemberStore store: favorMemberStore) {
            Log.d("tag","??????????????????");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(store.getLatitude(),store.getSlongitude()))
                    .title(store.getSname())
                    .icon(BitmapDescriptorFactory.fromBitmap(favorCustomMarker));

            Marker marker =googleMap.addMarker(markerOptions);
            marker.setTag(store);
            favorMarker.add(marker);
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermissionWithRationale();
        }

        this.googleMap.setOnMarkerClickListener(markerClickListener);
        this.googleMap.setOnMapClickListener(mapClickListener);

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermissionWithRationale() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("????????????")
                        .setMessage("??? ?????? ???????????? ???????????? ??????????????? ????????? ???????????????. ???????????? ????????? ???????????? ?????????.")
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(@NonNull @NotNull LatLng latLng) {
            card_view.setVisibility(View.GONE);

            if(gagulMarker!=null){

                MemberStore store= (MemberStore) gagulMarker.getTag();
                String stype= store.getStype();
                BitmapDrawable bitmapDraw;

                if(favorMemberStore.contains(store)){
                    bitmapDraw=(BitmapDrawable)getResources().getDrawable(R.drawable.favormarker);
                }else {
                    if (stype.equals("??????")) {
                        bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bluemarker);

                    } else if (stype.equals("??????")) {
                        bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.yellowmarker);

                    } else {
                        bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.greenmarker);
                    }
                }
                Bitmap b4=bitmapDraw.getBitmap();
                Bitmap customMarker = Bitmap.createScaledBitmap(b4, 60, 60, false);
                gagulMarker.setIcon(BitmapDescriptorFactory.fromBitmap(customMarker));

            }

        }


    };

    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {


            if(gagulMarker!=null && !gagulMarker.equals(marker)){

                MemberStore store= (MemberStore) gagulMarker.getTag();
                String stype= store.getStype();
                BitmapDrawable bitmapDraw;

                if(favorMemberStore.contains(store)){
                    Log.d("tag",store.getSid()+"?????????");

                    bitmapDraw=(BitmapDrawable)getResources().getDrawable(R.drawable.favormarker);
                }
                else {
                    Log.d("tag",store.getSid()+"????????????");
                    if (stype.equals("??????")) {

                        bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bluemarker);

                    } else if (stype.equals("??????")) {
                        bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.yellowmarker);

                    } else {
                        bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.greenmarker);
                    }
                }
                Bitmap b4=bitmapDraw.getBitmap();
                Bitmap customMarker = Bitmap.createScaledBitmap(b4, 60, 60, false);
                gagulMarker.setIcon(BitmapDescriptorFactory.fromBitmap(customMarker));

            }

            gagulMarker=marker;

            card_view.setVisibility(View.VISIBLE);
            TextView name = (TextView)findViewById(R.id.st_name);
            TextView num =(TextView)findViewById(R.id.st_num);
            TextView address=(TextView)findViewById(R.id.st_address);

            MemberStore store = (MemberStore) marker.getTag();

            name.setText(store.getSname());
            num.setText(store.getSnum());
            address.setText(store.getSaddress());

            String st_num=store.getSnum().toString();
            st_num=st_num.replace("-","");
            String tell;
            tell="tel:"+st_num;


            String stype= store.getStype();
            BitmapDrawable bitmapDrawGagul=null;


            if(favorMemberStore.contains(store)){    //??????????????? ????????? ??????????????????

                bitmapDrawGagul=(BitmapDrawable)getResources().getDrawable(R.drawable.redgagul);
                Log.d("tag","????????? ????????? ??????"+store.getSname());
                emptyStar.setVisibility(View.GONE);
                fullStar.setVisibility(View.VISIBLE);

            }else{

                if(stype.equals("??????")){

                    bitmapDrawGagul=(BitmapDrawable)getResources().getDrawable(R.drawable.bluegagul);

                }else if(stype.equals("??????")){

                    bitmapDrawGagul=(BitmapDrawable)getResources().getDrawable(R.drawable.yellowgagul);
                }
                else {
                    bitmapDrawGagul = (BitmapDrawable) getResources().getDrawable(R.drawable.greengagul);
                }

                emptyStar.setVisibility(View.VISIBLE);
                fullStar.setVisibility(View.GONE);

            }

            Bitmap gagul=bitmapDrawGagul.getBitmap();
            Bitmap gagulCustomMarker = Bitmap.createScaledBitmap(gagul, 100, 100, false);

            //  marker.setVisible(false);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(gagulCustomMarker));
            marker.setVisible(true);


            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(tell));
                    startActivity(intent);
                }
            });

            //??? ?????? ????????? -> ??????????????? ???????????????
            emptyStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //emptyStar-> gone , fullStar-> visible
                    emptyStar.setVisibility(View.GONE);
                    fullStar.setVisibility(View.VISIBLE);

                    //favorMemberStore??? ?????? ?????? ????????????
                    favorMemberStore.add(store);
                    Log.d("tag",store.getSid()+"?????????");

                    //?????? ?????? ??????????????? ????????????
                    //??????????????? ??????
                    BitmapDrawable bitmapDraw4=(BitmapDrawable)getResources().getDrawable(R.drawable.favormarker);
                    Bitmap b4=bitmapDraw4.getBitmap();
                    Bitmap favorCustomMarker = Bitmap.createScaledBitmap(b4, 60, 60, false);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(favorCustomMarker));

                    //???????????? ???????????? ??????
                    favorMarker.add(marker);

                    //DB??? ???????????? ??????

                    int sid = store.getSid();
                    registerFavoriteStore(userID, sid);




                }
            });

            // ?????? ?????? ????????? -> ??????????????? ???????????????
            fullStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //fullStar-> gone  , emptyStar-> visible
                    fullStar.setVisibility(View.GONE);
                    emptyStar.setVisibility(View.VISIBLE);

                    //favorMemberStore??? ?????? ?????? ?????????
                    int deleteSid =store.getSid();

                    ArrayList<MemberStore> copyfavorMemberStore= favorMemberStore;

                    favorMemberStore = new ArrayList<MemberStore>();
                    for(int i=0; i<copyfavorMemberStore.size();i++){
                        MemberStore store= copyfavorMemberStore.get(i);

                        int sid = store.getSid();
                        if(sid!=deleteSid){
                            favorMemberStore.add(store);
                        }
                    }

                    for(MemberStore store: favorMemberStore){
                       Log.d("tag","favorMemeberStore: "+ store.getSname()+"\n");
                    }

                    //?????? ?????? ????????? ????????? ?????? ????????? ????????????
                    //??????????????? ??????

                    String stype= store.getStype();
                    BitmapDrawable bitmapDraw;
                    Bitmap b4;
                    Bitmap customMarker;

                    if(stype.equals("??????")){

                        bitmapDraw=(BitmapDrawable)getResources().getDrawable(R.drawable.bluemarker);
                        b4=bitmapDraw.getBitmap();
                        customMarker = Bitmap.createScaledBitmap(b4, 60, 60, false);

                        marker.setVisible(false);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(store.getLatitude(),store.getSlongitude()))
                                .title(store.getSname())
                                .icon(BitmapDescriptorFactory.fromBitmap(customMarker));

                        Marker newMarker =googleMap.addMarker(markerOptions);
                        newMarker.setTag(store);


                    }else if(stype.equals("??????")){
                        bitmapDraw=(BitmapDrawable)getResources().getDrawable(R.drawable.yellowmarker);
                        b4=bitmapDraw.getBitmap();
                        customMarker = Bitmap.createScaledBitmap(b4, 60, 60, false);

                        marker.setVisible(false);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(store.getLatitude(),store.getSlongitude()))
                                .title(store.getSname())
                                .icon(BitmapDescriptorFactory.fromBitmap(customMarker));

                        Marker newMarker =googleMap.addMarker(markerOptions);
                        newMarker.setTag(store);


                    }else{
                        bitmapDraw=(BitmapDrawable)getResources().getDrawable(R.drawable.greenmarker);
                        b4=bitmapDraw.getBitmap();
                        customMarker = Bitmap.createScaledBitmap(b4, 60, 60, false);

                        marker.setVisible(false);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(store.getLatitude(),store.getSlongitude()))
                                .title(store.getSname())
                                .icon(BitmapDescriptorFactory.fromBitmap(customMarker));

                        Marker newMarker =googleMap.addMarker(markerOptions);
                        newMarker.setTag(store);

                    }


                     bitmapDraw=(BitmapDrawable)getResources().getDrawable(R.drawable.favormarker);
                     b4=bitmapDraw.getBitmap();
                     customMarker = Bitmap.createScaledBitmap(b4, 60, 60, false);
                     favorMarker = new ArrayList<Marker>();

                    for(MemberStore store: favorMemberStore) {
                        System.out.println(store.getSname());
                        Log.d("tag","??????????????????");

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(store.getLatitude(),store.getSlongitude()))
                                .title(store.getSname())
                                .icon(BitmapDescriptorFactory.fromBitmap(customMarker));

                        Marker marker =googleMap.addMarker(markerOptions);
                        marker.setTag(store);
                        marker.setVisible(true);
                        favorMarker.add(marker);
                    }

                    for(Marker marker: favorMarker){
                        MemberStore store = (MemberStore)marker.getTag();
                        System.out.println("favorMarker: "+ store.getSname()+"\n");
                    }

                    //DB??? ???????????? ??????
                    int sid = store.getSid();
                  releaseFavoriteStore(userID, sid);
                }
            });
            return false;
        }
    };


    void registerFavoriteStore(String userId, int sid){
        Server server = new Server();
        RetrofitService service= server.getRetrofitService();
        Call<Integer> call=  service.postFavoriteStore(userID,sid);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if(response.isSuccessful()){
                    Log.d("tag",response.body()+"??? ?????? ?????????");
                    if(response.body().intValue()==1){
                        Toast.makeText(getApplicationContext(), "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void releaseFavoriteStore(String userID, int sid){
        Server server = new Server();
        RetrofitService service= server.getRetrofitService();
        Call<Integer> call= service.deleteFavoriteStore(userID,sid);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()){
                    if(response.body().intValue()==1){
                        Toast.makeText(getApplicationContext(), "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

}