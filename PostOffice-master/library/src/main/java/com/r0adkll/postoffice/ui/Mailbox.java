package com.r0adkll.postoffice.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.ftinc.fontloader.FontLoader;
import com.ftinc.fontloader.Types;
import com.r0adkll.postoffice.R;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.styles.EditTextStyle;
import com.r0adkll.postoffice.styles.Style;
import com.r0adkll.postoffice.widgets.MaterialButtonLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * This is a delegate class to all the functions
 * of the DialogFragment and android.support.v4.app.DialogFragment class
 * so that I don't have to keep managing 2 copies of the same code
 *
 * Created by r0adkll on 10/20/14.
 */
public class Mailbox {

    /**********************************************************
     *
     * Static Creator
     *
     */

    /**
     * Create a new instance of this dialog fragment
     *
     * @return              the created/inflated dialog fragment
     */
    public static Mailbox createInstance(Delivery construct, MailboxInterface mailboxInterface){
        Mailbox mail = new Mailbox();
        mail.setConfiguration(construct);
        mail.setInterface(mailboxInterface);
        return mail;
    }

    /**********************************************************
     *
     * Constants
     *
     */

    private static final long DELAY_BUFFER = 50;

    /**********************************************************
     *
     * Variables
     *
     */

    private MailboxInterface mMail;
    private InputMethodManager mImm;
    private Delivery mConstruct;

    private TextView mTitle;
    private TextView mMessage;
    private LinearLayout mContentFrame;
    private FrameLayout mStyleContent;
    private MaterialButtonLayout mButtonContainer;
    private ScrollView mMessageScrollview;

    /**
     * Empty constructor
     */
    private Mailbox(){}

    /**********************************************************
     *
     * Lifecycle Methods
     *
     */

    /**
     * Called when the DialogFragment's view and everything is setup
     *
     * @param icicle        the saved instance state
     */
    public void onActivityCreated(Bundle icicle){
        // Load mImm
        mImm = (InputMethodManager) mMail.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if(mConstruct != null && mConstruct.getDesign().isMaterial()) {

            // Apply construct to UI
            if (mTitle != null && mMessage != null) {
                if (mConstruct.getTitle() != null){
                    mTitle.setText(mConstruct.getTitle());
                }else{
                    mTitle.setVisibility(View.GONE);
                }

                if (mConstruct.getMessage() != null) {
                    if(mConstruct.getMovementMethod() != null)
                        mMessage.setMovementMethod(mConstruct.getMovementMethod());

                    mMessage.setAutoLinkMask(mConstruct.getAutoLinkMask());
                    mMessage.setText(mConstruct.getMessage());
                }else
                    mContentFrame.removeView(mMessageScrollview);

                if (mConstruct.getDesign().isMaterial()) {
                    FontLoader.applyTypeface(mTitle, Types.ROBOTO_MEDIUM);
                    FontLoader.applyTypeface(mMessage, Types.ROBOTO_REGULAR);
                }
            }

            if (mButtonContainer != null && mConstruct.getButtonConfig().size() > 0) {

                // Iterate through config, and setup the button states
                HashMap<Integer, Delivery.ButtonConfig> config = mConstruct.getButtonConfig();
                List<Integer> keys = new ArrayList<>(config.keySet());

                // Fix for issue #18, Lollipop handles Set<> collections differently than older
                // versions of android and for some reason when we adapt the keyset to an array
                // list it reverses the added order of the buttons. So for now we will just
                // reverse the list when the device is lollipop
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    Collections.reverse(keys);

                // Only sort if set to
                if(mConstruct.isProperlySortingMaterialButton())
                    Collections.sort(keys, mButtonComparator);

                int N = keys.size();
                for (int i = 0; i < N; i++) {
                    final int key = keys.get(i);
                    final Delivery.ButtonConfig cfg = config.get(key);

                    // Pull button Color
                    int textColor = mConstruct.getButtonTextColor(key);

                    // Inflate the RippleView wrapped Button based on design
                    RippleView button = (RippleView)mMail.getActivity().getLayoutInflater()
                            .inflate(mConstruct.getDesign().isLight() ?
                                    R.layout.material_light_dialog_button :
                                    R.layout.material_dark_dialog_button, null, false);

                    // Load the Button from the ripple view
                    Button rippleButton = (Button) button.findViewById(R.id.ripple_button);

                    // Apply parameters to the button
                    FontLoader.applyTypeface(rippleButton, Types.ROBOTO_MEDIUM);
                    rippleButton.setId(key);
                    rippleButton.setText(cfg.title);
                    if(textColor != 0) rippleButton.setTextColor(textColor);
                    rippleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long delay = mMail.getActivity().getResources()
                                    .getInteger(R.integer.ripple_duration) + DELAY_BUFFER;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cfg.listener.onClick(mMail.getDialog(), key);
                                    if(mConstruct.getStyle() != null){
                                        mConstruct.getStyle().onButtonClicked(key, mMail.getDialog());
                                    }
                                }
                            }, delay);
                        }
                    });

                    // add to layout
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            mMail.getActivity().getResources().getDimensionPixelSize(R.dimen.material_button_height));

                    mButtonContainer.addView(button, params);
                }

            }else if(mButtonContainer != null && mConstruct.getButtonCount() == 0){
                mButtonContainer.setVisibility(View.GONE);
            }

            // Load Content
            if (mStyleContent != null && mConstruct.getStyle() != null) {
                mStyleContent.addView(mConstruct.getStyle().getContentView());
            }

        }

        if(mConstruct != null) {
            // Lastly apply the other dialog options
            mMail.setCancelable(mConstruct.isCancelable());
            mMail.getDialog().setCanceledOnTouchOutside(mConstruct.isCanceledOnTouchOutside());
            mMail.getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if (mConstruct.getOnShowListener() != null)
                        mConstruct.getOnShowListener().onShow(dialog);
                    if (mConstruct.getStyle() != null)
                        mConstruct.getStyle().onDialogShow(mMail.getDialog());

                    if (mConstruct.isShowKeyboardOnDisplay()) {
                        if (mConstruct.getStyle() instanceof EditTextStyle) {
                            EditText et = ((EditTextStyle) mConstruct.getStyle()).getEditTextView();
                            mImm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }

                }
            });
        }else{
            mMail.dismiss();
        }
    }

    /**
     * Called to create the view to use in the dialog
     * @return      a custom view, or null
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        if(mConstruct == null) {
            return null;
        }else{
            View view;

            // Depending on the style, load the appropriate layout
            if(mConstruct.getDesign().isMaterial()){
                if(mConstruct.getDesign().isLight()){
                    view = inflater.inflate(R.layout.layout_material_light_dialog, container, false);
                }else{
                    view = inflater.inflate(R.layout.layout_material_dark_dialog, container, false);
                }

                // Inflate the views
                mTitle = (TextView) view.findViewById(R.id.title);
                mMessage = (TextView) view.findViewById(R.id.message);
                mMessageScrollview = (ScrollView) view.findViewById(R.id.message_scrollview);
                mContentFrame = (LinearLayout) view.findViewById(R.id.content_frame);
                mStyleContent = (FrameLayout) view.findViewById(R.id.style_content);
                mButtonContainer = (MaterialButtonLayout) view.findViewById(R.id.button_container);

                // Attach the construct to the button container
                mButtonContainer.setConfiguration(mConstruct);
            }else{
                view = null;
            }

            return view;
        }
    }

    /**
     * Called to create the Dialog object to use
     *
     * @param icicle        the saved instance state
     * @return              the dialog to use
     */
    public Dialog onCreateDialog(Bundle icicle){
        Dialog diag;

        // Disable the title if the title data isn't null
        if(mConstruct != null && !mConstruct.getDesign().isMaterial()){

            // Construct the Dialog Object
            diag = buildAlertDialog(mMail.getActivity(), mConstruct);

            // Remove the Title Feature
            int theme = mConstruct.getDesign().isLight() ? android.R.style.Theme_Holo_Light : android.R.style.Theme_Holo;
            mMail.setStyle(DialogFragment.STYLE_NO_TITLE, theme);

        }else{
            diag = mMail.createSuperDialog(icicle); //super.onCreateDialog(savedInstanceState);
            if(mConstruct != null) {
                int theme = mConstruct.getDesign().isLight() ? android.R.style.Theme_Holo_Light : android.R.style.Theme_Holo;
                mMail.setStyle(DialogFragment.STYLE_NO_TITLE, theme);
            }

        }

        return diag;
    }

    /**
     * Called when the DialogFragment is dismissed
     *
     * @param dialog        the dialog interface for the dismissed dialog
     */
    public void onDismiss(DialogInterface dialog){
        if(mConstruct != null && mConstruct.getOnDismissListener() != null) mConstruct.getOnDismissListener().onDismiss(dialog);
    }

    /**
     * Called when the DialogFragment is canceled
     *
     * @param dialog        the dialog interface for the canceled dialog
     */
    public void onCancel(DialogInterface dialog){
        if(mConstruct != null && mConstruct.getOnCancelListener() != null) mConstruct.getOnCancelListener().onCancel(dialog);
    }


    /**********************************************************
     *
     * Helper Methods
     *
     */


    /**
     * Set the mailbox interface to communicate with the Mail object
     *
     * @param mailboxInterface      the interface
     */
    public void setInterface(MailboxInterface mailboxInterface){
        mMail = mailboxInterface;
    }

    /**
     * Apply a delivery configuration to the fragment
     *
     * @param delivery      the delivery configuration construct
     */
    public void setConfiguration(Delivery delivery){
        mConstruct = delivery;
    }

    /**
     * Modify the title of the dialog no matter what style it is
     *
     * @param title     the title to update with
     */
    public void setTitle(CharSequence title){
        if(mConstruct != null && !mConstruct.getDesign().isMaterial()){
            // Modify the dialog's title element
            mMail.getDialog().setTitle(title);
        }else{
            if(mTitle != null){
                mTitle.setText(title);
            }
        }
    }

    /**
     * Modify the title of the dialog no matter what style is being used
     *
     * @param titleResId        the resource id of the string value to use
     */
    public void setTitle(int titleResId){
        setTitle(mMail.getActivity()
                .getString(titleResId));
    }

    /**
     * Convienence function for building an AlertDialog stylized to the Delivery
     * construct.
     *
     * @param ctx           the application context
     * @param delivery      the delivery configuration construct
     * @return              the built AlertDialog
     */
    public static AlertDialog buildAlertDialog(Context ctx, final Delivery delivery){
        int theme = AlertDialog.THEME_HOLO_LIGHT;
        switch(delivery.getDesign()){
            case HOLO_LIGHT:
                theme = AlertDialog.THEME_HOLO_LIGHT;
                break;
            case HOLO_DARK:
                theme = AlertDialog.THEME_HOLO_DARK;
                break;
            case MATERIAL_LIGHT:
                theme = AlertDialog.THEME_HOLO_LIGHT;
                break;
            case MATERIAL_DARK:
                theme = AlertDialog.THEME_HOLO_DARK;
                break;
        }

        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx, theme);

        View view = LayoutInflater.from(ctx).inflate(R.layout.layout_holo_dialog, null, false);
        view.setMinimumWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, ctx.getResources().getDisplayMetrics()));
        ImageView icon = (ImageView)view.findViewById(R.id.icon);
        TextView alertTitle = (TextView)view.findViewById(R.id.alertTitle);
        TextView mMessage = (TextView)view.findViewById(R.id.message);
        View divider = view.findViewById(R.id.titleDivider);
        FrameLayout customContent = (FrameLayout)view.findViewById(R.id.customPanel);
        LinearLayout topPanel = (LinearLayout)view.findViewById(R.id.topPanel);
        LinearLayout contentPanel = (LinearLayout)view.findViewById(R.id.contentPanel);

        if(delivery.getTitle() != null){
            alertTitle.setText(delivery.getTitle());
            int color = delivery.getThemeColor() == -1 ? ctx.getResources().getColor(R.color.red_200) : delivery.getThemeColor();
            alertTitle.setTextColor(color);
            divider.setBackgroundColor(color);
        }else{
            topPanel.setVisibility(View.GONE);
        }

        if(delivery.getIcon() != -1){
            icon.setImageResource(delivery.getIcon());
        }else{
            icon.setVisibility(View.GONE);
        }

        // Set the content

        // if the style is null, and the message exists in the construct, set the alert dialog message
        if(delivery.getMessage() != null){
            if(delivery.getMovementMethod() != null)
                mMessage.setMovementMethod(delivery.getMovementMethod());

            mMessage.setAutoLinkMask(delivery.getAutoLinkMask());
            mMessage.setText(delivery.getMessage());
            mMessage.setTextColor(ctx.getResources().getColor(delivery.getDesign().isLight() ? R.color.background_material_dark : R.color.background_material_light));
        }else{
            contentPanel.setVisibility(View.GONE);
        }

        // set the custom content
        builder.setView(view);

        // If it isn't material design, apply the button constructs
        if(!delivery.getDesign().isMaterial()){

            // Iterate through config, and setup the button states
            HashMap<Integer, Delivery.ButtonConfig> config = delivery.getButtonConfig();
            List<Integer> keys = new ArrayList<>(config.keySet());
            int N = config.size();
            for(int i=0; i<N; i++){
                int key = keys.get(i);
                final Delivery.ButtonConfig cfg = config.get(key);

                // Pull button Color
                int textColor = delivery.getButtonTextColor(key);
                Spannable title = new SpannableString(cfg.title);
                if(textColor != 0){
                    title.setSpan(new ForegroundColorSpan(textColor), 0, cfg.title.length(), 0);
                }

                // Ensure that they are using the correct which buttons
                switch (key){
                    case AlertDialog.BUTTON_POSITIVE:
                        builder.setPositiveButton(title, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cfg.listener.onClick(dialog, which);
                                if(delivery.getStyle() != null){
                                    delivery.getStyle().onButtonClicked(which, dialog);
                                }
                            }
                        });
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        builder.setNegativeButton(title, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cfg.listener.onClick(dialog, which);
                                if(delivery.getStyle() != null){
                                    delivery.getStyle().onButtonClicked(which, dialog);
                                }
                            }
                        });
                        break;
                    case AlertDialog.BUTTON_NEUTRAL:
                        builder.setNeutralButton(title, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cfg.listener.onClick(dialog, which);
                                if(delivery.getStyle() != null){
                                    delivery.getStyle().onButtonClicked(which, dialog);
                                }
                            }
                        });
                        break;
                }
            }
        }

        if(delivery.getStyle() != null && !delivery.getDesign().isMaterial()){
            Style style = delivery.getStyle();
            customContent.addView(style.getContentView());
        }

        // Create the dialog and return it
        return builder.create();
    }

    /**
     * The button sorting comparator based on the priority from
     * {@link #getButtonPriority(Integer)} function
     */
    private Comparator<Integer> mButtonComparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer lhs, Integer rhs) {
            /*
             * Sort Order
             * 1) Dialog.BUTTON_POSITIVE = -1
             * 2) Dialog.BUTTON_NEUTRAL = -3
             * 3) Dialog.BUTTON_NEGATIVE = -2
             */
            Integer lhsPriority = getButtonPriority(lhs);
            Integer rhsPriority = getButtonPriority(rhs);
            return lhsPriority.compareTo(rhsPriority);
        }
    };

    /**
     * Get the proper button sorting priority
     *
     * @param button        the button constant
     * @return              the sorting order
     */
    private Integer getButtonPriority(Integer button){
        switch (button){
            case Dialog.BUTTON_POSITIVE:
                return 3;
            case Dialog.BUTTON_NEUTRAL:
                return 2;
            case Dialog.BUTTON_NEGATIVE:
                return 1;
            default:
                return 0;
        }
    }

}
