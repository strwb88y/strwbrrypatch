package com.houndify.CalcTipApp;

import android.app.Activity;


import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class StartActivity extends Activity {
    EditText billamountTextView;
    final Context context = this;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        billamountTextView = (EditText) findViewById(R.id.editText2);

        double billAmount = 0;
        boolean validBillAmount = true;

        try {
            billAmount = Double.parseDouble(billamountTextView.getText().toString());
            billAmount = ((double) java.lang.Math.round(billAmount*100))/100;
            String strnewBillAmount = String.format("%.2f", billAmount);
            billamountTextView.setText(strnewBillAmount);
            if (billAmount < 0) {
                throw new InvalidCashAmountException();
            }
        }
        catch (java.lang.NumberFormatException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            // set title
            //alertDialogBuilder.setTitle("Your Title");
            // set dialog message
            alertDialogBuilder
                    .setMessage("Entry has invalid characters.  Please try again.")
                    .setCancelable(false)
                    .setNegativeButton("Ok, got it!",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            validBillAmount = false;
        }
        catch (InvalidCashAmountException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            // set title
            //alertDialogBuilder.setTitle("Your Title");
            // set dialog message
            alertDialogBuilder
                    .setMessage("Not a valid bill amount.  Please try again.")
                    .setCancelable(false)
                    .setNegativeButton("Ok, got it!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            validBillAmount = false;
        }
        if (validBillAmount) {


//        findViewById( R.id.btn_hound_search).setOnClickListener(new View.OnClickListener() {
//        @Override
//            public void onClick(final View view) {
//                startActivity(new Intent(StartActivity.this, HoundVoiceSearchExampleActivity.class));
//            }
//        });
            findViewById(R.id.btn_hound_search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    double localbillAmount = Double.parseDouble(billamountTextView.getText().toString());
                    Intent intent =
                            new Intent(StartActivity.this, CalcTip.class);
                    Bundle bun = new Bundle();
                    bun.putDouble("doubleBillAmt", localbillAmount);
                    intent.putExtras(bun);
                    startActivity(intent);
                }
            });
        }


        findViewById( R.id.btn_basic_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(StartActivity.this, CustomSearchActivity.class));
            }
        });

        TextView textView1 = (TextView)findViewById( R.id.title1);
        SpannableString spannableString =  new SpannableString( textView1.getText() );
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), 0);
        textView1.setText( spannableString );

        TextView textView2 = (TextView)findViewById( R.id.title2 );
        SpannableString spannableString2 =  new SpannableString( textView2.getText() );
        spannableString2.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString2.length(), 0);
        textView2.setText( spannableString2 );

    }

}
