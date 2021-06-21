package com.pwj.helloya;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.pwj.BaseActivity;
import com.pwj.activity.CityDetailList;
import com.pwj.bean.Customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.pwj.tree.DataBaseHelper;
import com.umeng.analytics.MobclickAgent;

public class RequestActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.title_im)
    LinearLayout title_im;
    private MapView mMapView = null;
    private String numberOfCustomer = "???";
    private DataBaseHelper dataBaseHelper;
    Cursor querryResults;
    private double longitude =0.0;
    private double latitude = 0.0;
    BaiduMap baiduMap;
    ArrayList<Customer> customerArrayList;
    private TextView textViewSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout(R.layout.activity_request);
        ButterKnife.bind(this);
        title_im.setOnClickListener(this);
        customerArrayList= new ArrayList<Customer>();
        textViewSeekBar = (TextView) findViewById(R.id.textView2);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO show the number of customers.
                querryResults = querryDB(seekBar.getProgress());
                baiduMap.clear();
                int counter = 0;
                customerArrayList.clear();

                while (!querryResults.isAfterLast()) {

                    Customer customer = new Customer(querryResults.getInt(0), querryResults.getString(1), querryResults.getString(2),
                            querryResults.getString(3), querryResults.getString(4), querryResults.getString(5), querryResults.getDouble(6),
                            querryResults.getDouble(7), querryResults.getString(8), querryResults.getString(9), querryResults.getString(10),
                            querryResults.getString(11), querryResults.getString(12), querryResults.getString(13), querryResults.getString(14),
                            querryResults.getString(15), querryResults.getString(16), querryResults.getString(17));

                    customerArrayList.add(customer);
                    querryResults.moveToNext();
                }

                Collections.sort(customerArrayList, new SortByDis());


                // show nearest 10 results in map

                Customer nearestCustomer = null;
                try{
                    for (int i = 0; i < 10; i++){
                        addMarker(baiduMap, customerArrayList.get(i));
                    }
                    nearestCustomer  = customerArrayList.get(0);
                    if (nearestCustomer != null){
                        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(new LatLng(nearestCustomer.getLatitude(), nearestCustomer.getLongitude()));
                        baiduMap.setMapStatus(update);
                    }
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }

                textViewSeekBar.setText("距您" + String.valueOf(seekBar.getProgress()) + "公里之内， 共有"+ numberOfCustomer + "个潜在客户");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mMapView.getMap();
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Customer customer = (Customer) marker.getExtraInfo().get("customer");
                String infoShow = "名称：" + customer.getCompanyname() + "\n" +
                        "地址：" + customer.getAddress() + "\n" +
                        "距您约" + String.format("%.1f",DistanceUtil.getDistance(new LatLng(latitude,longitude), new LatLng(customer.getLatitude(), customer.getLongitude()))/1000) + "公里";

                InfoWindow infoWindow;
                TextView textView = new TextView(getApplicationContext());
                textView.setBackgroundColor(Color.argb(100,254,245, 231));
                textView.setTextColor(Color.argb(255,23,32,42));
                textView.setPadding(30,20,30,50);
                textView.setText(infoShow);

                final LatLng latLng= marker.getPosition();
                infoWindow = new InfoWindow(textView, latLng, -47);
                baiduMap.showInfoWindow(infoWindow);

                return true;
            }
        });

        Button resultDetail = (Button) findViewById(R.id.result_detail);
        resultDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customerArrayList.size() > 0){
                    Intent intent = new Intent(RequestActivity.this, CityDetailList.class);
                    intent.putExtra("type",1);
                    intent.putExtra("resultlist", (Serializable) customerArrayList);
                    startActivity(intent);
                    RequestActivity.this.finish();
                }
                else{
                    Toast.makeText(RequestActivity.this,"请滑动选择周边范围",Toast.LENGTH_SHORT).show();
                }
            }
        });
        initLocation(seekBar);
//        initData();
    }

    private void initLocation(SeekBar seekBar) {
        longitude=getIntent().getDoubleExtra("longitude",0.0);
        latitude=getIntent().getDoubleExtra("latitude",0.0);

        seekBar.setProgress(20);    //初始值设为20公里
    }

    public double[] getAround(double lon, double lat, int raidus){
        Double latitude = lat;
        Double longitude = lon;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        return new double[]{minLng, minLat, maxLng, maxLat};
    }

    public double getDis(double lon1, double lat1, double lon2, double lat2){
        double earthRaius = 6371.0; //km
        lat1 = convertDegreesToRadians(lat1);
        lon1 = convertDegreesToRadians(lon1);
        lat2 = convertDegreesToRadians(lat2);
        lon2 = convertDegreesToRadians(lon2);

        //差值
        double vLon = Math.abs(lon1 - lon2);
        double vLat = Math.abs(lat1 - lat2);

        //h is the great circle distance in radians, great circle就是一个球体上的切面，它的圆心即是球心的一个周长最大的圆。
        double h = haverSin(vLat) + Math.cos(lat1) * Math.cos(lat2) * haverSin(vLon);

        double distance = 2 * earthRaius * Math.asin(Math.sqrt(h));

        return distance;
    }

    //used for sort the querry results, not the true distance;
    public double getDisSort(double lon1, double lat1, double lon2, double lat2){
        double disSort = Math.pow(lon1-lon2, 2) + Math.pow(lat1-lat2, 2);
        return disSort;
    }

    public double convertDegreesToRadians(double degree){
        return degree * Math.PI / 180;
    }

    public double haverSin(double theta){
        return Math.pow(Math.sin(theta/2),2);
    }

    public Cursor querryDB(int distance){
        dataBaseHelper = new DataBaseHelper(this);
        SQLiteDatabase sqLiteDatabase;

        try{
            sqLiteDatabase = dataBaseHelper.openDatabase();
            double range[] = getAround(longitude,latitude,distance*1000);

            String sql = "select * from pwj_user where longitude>" + range[0] + " and longitude<" + range[2]
                    + " and latitude>" + range[1] + " and latitude<" + range[3] + ";";

            String sqlCount = "select count(*) from pwj_user where longitude>" + range[0] + " and longitude<" + range[2]
                    + " and latitude>" + range[1] + " and latitude<" + range[3] + ";";

            querryResults = sqLiteDatabase.rawQuery(sql, null);
            querryResults.moveToFirst();

            Cursor count = sqLiteDatabase.rawQuery(sqlCount,null);
            count.moveToFirst();
            numberOfCustomer = String.valueOf(count.getInt(0));


        } catch (SQLException e){
            throw e;
        }
        return querryResults;
    }


    private void addMarker(BaiduMap map, Customer customer){
        LatLng point = new LatLng(customer.getLatitude(), customer.getLongitude());
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.location_pointer_24);
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmapDescriptor).draggable(false).perspective(true);
        Marker marker = (Marker) (map.addOverlay(option));
        Bundle bundle = new Bundle();
        bundle.putSerializable("customer", customer);
        marker.setExtraInfo(bundle);

    }


    @Override
    public void onResume(){
        super.onResume();
//        mLocationClient.restart();
        mMapView.onResume();
//        MobclickAgent.onPageStart("RequestActivity"); //统计页面("MainScreen"为页面名称，可自定义)
//        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onPause(){
        super.onPause();
        mMapView.onPause();
//        MobclickAgent.onPageEnd("RequestActivity");
//        MobclickAgent.onPause(this); //统计时长
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onClick(View view) {
        RequestActivity.this.finish();

    }

    class SortByDis implements Comparator {
        public int compare(Object o1, Object o2){
            Customer customer1 = (Customer) o1;
            Customer customer2 = (Customer) o2;
            double dis1 =  getDisSort(longitude, latitude, customer1.getLongitude(), customer1.getLatitude());
            double dis2 = getDisSort(longitude, latitude, customer2.getLongitude(), customer2.getLatitude());
            if (dis1 > dis2){
                return 1;
            }
            return -1;
        }
    }

}
