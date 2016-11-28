package com.example.martin.nextflight;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.martin.nextflight.managers.ScreenUtility;

public class ConverterActivity extends AppCompatActivity {

    private String item = "Pesos";
    private String item2 = "Pesos";
    private double rToP = 4.58;
    private double dToR = 3.38;
    private double dToP = 15.49;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        final ScreenUtility screenUtility = new ScreenUtility(this);

        Spinner spinner = (Spinner) findViewById(R.id.converter_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.converter_spinner1_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                item = adapterView.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                // vacio
            }
        });

        Spinner spinner2 = (Spinner) findViewById(R.id.converter_spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.converter_spinner1_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView2, View view2, int position2, long id2)
            {
                item2 = adapterView2.getItemAtPosition(position2).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView2)
            {
                // vacío
            }
        });

        Button button = (Button) findViewById(R.id.converter_button);
        if (button != null) {
            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView) findViewById(R.id.converter_converted_text);
                    EditText editText = (EditText) findViewById(R.id.converter_number1);
                    TextView textView2 = (TextView) findViewById(R.id.converter_succes);
                    if (editText != null && textView != null) {
                        try {
                            String text = editText.getText().toString();
                            double number = Double.parseDouble(text);
                            number = convertNumber(number);
                            textView.setText(Double.toString(number));
                            if (screenUtility.getWidth() > 700.0 && textView2 != null){
                                textView2.setText("Conversión exitosa");
                            }
                        } catch(Exception e) {
                            textView.setText("");
                            if (screenUtility.getWidth() > 700.0 && textView2 != null){
                                textView2.setText("");
                            }
                        }
                    }
                }
                private double convertNumber (double n){
                    if (item.equals("Pesos")){
                       if (item2.equals("Reales")){
                           n*=1/rToP;
                       } else if (item2.equals("Dólares")){
                           n*=1/dToP;
                       }
                    } else if (item.equals("Reales")){
                        if (item2.equals("Pesos")){
                            n*=rToP;
                        } else if (item2.equals("Dólares")){
                            n*=1/dToR;
                        }
                    } else {
                        if (item2.equals("Pesos")){
                            n*=dToP;
                        } else if (item2.equals("Reales")){
                            n*=dToR;
                        }
                    }
                    n = Math.floor(n * 100) / 100;
                    return n;
                }

            });
        }
    }

}
