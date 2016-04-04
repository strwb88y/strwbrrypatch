package com.houndify.CalcTipApp;

 import android.content.Intent;
 import android.os.Bundle;
 import android.view.View;
 import android.widget.Button;
 import android.widget.CheckBox;
 import android.widget.EditText;
 import android.widget.TextView;
 import android.app.Activity;

 import android.app.AlertDialog;
 import android.content.Context;
 import android.content.DialogInterface;


public class CalcTip extends Activity {

    TextView tiptextView, totaltextView, taxtextView;
    TextView billamounttextView, pretaxtextView;
    EditText newTipPercentageText;

    final Context context = this;
    double posttaxbillAmt, pretaxbillAmt, taxAmt, tipAmt, totalAmt;
    static final double TAXRATE = 0.0875;
    static final double DEFAULT_TIP_PERCENTAGE=  0.15;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_tip);

        newTipPercentageText = (EditText) findViewById(R.id.editText);
        billamounttextView = (TextView) findViewById(R.id.textView5);
        pretaxtextView= (TextView) findViewById(R.id.textView3);
        taxtextView = (TextView) findViewById(R.id.textView12);
        tiptextView = (TextView) findViewById(R.id.textView13);
        totaltextView = (TextView) findViewById(R.id.textView11);

        Intent intent = getIntent();
        posttaxbillAmt = intent.getDoubleExtra("doubleBillAmt",0.0);
        pretaxbillAmt = posttaxbillAmt/(1+TAXRATE);
        taxAmt = pretaxbillAmt*TAXRATE;
        tipAmt = pretaxbillAmt*DEFAULT_TIP_PERCENTAGE;
        totalAmt = pretaxbillAmt+taxAmt+tipAmt;
        // Display everything
        billamounttextView.setText(Double.toString(posttaxbillAmt));
        pretaxtextView.setText(Double.toString(pretaxbillAmt));
        taxtextView.setText(Double.toString(taxAmt));
        tiptextView.setText(Double.toString(tipAmt));
        totaltextView.setText(Double.toString(totalAmt));
    }


    public void oncalctiptotalClick (View view) {
        double pretax = 0;
        double tip = 0;
        double total = 0;
        double tax = 0;

        boolean validTipPercentage = true;
        double newTipPercentage = 0;

        try {
            newTipPercentage = Double.parseDouble(newTipPercentageText.getText().toString());
            newTipPercentage = ((double) java.lang.Math.round(newTipPercentage*100))/100;
            String strnewTipPercentage = String.format("%.2f", newTipPercentage);
            newTipPercentageText.setText(strnewTipPercentage);
            if (newTipPercentage < 0) {
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
            validTipPercentage = false;
        }
        catch (InvalidCashAmountException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            // set title
            //alertDialogBuilder.setTitle("Your Title");
            // set dialog message
            alertDialogBuilder
                    .setMessage("Not a valid tip percentage.  Please try again.")
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
            validTipPercentage = false;
        }
        if (validTipPercentage) {
            pretax = Double.parseDouble(pretaxtextView.getText().toString());
            tip = pretax * newTipPercentage;
            tax = pretax * TAXRATE;
            total = pretax + tip + tax;
            String strTax = String.format("%.2f", tax);
            taxtextView.setText(strTax);
            String strTip = String.format("%.2f", tip);
            tiptextView.setText(strTip);
            String strTotal = String.format("%.2f", total);
            totaltextView.setText(strTotal);
        }
    }
}

