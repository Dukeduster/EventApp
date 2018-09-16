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
import co.edu.udea.mobile.eventapp.MapsActivity;
import co.edu.udea.mobile.eventapp.R;
import co.edu.udea.mobile.eventapp.dto.Attendance;
import co.edu.udea.mobile.eventapp.dto.AttendanceShow;

public class AttendanceAdapter  extends RecyclerView.Adapter<AttendanceAdapter.RequestViewHolder>
        implements RecyclerView.OnItemTouchListener {

    static List<AttendanceShow> attendances;
    private Context context;

    public AttendanceAdapter(List<AttendanceShow> attendances, Context context) {
        AttendanceAdapter.attendances = attendances;
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
    public AttendanceAdapter.RequestViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assist_card, viewGroup, false);
        return new AttendanceAdapter.RequestViewHolder(view, context);
    }


    @Override
    public void onBindViewHolder(final AttendanceAdapter.RequestViewHolder viewHolder, int i) {
        viewHolder.sessionName.setText(attendances.get(i).getSession());
        viewHolder.attendanceDate.setText(attendances.get(i).getAttendanceDate());
        viewHolder.attendantName.setText(String.valueOf(attendances.get(i).attendant.getName() + " " +
                attendances.get(i).attendant.getLastname()));
        viewHolder.assistEmail.setText(attendances.get(i).attendant.getEmail());
        final Integer index = i;
        viewHolder.buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se carga Actividad Mapa con los valores de la tarjeta
                Intent i = new Intent(context, MapsActivity.class);
                i.putExtra("lat", attendances.get(index).getLatitude());
                i.putExtra("lon", attendances.get(index).getLongitude());
                context.startActivity(i);

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
        return attendances.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.session_assist_card)
        public TextView sessionName;
        @BindView(R.id.date_assist_card)
        public TextView attendanceDate;
        @BindView(R.id.stu_assist_card)
        public TextView attendantName;
        @BindView(R.id.email_assist_card)
        public TextView assistEmail;
        @BindView(R.id.button_assist_map)
        public Button buttonMap;

        View viewParent;

        RequestViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            viewParent = view;
        }
    }
}

