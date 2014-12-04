package com.r0adkll.postoffice.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.r0adkll.deadskunk.utils.Utils;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.EditTextStyle;
import com.r0adkll.postoffice.styles.ListStyle;
import com.r0adkll.postoffice.styles.ProgressStyle;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity implements View.OnClickListener{

    public static final String PREF_NAME = "PostOfficeExample.prefs";
    public static final String PREF_THEME = "pref_theme";
    public static final String PREF_COLOR = "pref_color";

    public static final CharSequence[] LIST_CONTENT = new CharSequence[]{
            "Batman",
            "The Flash",
            "Super Man",
            "Jon Stewart",
            "Wonder Woman",
            "Black Canary",
            "Red Tornado",
            "Cpt. Atom",
            "The Queston",
            "Hawk Girl",
            "Martian Man-Hunter",
            "Green Arrow",
            "Red Arrow",
            "Robin",
            "Nightwing",
            "Aquaman",
            "The Joker",
            "Cpt. Cold",
            "Cpt. Boomerang",
            "Darkseid",
            "Mongol",
            "Two Face",
            "The Penguin",
            "Ra's al Ghul",
            "Lex Luthor",
            "Gorilla Grod",
            "Shade",
            "Bizarro",
            "Ultra Humanite"
    };

    @InjectView(R.id.alert_holo)            Button mAlertHolo;
    @InjectView(R.id.alert_material)        Button mAlertMaterial;
    @InjectView(R.id.edittext_holo)         Button mEdittextHolo;
    @InjectView(R.id.edittext_material)     Button mEdittextMaterial;
    @InjectView(R.id.progress_holo)         Button mProgressHolo;
    @InjectView(R.id.progress_material)     Button mProgressMaterial;
    @InjectView(R.id.list_holo)             Button mListHolo;
    @InjectView(R.id.list_material)         Button mListMaterial;
    @InjectView(R.id.theme_color)           View mThemeColor;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set Theme based on preference
        mPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if(isLight()){
            setTheme(R.style.Theme_Light);
        }else{
            setTheme(R.style.Theme_Dark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mAlertHolo.setOnClickListener(this);
        mAlertMaterial.setOnClickListener(this);
        mEdittextHolo.setOnClickListener(this);
        mEdittextMaterial.setOnClickListener(this);
        mProgressHolo.setOnClickListener(this);
        mProgressMaterial.setOnClickListener(this);
        mListHolo.setOnClickListener(this);
        mListMaterial.setOnClickListener(this);

        mThemeColor.setBackgroundColor(getColor());
        mThemeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HSVColorPickerDialog diag = new HSVColorPickerDialog(MainActivity.this, getColor(), new HSVColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void colorSelected(Integer color) {
                        mPrefs.edit().putInt(PREF_COLOR, color).commit();
                        mThemeColor.setBackgroundColor(color);
                    }
                });
                diag.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_theme:
                // Toggle preference
                mPrefs.edit().putBoolean(PREF_THEME, !isLight()).commit();

                // Restart this activity
                Intent restart = new Intent(this, MainActivity.class);
                restart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);

                overridePendingTransition(0, 0);
                startActivity(restart);


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem themeSwitch = menu.findItem(R.id.action_theme);
        themeSwitch.setIcon(isLight() ? R.drawable.ic_action_theme_light : R.drawable.ic_action_theme_dark);
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        Delivery delivery = null;
        String tag = "";
        Design holoDesign = isLight() ? Design.HOLO_LIGHT : Design.HOLO_DARK;
        Design mtrlDesign = isLight() ? Design.MATERIAL_LIGHT : Design.MATERIAL_DARK;

        switch (view.getId()){
            case R.id.alert_holo:
                tag = "ALERT_HOLO";

                // Create and show holo alert style
                delivery = PostOffice.newMail(this)
                        .setTitle(R.string.title)
                        .setThemeColor(getColor())
                        .setMessage(R.string.message)
                        .setDesign(holoDesign)
                        .setButton(Dialog.BUTTON_POSITIVE, R.string.action1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do something with teh clicks
                                Toast.makeText(MainActivity.this, "Alert Holo Closed.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .build();

                break;
            case R.id.alert_material:
                tag = "ALERT_MATERIAL";

                // Create and show holo alert style
                delivery = PostOffice.newMail(this)
                        .setTitle(R.string.mtrl_alert_title)
                        .setThemeColor(getColor())
                        .setMessage(Utils.getRandom().nextBoolean() ? R.string.message : R.string.message_long)
                        .setDesign(mtrlDesign)
                        .setCanceledOnTouchOutside(true)
                        .setCancelable(true)
                        .setButton(Dialog.BUTTON_NEUTRAL, "maybe", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do something with teh clicks
                                Toast.makeText(MainActivity.this, "Alert Material Closed.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_NEGATIVE, android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_POSITIVE, android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setShouldProperlySortButtons(false)
                        .build();
                break;

            case R.id.edittext_holo:
                tag = "EDITTEXT_HOLO";

                delivery = PostOffice.newMail(this)
                        .setTitle(R.string.title)
                        .setThemeColor(getColor())
                        .setDesign(holoDesign)
                        .showKeyboardOnDisplay(true)
                        .setButton(Dialog.BUTTON_POSITIVE, R.string.action1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_NEGATIVE, R.string.action2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setStyle(new EditTextStyle.Builder(this)
                                        .setHint("Email")
                                        .setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                        .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                            @Override
                                            public void onAccepted(String text) {
                                                Toast.makeText(MainActivity.this, "Text was accepted: " + text, Toast.LENGTH_SHORT).show();
                                            }
                                        }).build())
                        .build();

                break;
            case R.id.edittext_material:
                tag = "EDITTEXT_MATERIAL";

                delivery = PostOffice.newMail(this)
                        .setTitle(R.string.mtrl_title)
                        .setThemeColor(getColor())
                        .setDesign(mtrlDesign)
                        .showKeyboardOnDisplay(true)
                        .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.blue_500)
                        .setButton(Dialog.BUTTON_POSITIVE, R.string.action1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_NEGATIVE, R.string.action2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setStyle(new EditTextStyle.Builder(this)
                                .setHint("Email")
                                .setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                    @Override
                                    public void onAccepted(String text) {
                                        Toast.makeText(MainActivity.this, "Text was accepted: " + text, Toast.LENGTH_SHORT).show();
                                    }
                                }).build())
                        .build();

                break;

            case R.id.progress_holo:
                tag = "PROGRESS_HOLO";
                delivery = PostOffice.newMail(this)
                        .setTitle("Downloading")
//                        .setMessage("Your download is currently in progress. Please wait.")
                        .setThemeColor(getColor())
                        .setDesign(holoDesign)
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)

                        .setStyle(new ProgressStyle.Builder(this)
                                        .setIndeterminate(true)
                                        .setProgressStyle(ProgressStyle.HORIZONTAL)
                                        .build())

                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .build();

//                ProgressStyle style = (ProgressStyle) delivery.getStyle();
//                style.setIndeterminate(true);
//                style.setProgress(1);
//                style.setMax(1);

                break;
            case R.id.progress_material:
                tag = "PROGRESS_MATERIAL";
                delivery = PostOffice.newMail(this)
//                        .setTitle("Downloading")
//                        .setMessage("Your download is currently in progress. Please wait.")
                        .setThemeColor(getColor())
                        .setDesign(mtrlDesign)

                        .setStyle(new ProgressStyle.Builder(this)
                                .setProgressStyle(ProgressStyle.NORMAL)
                                .setProgressMessage("Your content is loading...")
                                .build())

                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .build();

                break;

            case R.id.list_holo:
                tag = "LIST_HOLO";

                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, LIST_CONTENT);
                delivery = PostOffice.newMail(this)
                        .setTitle("DC Universe")
                        .setThemeColor(getColor())
                        .setDesign(holoDesign)
                        .setStyle(new ListStyle.Builder(this)
                                .setDividerHeight(2)
                                .setOnItemAcceptedListener(new ListStyle.OnItemAcceptedListener<CharSequence>() {
                                    @Override
                                    public void onItemAccepted(CharSequence item, int position) {
                                        Toast.makeText(MainActivity.this, String.format("%s is the best DC Character", item), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .build(adapter))
                        .build();


                break;
            case R.id.list_material:
                tag = "LIST_MATERIAL";
                delivery = PostOffice.newSimpleListMail(this, "DC Universe", mtrlDesign, LIST_CONTENT, new ListStyle.OnItemAcceptedListener<CharSequence>() {
                    @Override
                    public void onItemAccepted(CharSequence item, int position) {
                        Toast.makeText(MainActivity.this, String.format("%s is the bestest DC Character", item), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

        // Show the delivery
        if(delivery != null)
            delivery.show(getFragmentManager(), tag);
    }

    /**
     * Return the current theme
     * @return
     */
    private boolean isLight(){
        return mPrefs.getBoolean(PREF_THEME, true);
    }

    private int getColor(){
        return mPrefs.getInt(PREF_COLOR, getResources().getColor(R.color.blue_500));
    }
}
