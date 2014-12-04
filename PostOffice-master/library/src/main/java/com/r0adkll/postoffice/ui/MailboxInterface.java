package com.r0adkll.postoffice.ui;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

/**
 * This is the abstracted communication layer between the Mailbox and the
 * Fragments
 *
 * Created by r0adkll on 10/20/14.
 */
public interface MailboxInterface {

    public Activity getActivity();

    public Dialog getDialog();

    public Dialog createSuperDialog(Bundle icicle);


    public void setCancelable(boolean cancelable);

    public void setStyle(int style, int theme);

    public void dismiss();

}
