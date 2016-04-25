package kgk.beacon.view.actis.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import kgk.beacon.R;

/**
 * Стандартный платформенный адаптер для отображения списка подсказок
 */
public class HelpListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<HelpEntry> helpEntries;

    ////

    public HelpListAdapter(Context context, List<HelpEntry> helpEntries) {
        this.context = context;
        this.helpEntries = helpEntries;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    ////


    @Override
    public int getCount() {
        return helpEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return helpEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_item_help, parent, false);
        }

        HelpEntry helpEntry = helpEntries.get(position);

        TextView title = (TextView) view.findViewById(R.id.listItemHelp_title);
        title.setText(helpEntry.getTitle());
        TextView body = (TextView) view.findViewById(R.id.listItemHelp_body);
        body.setText(helpEntry.getBody());

        return view;
    }
}
