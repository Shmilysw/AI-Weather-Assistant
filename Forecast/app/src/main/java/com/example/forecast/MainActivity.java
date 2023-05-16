package com.example.forecast;

import static com.example.forecast.db.DBManager.deleteInfoByCity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.forecast.city_manager.CityManagerActivity;
import com.example.forecast.db.DBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 这里是界面和其他内容
    ImageView addCityIv, moreIv;
    LinearLayout pointLayout;
    ViewPager mainVp;

    // ViewPager的数据
    List<Fragment> fragmentList;
    // 表示选中城市的集合
    List<String> cityList;
    // 表示ViewPager的页面指数器显示集合
    List<ImageView> imageList;
    private CityFragmentPageAdapter adapter;
    private SharedPreferences pref;
    private int bgNum;
    private View outLayout;

//    private String city;

    // 腾讯天气 API 的拼接
    // 当前位置
    // 杭州市
//     String url_location = "https://apis.map.qq.com/ws/location/v1/ip?ip=39.182.39.224&key=DZ5BZ-AL43T-RMIX6-LUPFG-7VUE3-GJBAO";
    String url_location = "https://apis.map.qq.com/ws/location/v1/ip?ip=&key=DZ5BZ-AL43T-RMIX6-LUPFG-7VUE3-GJBAO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化
       //        init();

//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        addCityIv = findViewById(R.id.main_iv_add);
        moreIv = findViewById(R.id.main_iv_more);
        pointLayout = findViewById(R.id.main_layout_point);
        mainVp = findViewById(R.id.main_vp);
        outLayout = findViewById(R.id.main_out_layout);
        exchangeBg(); // 更换壁纸函数
        // 添加点击事件
        addCityIv.setOnClickListener(this);
        moreIv.setOnClickListener(this);

        fragmentList = new ArrayList<>();
        // 获取数据库包含的城市信息列表
        cityList = DBManager.queryAllCityName();
        imageList = new ArrayList<>();

        // 获取当前位置天气
//        LocationWeather locationWeather = new LocationWeather();
//        fragmentList.add(locationWeather);

//        city = locationWeather.city;
//        System.out.println(city);
//        cityList.add(city);

//        System.out.println(locationWeather.getCity());
//        System.out.println("11111111111111111111111");

//        cityList.add(locationWeather.getCity());

        cityList.clear();

        getCityLocation();

        while (cityList.size() == 0) {
            System.out.println("9999999999999999999999");
        }
        Toast.makeText(this ,"当前位置天气情况！",Toast.LENGTH_LONG).show();

        System.out.println(cityList);
        System.out.println("888888888888888888888");

        if (cityList.size() == 0) {

//            cityList.add("杭州");
//            cityList.add("宁波");
        }
        // 因为可能搜索界面点击跳转此界面，会传值，所以此处获取一下
        try {
            Intent intent = getIntent();
            String city = intent.getStringExtra("city");
            if (!cityList.contains(city) && !TextUtils.isEmpty(city)) {
                cityList.add(city);
            }
        } catch (Exception e) {
            Log.i("animee","程序出现问题了！！");
        }

        /* 因为可能搜索界面点击跳转此界面，会传值，所以此处获取一下*/
//        try {
//            Intent intent = getIntent();
//            String city = intent.getStringExtra("city");
//            if (!cityList.contains(city)&&!TextUtils.isEmpty(city)) {
//                cityList.add(city);
//            }
//        }catch (Exception e){
//            Log.i("animee","程序出现问题了！！");
//        }
        
        // 初始化ViewPager页面的方法
        initPager();

        adapter = new CityFragmentPageAdapter(getSupportFragmentManager(), fragmentList);
        mainVp.setAdapter(adapter);
        // 创建小圆点指示器
        initPoint();
        // 设置最后一个城市信息
        mainVp.setCurrentItem(fragmentList.size() - 1);
        // 设置ViewPager页面监听
        setPagerListener();


        // 修改背景铺满任务栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            //getWindow.setStatusBarColor(Color.TRANSPARENT)
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }

    // 获取当前位置
    private void getCityLocation() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url_location).build();
        String city;
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String content = response.body().string();
//                System.out.println("1111111111111111111111");
//                System.out.println(content);
//                System.out.println("1111111111111111111111");

                try {
                    JSONObject jsonObjectALL = new JSONObject(content);
                    JSONObject jsonObject1 = jsonObjectALL.getJSONObject("result");
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("ad_info");
                    String city = jsonObject2.getString("city");
                    String province = jsonObject2.getString("province");
//                    System.out.println("222222222222222222222");
//                    System.out.println(city);
//                    System.out.println("222222222222222222222");

                    // 截取
                    String location_city = province + " " + city.substring(0, 2);
//                    System.out.println(location_city);
//                    int idx = 0;
//                    for (int i = 0; i < city.length() ; i ++ ) {
//                        System.out.println(location_city[i]);
//                    }
                    System.out.println(location_city + "222222222222222222");
                    // 获取数据库里的数据
                    List<String> list = DBManager.queryAllCityName();
                    System.out.println(list);
                    System.out.println("listlist-listlist");
                    // 判断当前位置是否改变
                    cityList.add(location_city);
                    System.out.println(cityList);
                    System.out.println("33333333333333333333");



                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }




    // 换壁纸的函数
    public void exchangeBg() {
        pref = getSharedPreferences("bg_pref", MODE_PRIVATE);
        bgNum = pref.getInt("bg", 4);
        switch (bgNum) {
            case 0:
                outLayout.setBackgroundResource(R.mipmap.bg);
                break;
            case 1:
                outLayout.setBackgroundResource(R.mipmap.bg2);
                break;
            case 2:
                outLayout.setBackgroundResource(R.mipmap.bg3);
                break;
            case 3:
                outLayout.setBackgroundResource(R.mipmap.bg7);
                break;
            case 4:
                outLayout.setBackgroundResource(R.mipmap.bg6);
                break;
        }
    }


    private void setPagerListener() {
        // 设置监听事件
        mainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            // 表示页面所选择的位置
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < imageList.size() ; i ++ ) {
                    imageList.get(i).setImageResource(R.mipmap.a1);
                }
                imageList.get(position).setImageResource(R.mipmap.a2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPoint() {
        // 创建小圆点 ViewPager页面指示器的函数
        for (int i = 0; i < fragmentList.size() ; i ++ ) {
            ImageView pIv = new ImageView(this);
            pIv.setImageResource(R.mipmap.a1);
            pIv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pIv.getLayoutParams();
            lp.setMargins(0, 0, 20, 0);
            imageList.add(pIv);
            pointLayout.addView(pIv);
        }
        imageList.get(imageList.size() - 1).setImageResource(R.mipmap.a2);
    }

    private void initPager() {
        // 创建Fragment对象，添加到ViewPager数据源当中
        System.out.println(cityList);
        System.out.println("777777777777777777");
        for (int i = 0; i < cityList.size(); i ++ ) {
            System.out.println(cityList);
            System.out.println("66666666666666666666");
            CityWeatherFragment cwFrag = new CityWeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("city", cityList.get(i));
            cwFrag.setArguments(bundle);
            fragmentList.add(cwFrag);
        }
    }

    // 点击按钮页面跳转
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v.getId() == R.id.main_iv_add) {
            intent.setClass(this, CityManagerActivity.class);
        }
        else if (v.getId() == R.id.main_iv_more) {
            intent.setClass(this, MoreActivity.class);
        }
        startActivity(intent);
    }

    // 当页面重新加载时会调用的函数，这个函数在页面获取焦点之前进行调用，此处完成ViewPager页面的更新
    @Override
    protected void onRestart() {
        super.onRestart();

        // 获取数据库当中还剩下的城市集合
        List<String> list = DBManager.queryAllCityName();

//        System.out.println(cityList);
//        list.add(cityList.get(0));

        System.out.println(list);
        System.out.println("555555555555555555555");
//        list.add(cityList.get(0));
        System.out.println(cityList);
        System.out.println("444444444444444444444");
        // 判空，在判断之前，加入数据
        if (list.size() == 0) {
            list.add(cityList.get(0));
        }
//        if (list.size() == 0) {
            // 判空
//            list.add(cityList.get(0));
//            list.add(city);
//            list.add("上海");
//            list.add(city);
//        }
        cityList.clear(); // 重新加载之前，清空原本数据源
        cityList.addAll(list);
        // 剩余城市也要创建对应的fragmengt页面
        fragmentList.clear();
        initPager();
        adapter.notifyDataSetChanged();
        // 页面数量发生改变，指示器的数量也会发生变化，重新设置添加指示器
        imageList.clear();
        pointLayout.removeAllViews(); // 将布局当中所以元素移除
        initPoint(); // 创建新的小点点指示器
        mainVp.setCurrentItem(fragmentList.size() - 1); // 确定当前页面是你添加的那个页面
    }

    private long exitTime; // 记录第一次点击时的时间

    // 设置
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出天气预报APP",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}