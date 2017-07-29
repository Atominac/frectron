package com.fretron.fleet.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.fretron.fleet.login.SmsListener;

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();

        //Object[] pdus = (Object[]) data.get("pdus");
        final Object[] pdus = (Object[]) data.get("pdus");

        if (pdus != null) {
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);

                String sender = smsMessage.getDisplayOriginatingAddress();
                //You must check here if the sender is your provider and not another one with same text.

                //String messageBody = smsMessage.getMessageBody();
                String message = smsMessage.getDisplayMessageBody();
                mListener.messageReceived(message);
            }
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}

