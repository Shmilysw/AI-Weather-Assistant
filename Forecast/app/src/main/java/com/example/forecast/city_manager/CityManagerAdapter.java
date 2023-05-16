package com.example.forecast.city_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.forecast.R;
import com.example.forecast.bean.WeatherBean;
import com.example.forecast.db.DatabaseBean;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CityManagerAdapter extends BaseAdapter {

    Context context;
    List<DatabaseBean> mDatas;

    public CityManagerAdapter(Context context, List<DatabaseBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_city_manager, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DatabaseBean bean = mDatas.get(position);
        holder.cityTv.setText(bean.getCity());
        WeatherBean weatherBean = new Gson().fromJson(bean.getContent(), WeatherBean.class);
//        获取今日天气情况
        WeatherBean.DataBean.ObserveBean dataBean = weatherBean.getData().getObserve();
        holder.conTv.setText("天气 : "+dataBean.getWeather());
        holder.currentTempTv.setText(dataBean.getDegree()+"°C");
        WeatherBean.DataBean.Forecast24hBean._$0Bean bean_0 = weatherBean.getData().getForecast_24h().get_$0();
        holder.windTv.setText("风向 " + bean_0.getDay_wind_direction());
        holder.tempRangeTv.setText("温度 "+ bean_0.getMin_degree() + "~" + bean_0.getMax_degree() + "°C");

//        try {
//            holder.tempRangeTv.setText(changeTime(dataBean.getUpdate_time()));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return convertView;
    }

    //    时间格式化
    private String changeTime(String update_time) throws ParseException {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat sf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String sfstr = "";
        sfstr = sf2.format(sf1.parse(update_time));
        return sfstr;
    }

    class ViewHolder {
        TextView cityTv, conTv, currentTempTv, windTv, tempRangeTv;
        public ViewHolder(View itemView) {
            cityTv = itemView.findViewById(R.id.item_city_tv_city);
            conTv = itemView.findViewById(R.id.item_city_tv_condition);
            currentTempTv = itemView.findViewById(R.id.item_city_tv_temp);
            windTv = itemView.findViewById(R.id.item_city_wind);
            tempRangeTv = itemView.findViewById(R.id.item_city_temprange);
        }

    }



}
