package kgk.beacon.monitoring.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import kgk.beacon.R;

public class RouteReportEventsListAdapter
        extends RecyclerView.Adapter<RouteReportEventsListAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    ////

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout listItemLayout;
        TextView typeTextView;
        TextView informationTextView;
        TextView startTextView;
        TextView endTextView;
        TextView durationTextView;

        ////

        public ViewHolder(View itemView) {
            super(itemView);

            listItemLayout = (LinearLayout)
                    itemView.findViewById(R.id.monitoring_activity_route_report_events_list_item_layout);
            typeTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_route_report_events_list_item_type_text_view);
            informationTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_route_report_events_list_item_information_text_view);
            startTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_route_report_events_list_start_text_view);
            endTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_route_report_events_list_end_text_view);
            durationTextView = (TextView)
                    itemView.findViewById(R.id.monitoring_activity_route_report_events_list_duration_text_view);
        }
    }
}
