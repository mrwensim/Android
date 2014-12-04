package com.r0adkll.postoffice.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.r0adkll.postoffice.model.Delivery;

/**
 * Project: PostOffice
 * Package: com.r0adkll.postoffice.ui
 * Created by drew.heavner on 8/20/14.
 */
public class Mail extends DialogFragment implements MailboxInterface{

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
    public static Mail createInstance(){
        Mail mail = new Mail();
        return mail;
    }

    /**********************************************************
     *
     * Variables
     *
     */

    private Mailbox mMailbox;

    /**
     * Empty constructor
     */
    public Mail(){}

    /**********************************************************
     *
     * Lifecycle Methods
     *
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Safety check the Mailbox interface to ensure that it isn't null
        if(mMailbox == null){
            dismiss();
            return;
        }


        mMailbox.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Safety check the Mailbox interface to ensure that it isn't null
        if(mMailbox == null){
            dismiss();
            return null;
        }

        View layout = mMailbox.onCreateView(inflater, container, savedInstanceState);
        if(layout == null)
            layout = super.onCreateView(inflater, container, savedInstanceState);

        return layout;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Safety check the Mailbox interface to ensure that it isn't null
        if(mMailbox == null){
            dismiss();
            return null;
        }

        return mMailbox.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mMailbox.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mMailbox.onCancel(dialog);
    }

    /**********************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Apply a delivery configuration to the fragment
     *
     * @param delivery      the delivery configuration construct
     */
    public void setConfiguration(Delivery delivery){
        mMailbox = Mailbox.createInstance(delivery, this);
    }

    /**
     * Create the default {@link android.app.Dialog} object for this fragment
     *
     * @param icicle        the savedInstanceState
     * @return              the native Dialog object
     */
    @Override
    public Dialog createSuperDialog(Bundle icicle) {
        return super.onCreateDialog(icicle);
    }
}
