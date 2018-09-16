package co.edu.udea.mobile.eventapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.edu.udea.mobile.eventapp.R;
import co.edu.udea.mobile.eventapp.dto.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.RequestViewHolder> implements RecyclerView.OnItemTouchListener {

    static List<Event> events;
    private Context context;

    public EventAdapter(List<Event> events, Context context) {
        EventAdapter.events = events;
        this.context = context;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public EventAdapter.RequestViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_card, viewGroup, false);
        return new EventAdapter.RequestViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final EventAdapter.RequestViewHolder viewHolder, final int i) {
        viewHolder.eventName.setText(events.get(i).getName());
        viewHolder.dateExpire.setText(events.get(i).getExpireDate().toString());
        viewHolder.description.setText(events.get(i).getDescription());
        final Integer index = i;
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent("LOADSESSIONS");
                in.putExtra("event", events.get(index).getId());
                context.sendBroadcast(in);
            }
        });

        setAnimation(viewHolder.viewParent);

    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.left_animation);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name_event_card)
        public TextView eventName;
        @BindView(R.id.date_event_card)
        public TextView dateExpire;
        @BindView(R.id.desc_event_card)
        public TextView description;
        @BindView(R.id.button_card)
        public Button button;

        View viewParent;

        RequestViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            viewParent = view;
        }
    }
}
