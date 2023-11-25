package android.example.weatherforcast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherRvModel> weatherRvModelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRvModel> weatherRvModelArrayList) {
        this.context = context;
        this.weatherRvModelArrayList = weatherRvModelArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // return null;
        View view= LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {

        WeatherRvModel model=weatherRvModelArrayList.get(position);
        holder.temperaturetv.setText(model.getTemperature()+"°c");


        Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditiontv);
        holder.windtv.setText(model.getWindspeed()+"Km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat(" hh:mm   aa");
        // for expection handling
        try{
            Date t=input.parse(model.getTime());
            holder.timetv.setText(output.format(t));

        }catch (ParseException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherRvModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windtv,temperaturetv,timetv;
        private ImageView conditiontv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windtv=itemView.findViewById(R.id.idTVWindspeed);
            temperaturetv=itemView.findViewById(R.id.idTVTemperature);
            timetv=itemView.findViewById(R.id.idTVTime);
            conditiontv=itemView.findViewById(R.id.idTVCondition);
        }
    }
}