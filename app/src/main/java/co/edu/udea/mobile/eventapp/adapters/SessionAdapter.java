package co.edu.udea.mobile.eventapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import co.edu.udea.mobile.eventapp.dto.EventSession;

/**
 * Adapter para Datos de Sesiones de Evento
 *
 * @Using butterknife
 */

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.RequestViewHolder> implements RecyclerView.OnItemTouchListener {

    static List<EventSession> sessions;
    //URL Google Chart API(Infographics)
    public final String urlQr = "https://chart.googleapis.com/chart?cht=qr&chs=250x250&chl=";
    private Context context;

    public SessionAdapter(List<EventSession> sessions, Context context) {
        SessionAdapter.sessions = sessions;
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
    public SessionAdapter.RequestViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.session_card, viewGroup, false);
        return new SessionAdapter.RequestViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final SessionAdapter.RequestViewHolder viewHolder, int i) {
        viewHolder.sessionName.setText(sessions.get(i).getName());
        viewHolder.sessionDate.setText(sessions.get(i).getDateSession().toString());
        // Log.e("statusRoute", routes.get())
        viewHolder.sessionDescription.setText(sessions.get(i).getDescription());
        final Integer index = i;
        viewHolder.buttonAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent("LOADASSIST");
                in.putExtra("session", sessions.get(index).getId());
                context.sendBroadcast(in);

            }
        });
        viewHolder.buttonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlQr + sessions.get(index).getId()));
                context.startActivity(browserIntent);
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
        return sessions.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name_session_card)
        public TextView sessionName;
        @BindView(R.id.date_session_card)
        public TextView sessionDate;
        @BindView(R.id.desc_session_card)
        public TextView sessionDescription;
        @BindView(R.id.button_assist)
        public Button buttonAssist;
        @BindView(R.id.button_link)
        public Button buttonLink;

        View viewParent;

        RequestViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            viewParent = view;
        }
    }
}
