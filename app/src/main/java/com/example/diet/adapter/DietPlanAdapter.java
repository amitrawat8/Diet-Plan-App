package com.example.diet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.diet.R;
import com.example.diet.constant.BaseConstant;
import com.example.diet.entity.DietDataList;
import com.example.diet.receiver.AlarmReceiver;
import com.example.diet.utils.CommonUtil;

import java.util.Calendar;
import java.util.List;

public class DietPlanAdapter extends RecyclerView.Adapter<DietPlanAdapter.dietListHolder> {

    private List<DietDataList> dietDataLists;
    private Context context;



    public DietPlanAdapter(final List<DietDataList> itemList, Context context) {
        this.dietDataLists = itemList;
        this.context = context;

    }

    @Override
    public dietListHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View menuItemView = layoutInflater.inflate(R.layout.item_diet_list, parent, false);
        return new dietListHolder(menuItemView);
    }

    @Override
    public void onBindViewHolder(final dietListHolder holder, final int position) {
        if (dietDataLists != null) {
            DietDataList item = dietDataLists.get(position);
            int lastPosition = -1;
            if (item != null) {


                if (!CommonUtil.isNullOrEmpty(item.getFood())) {
                    holder.diet_title.setText(item.getFood());
                }
                if (!CommonUtil.isNullOrEmpty(item.getMealTime())) {
                    holder.diet_time.setText(item.getMealTime());
                }
                if (!CommonUtil.isNullOrEmpty(item.getWeek())) {
                    holder.diet_week.setText(item.getWeek());
                }
                Log.e("id", String.valueOf(item.getDietID()));
                // Create a new notification


                if (position > lastPosition) {

                    Animation animation = AnimationUtils.loadAnimation(context,
                            (position > lastPosition) ? R.anim.up_bottom
                                    : R.anim.down_from_top);
                    holder.itemView.startAnimation(animation);
                    lastPosition = position;
                }


            }
        }
    }



    @Override
    public int getItemCount() {
        int ret = 0;
        if (dietDataLists != null) {
            ret = dietDataLists.size();
        }
        return ret;
    }

    /*animation*/
    public void clear() {
        dietDataLists.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<DietDataList> list) {
        dietDataLists.addAll(list);
        notifyDataSetChanged();
    }

    public class dietListHolder extends RecyclerView.ViewHolder {
        private TextView diet_title;
        private TextView diet_week;
        private TextView diet_time;

        dietListHolder(final View itemView) {
            super(itemView);
            diet_title = itemView.findViewById(R.id.diet_title);
            diet_week = itemView.findViewById(R.id.diet_week);
            diet_time = itemView.findViewById(R.id.diet_time);

        }
    }
}
