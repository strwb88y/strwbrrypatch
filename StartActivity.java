package com.houndify.CalcTipApp;

import android.app.Activity;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_start );

        findViewById( R.id.btn_hound_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(StartActivity.this, HoundVoiceSearchExampleActivity.class));
            }
        });

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
