package kgk.beacon.view.actis;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kgk.beacon.R;
import kgk.beacon.view.actis.adapter.HelpEntry;
import kgk.beacon.view.actis.adapter.HelpListAdapter;

/**
 * Экран пользовательской инструкции
 */
public class HelpActivity extends AppCompatActivity {

    public static final String KEY_SCREEN_NAME = "key_screen_name";
    public static final String INFORMATION_SCREEN = "information_screen";
    public static final String HISTORY_SCREEN = "history_screen";
    public static final String PERIOD_SCREEN = "period_screen";
    public static final String TRACK_SCREEN = "track_screen";

    private static final String TAG = HelpActivity.class.getSimpleName();

    @Bind(R.id.actisApp_toolbar) Toolbar toolbar;
    @Bind(R.id.listViewHelp) ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        prepareToolbar();
        prepareListView();
    }

    ////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////

    private void prepareToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.actis_navigation_menu_icon));
    }

    /** составления списка подсказок в зависимости от экрана, с которого был сделан переход */
    private void prepareListView() {
        String screenName = getIntent().getExtras().getString(KEY_SCREEN_NAME);
        List<HelpEntry> helpEntryList = new ArrayList<>();

        HelpEntry historyEntry = new HelpEntry();
        historyEntry.setTitle(getString(R.string.history_button_label));
        historyEntry.setBody(getText(R.string.history_screen_description));

        HelpEntry searchEntry = new HelpEntry();
        searchEntry.setTitle(getString(R.string.search_on_button_label));
        searchEntry.setBody(getText(R.string.query_function_description));

        HelpEntry autosearchEntry = new HelpEntry();
        autosearchEntry.setTitle(getString(R.string.auto_search_description_title));
        SpannableStringBuilder content = prepareImagedString(getText(R.string.auto_search_description).toString());
        autosearchEntry.setBody(content);

        HelpEntry periodEntry = new HelpEntry();
        periodEntry.setTitle(getString(R.string.select_period_button_label));
        periodEntry.setBody(getText(R.string.period_screen_description));

        HelpEntry trackEntry = new HelpEntry();
        trackEntry.setTitle(getString(R.string.track_button_label));
        trackEntry.setBody(getText(R.string.track_screen_description));


        if (screenName != null) {
            switch (screenName) {
                case INFORMATION_SCREEN:
                    helpEntryList.add(historyEntry);
                    helpEntryList.add(searchEntry);
                    helpEntryList.add(autosearchEntry);
                    break;
                case HISTORY_SCREEN:
                    helpEntryList.add(historyEntry);
                    helpEntryList.add(periodEntry);
                    helpEntryList.add(trackEntry);
                    break;
                case PERIOD_SCREEN:
                    helpEntryList.add(periodEntry);
                    break;
                case TRACK_SCREEN:
                    helpEntryList.add(trackEntry);
                    break;
            }
        }

        HelpListAdapter adapter = new HelpListAdapter(this, helpEntryList);
        listView.setAdapter(adapter);
        listView.setClickable(false);
    }

    private SpannableStringBuilder prepareImagedString(String sourceString) {
        int index1 = sourceString.indexOf("@1");
        int index2 = sourceString.indexOf("@2");
        int index3 = sourceString.indexOf("@3");
        int index4 = sourceString.indexOf("@4");
        int index5 = sourceString.indexOf("@5");
        int index6 = sourceString.indexOf("@6");
        int index7 = sourceString.indexOf("@7");
        int index8 = sourceString.indexOf("@8");
        int index9 = sourceString.indexOf("@9");

        String targetString = getString(R.string.auto_search_description_title);
        List<Integer> boldIndexes = new ArrayList<>();

        int boldEntriesCounter = 0;
        while (boldEntriesCounter != -1) {
            boldEntriesCounter = sourceString.indexOf(targetString, boldEntriesCounter);

            Log.d(TAG, " " + boldEntriesCounter);

            if (boldEntriesCounter != -1) {
                boldIndexes.add(boldEntriesCounter);
                boldEntriesCounter += targetString.length();
            }
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(sourceString);
        builder.setSpan(new ImageSpan(this, R.drawable.search_custom_switch_grey_thumb_with_lock),
                index1, index1 + 2, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(new ImageSpan(this, R.drawable.search_custom_switch_yellow_thumb_with_lock),
                index2, index2 + 2, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(new ImageSpan(this, R.drawable.search_custom_switch_green_thumb_with_lock),
                index3, index3 + 2, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(new ImageSpan(this, R.drawable.search_custom_switch_grey_thumb_with_lock),
                index4, index4 + 2, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(new ImageSpan(this, R.drawable.search_custom_switch_yellow_thumb_with_lock),
                index5, index5 + 2, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(new ImageSpan(this, R.drawable.search_custom_switch_green_thumb_with_lock),
                index6, index6 + 2, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(new ImageSpan(this, R.drawable.search_custom_switch_green_thumb_with_lock),
                index7, index7 + 2, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(new ImageSpan(this, R.drawable.search_custom_switch_yellow_thumb_with_lock),
                index8, index8 + 2, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(new ImageSpan(this, R.drawable.search_custom_switch_grey_thumb_with_lock),
                index9, index9 + 2, ImageSpan.ALIGN_BASELINE);

        for (int boldIndex : boldIndexes) {
            StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
            builder.setSpan(boldStyle, boldIndex, boldIndex + targetString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        return builder;
    }
}
