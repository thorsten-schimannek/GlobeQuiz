package com.shnoop.globequiz;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shnoop.globequiz.fragments.FragmentGlobe;
import com.shnoop.globequiz.fragments.FragmentMainMenu;
import com.shnoop.globequiz.gamedata.GameData;
import com.shnoop.globequiz.player.PlayerManager;

import java.io.InputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public interface ReceiverFragment{ void unregisterBroadcastReceiver(); }

    private static GameData m_game_data;
    private static PlayerManager m_player_manager;
    private static GameState m_game_state;

    private FragmentGlobe m_globe_fragment;

    private LinearLayout m_info_layout;
    private TextView m_info_textview;
    private ImageView m_flag_imageview;
    private ImageButton m_wiki_button;
    private RegionPickBroadCastReceiver m_broadcast_receiver;
    private int m_selected_region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        m_game_state = new GameState();
        m_player_manager = new PlayerManager(this);

        m_game_data = new GameData(this, "languages.json");

        String language;
        if(m_player_manager.getState() == PlayerManager.PlayerManagerState.PLAYER_SELECTED) {

            language = m_player_manager.getCurrentPlayer().getLanguage();
        }
        else {
            language = getResources().getConfiguration().locale.getCountry().toLowerCase();
        }
        m_game_data.setCurrentLanguage(this, language);

        m_info_layout = findViewById(R.id.linearLayoutInfo);
        m_info_textview = findViewById(R.id.textViewInfo);
        m_flag_imageview = findViewById(R.id.imageViewFlag);
        m_wiki_button = findViewById(R.id.imageButtonWiki);
        m_wiki_button.setOnClickListener(onWikiClickListener);

        m_broadcast_receiver = new RegionPickBroadCastReceiver();
        registerReceiver(m_broadcast_receiver, new IntentFilter("region_picked"));

        m_globe_fragment = new FragmentGlobe();
        FragmentTransaction transactionGlobe = getSupportFragmentManager().beginTransaction();
        transactionGlobe.add(R.id.globeHolderFrameLayout, m_globe_fragment, "globe").commit();
        getSupportFragmentManager().executePendingTransactions();

        showMenu(this);
    }

    public static void showMenu(AppCompatActivity activity) {

        FragmentMainMenu menuFragment = new FragmentMainMenu();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainer, menuFragment)
                .addToBackStack("menu").commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        final FragmentManager fragmentManager = getSupportFragmentManager();
        int back_stack_count = fragmentManager.getBackStackEntryCount();

        if(m_globe_fragment.getMode() == FragmentGlobe.Mode.Globe) {

            m_globe_fragment.setMode(FragmentGlobe.Mode.Idle);
            showMenu(this);
        }
        else if(back_stack_count > 0){

            String name = fragmentManager.getBackStackEntryAt(back_stack_count - 1).getName();

            if(name != null) {
                if(name.equals("onePop")) {

                    fragmentManager.popBackStack();
                }
                else if (name.equals("question")){

                    AbortAlertDialog abort_alert = AbortAlertDialog
                            .newInstance(R.string.abort_game_warning);
                    abort_alert.show(getSupportFragmentManager(), "dialog");
                }
                else if (name.equals("score")) {

                    fragmentManager.popBackStack();
                    showMenu(this);
                }
                else if (name.equals("menu")) { finish(); }
            }
            else {
                fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(1).getId(),
                        fragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    public void onAbortAlertDialogYes(){

        ReceiverFragment rf = (ReceiverFragment) getSupportFragmentManager()
                .findFragmentByTag("answer");

        if(rf != null) rf.unregisterBroadcastReceiver();

        m_globe_fragment.setMode(FragmentGlobe.Mode.Idle);
        m_globe_fragment.clearForeground();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(1).getId(),
                fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void onAbortAlertDialogNo(){
    }

    public static class AbortAlertDialog extends DialogFragment {

        public static AbortAlertDialog newInstance(int title){
            AbortAlertDialog dialog = new AbortAlertDialog();
            Bundle arguments = new Bundle();
            arguments.putInt("title", title);
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            int title = getArguments().getInt("title");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton){
                                    ((MainActivity) getActivity()).onAbortAlertDialogYes();
                                }
                            })
                    .setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton){
                                    ((MainActivity) getActivity()).onAbortAlertDialogNo();
                                }
                            })
                    .create();
        }
    }

    public static GameData getGameData() { return m_game_data; }
    public static PlayerManager getPlayerManager() { return m_player_manager; }
    public static GameState getGameState() { return m_game_state; }

    public static Resources getResourcesByLocal(Context context, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources();
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale + 0.5f);
    }

    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round((px + 0.5f) / scale);
    }

    private class RegionPickBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {

            if (intent.getAction() != null
                    && intent.getAction().equalsIgnoreCase("region_picked")
                    && m_globe_fragment.getMode() == FragmentGlobe.Mode.Globe) {

                int region = intent.getIntExtra("region", -1);

                FragmentGlobe.PressType press_type = FragmentGlobe.PressType.values()[
                        intent.getIntExtra("type", FragmentGlobe.PressType.Short.ordinal())
                        ];

                if(press_type != FragmentGlobe.PressType.Short) return;

                m_globe_fragment.clearForeground();

                RelativeLayout.LayoutParams layoutParams;
                if(region == m_selected_region || region == -1) {

                    m_selected_region = -1;

                     layoutParams = new RelativeLayout.LayoutParams(
                             RelativeLayout.LayoutParams.MATCH_PARENT, 0);
                }
                else {

                    m_selected_region = region;

                    layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                    showCountryInfo(region);
                    showFlag(region);
                }
                m_info_layout.setLayoutParams(layoutParams);
            }
        }
    }

    private void showCountryInfo(int region) {

        m_globe_fragment.showAsset("countries.triangles.jet", region,
                new double[] {0.7f, .3f, 0.7f, 1.f});
        m_globe_fragment.showAsset("capitals.points.jet", region,
                new double[] {1.f, 1.0f, 1.0f, 1.f});

        String name = m_game_data.getCountries().get(region).getName();
        String capital = m_game_data.getCountries().get(region).getCapital();

        Resources resources = getResourcesByLocal(this,
                m_game_data.getCurrentLanguage().getName());

        if(capital == null) {
            m_info_textview.setText(resources.getString(
                    R.string.info_format_no_capital, name));
        }
        else {
            m_info_textview.setText(resources.getString(
                    R.string.info_format, name, capital));
        }
    }

    private void showFlag(int region) {

        try {
            InputStream inStream = getAssets()
                    .open("flags/" + region + ".png");
            Drawable d = Drawable.createFromStream(inStream, null);
            m_flag_imageview.setImageDrawable(d);
            inStream.close();
        }
        catch(java.io.IOException exception) { }
    }

    private View.OnClickListener onWikiClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if(m_selected_region != -1) {

                String wiki = m_game_data.getCountries().get(m_selected_region).getWiki();
                if(wiki != null) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wiki));
                    startActivity(browserIntent);
                }
            }
        }
    };
}
