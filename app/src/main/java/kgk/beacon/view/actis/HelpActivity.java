package kgk.beacon.view.actis;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
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
    public static final String MAP_SCREEN = "map_screen";

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

        HelpEntry lbsEntry = new HelpEntry();
        lbsEntry.setTitle(getString(R.string.lbs_description_title));
        lbsEntry.setBody(getString(R.string.lbs_description));


        if (screenName != null) {
            switch (screenName) {
                case INFORMATION_SCREEN:
                    helpEntryList.add(historyEntry);
                    helpEntryList.add(searchEntry);
                    helpEntryList.add(autosearchEntry);
                    helpEntryList.add(lbsEntry);
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
                    helpEntryList.add(lbsEntry);
                    break;
                case MAP_SCREEN:
                    helpEntryList.add(lbsEntry);
            }
        }

        HelpListAdapter adapter = new HelpListAdapter(this, helpEntryList);
        listView.setAdapter(adapter);
        listView.setClickable(false);
    }

    private SpannableStringBuilder prepareImagedString(String sourceString) {
        int index1 = sourceString.indexOf("@1");
        int index2 = sourceString.indexOf("@2");

        List<String> stringsToBold = new ArrayList<>();
        stringsToBold.add(getString(R.string.auto_search_description_title));
        stringsToBold.add(getString(R.string.auto_search_on_label));
        stringsToBold.add(getString(R.string.auto_search_off_label));
        stringsToBold.add(getString(R.string.turning_on_word));
        stringsToBold.add(getString(R.string.turning_off_word));
        stringsToBold.add(getString(R.string.grey_word_1));
        stringsToBold.add(getString(R.string.grey_word_2));
        stringsToBold.add(getString(R.string.yellow_word_1));
        stringsToBold.add(getString(R.string.yellow_word_2));
        stringsToBold.add(getString(R.string.green_word_1));
        stringsToBold.add(getString(R.string.green_word_2));

        List<String> stringsToGrey = new ArrayList<>();
        stringsToGrey.add(getString(R.string.grey_word_1));
        stringsToGrey.add(getString(R.string.grey_word_2));

        List<String> stringsToYellow = new ArrayList<>();
        stringsToYellow.add(getString(R.string.yellow_word_1));
        stringsToYellow.add(getString(R.string.yellow_word_2));

        List<String> stringsToGreen = new ArrayList<>();
        stringsToGreen.add(getString(R.string.green_word_1));
        stringsToGreen.add(getString(R.string.green_word_2));


        List<Integer> boldIndexes = new ArrayList<>();
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(sourceString);

        for (String stringToBold : stringsToBold) {
            int boldEntriesCounter = 0;
            while (boldEntriesCounter != -1) {
                boldEntriesCounter = sourceString.indexOf(stringToBold, boldEntriesCounter);

                if (boldEntriesCounter != -1) {
                    boldIndexes.add(boldEntriesCounter);
                    boldEntriesCounter += stringToBold.length();
                }
            }

            for (int boldIndex : boldIndexes) {
                StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
                builder.setSpan(boldStyle, boldIndex, boldIndex + stringToBold.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                if (stringsToGrey.contains(stringToBold)) {
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources()
                            .getColor(R.color.actis_app_toolbar_background));
                    builder.setSpan(colorSpan, boldIndex, boldIndex + stringToBold.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }

                if (stringsToYellow.contains(stringToBold)) {
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources()
                            .getColor(R.color.actis_app_yellow_accent));
                    builder.setSpan(colorSpan, boldIndex, boldIndex + stringToBold.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }

                if (stringsToGreen.contains(stringToBold)) {
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources()
                            .getColor(R.color.battery_text_color));
                    builder.setSpan(colorSpan, boldIndex, boldIndex + stringToBold.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }

            boldIndexes.clear();
        }

        builder.setSpan(new ImageSpan(this, R.drawable.search_on_scheme),
                index1, index1 + 2, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(new ImageSpan(this, R.drawable.search_off_scheme),
                index2, index2 + 2, ImageSpan.ALIGN_BASELINE);

        return builder;
    }
}
