package com.umfundi.wear.claywear;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.umfundi.wear.claywear.model.Layout;

// import com.umfundi.wear.claywear.dao.DataFactory;

/**
 * Created by ianmolesworth on 06/04/15.
 */
public class MobileSetFormatActivity extends Activity {

    protected ClayWearApplication app;

    private NumberPicker npDiscipline;
    private NumberPicker npFormat;

    private Button btBuild;
    private Button btAbandon;

    private Layout workingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameselector);

        app = (ClayWearApplication)this.getApplication();

        // todo ??
        workingLayout = (Layout)app.df.theRound.getLayouts().get(1);

        npDiscipline = (NumberPicker) findViewById(R.id.disciplinePicker);
        npFormat = (NumberPicker) findViewById(R.id.formatPicker);

        btBuild = (Button) findViewById(R.id.btnBuild);

        npDiscipline.setMaxValue(app.df.aAllDisciplines.length - 1);
        npDiscipline.setDisplayedValues(app.df.aAllDisciplines);

        npDiscipline.setMeasureWithLargestChildEnabled(false);
        npDiscipline.setMinimumWidth(250);
        npFormat.setMeasureWithLargestChildEnabled(false);
        npFormat.setMinimumWidth(300);


        String s = workingLayout.getDiscipline();
        if (!( s == null)){
            npDiscipline.setValue(app.df.lAllDisciplines.indexOf(s));
            // Need to work out what discipline we are currently using
            // set the numberpicker values to the right discipline
            setFormatPicker(app.df.lAllDisciplines.indexOf(s));
            // go and point to the right format on the format picker
            setCurrentFormat();
        } else {
            npDiscipline.setValue(app.df.lAllDisciplines.indexOf(app.df.ESP));
            setFormatPicker(app.df.lAllDisciplines.indexOf(app.df.ESP));
        }

        npDiscipline.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(
                    NumberPicker picker,
                    int oldVal,
                    int newVal) {
                setFormatPicker(newVal);
            }
        });

        btBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workingLayout.setDiscipline(app.df.lAllDisciplines.get(npDiscipline.getValue()));
                // Initialize the dataFactory with blank Layout in the current format

                if (workingLayout.getDiscipline().equals(app.df.SPORTING))
                    workingLayout.setFormat(app.df.lFormatsSporting.get(npFormat.getValue()));
                else if (workingLayout.getDiscipline().equals(app.df.SKEET))
                    workingLayout.setFormat(app.df.lFormatsSkeet.get(npFormat.getValue()));
                else if (workingLayout.getDiscipline().equals(app.df.TRAP))
                    workingLayout.setFormat(app.df.lFormatsTrap.get(npFormat.getValue()));
                else if (workingLayout.getDiscipline().equals(app.df.HEL))
                    workingLayout.setFormat(app.df.lFormatsHel.get(npFormat.getValue()));
                else if (workingLayout.getDiscipline().equals(app.df.FED))
                    workingLayout.setFormat(app.df.lFormatsFed.get(npFormat.getValue()));

                workingLayout.buildDefaultLayoutPattern();
                finish();
            }
        });
    }


    private void setFormatPicker(int v) {

        if (v == app.df.lAllDisciplines.indexOf(app.df.SPORTING)){
            npFormat.setValue(0);
            npFormat.setDisplayedValues(null);
            npFormat.setMaxValue (app.df.aFormatsSporting.length -1 );
            npFormat.setDisplayedValues (app.df.aFormatsSporting);


        } else if (v == app.df.lAllDisciplines.indexOf(app.df.HEL)){
            npFormat.setValue(0);
            npFormat.setDisplayedValues(null);
            npFormat.setMaxValue (app.df.aFormatsHel.length -1 );
            npFormat.setDisplayedValues (app.df.aFormatsHel);

        } else if (v == app.df.lAllDisciplines.indexOf(app.df.FED)){
            npFormat.setValue(0);
            npFormat.setDisplayedValues(null);
            npFormat.setMaxValue (app.df.aFormatsFed.length -1 );
            npFormat.setDisplayedValues (app.df.aFormatsFed);

        } else if (v == app.df.lAllDisciplines.indexOf(app.df.TRAP)){
            npFormat.setValue(0);
            npFormat.setDisplayedValues(null);
            npFormat.setMaxValue (app.df.aFormatsTrap.length -1 );
            npFormat.setDisplayedValues (app.df.aFormatsTrap);

        } else if (v == app.df.lAllDisciplines.indexOf(app.df.SKEET)){
            npFormat.setValue(0);
            npFormat.setDisplayedValues(null);
            npFormat.setMaxValue (app.df.aFormatsSkeet.length -1 );
            npFormat.setDisplayedValues (app.df.aFormatsSkeet);

        }
    }

    // set the current format to the one in the datafactory
    private void setCurrentFormat(){
        String s = workingLayout.getDiscipline();
        String f = workingLayout.getFormat();
        if (s.equals(app.df.SPORTING)){
            npFormat.setValue(app.df.lFormatsSporting.indexOf(f));
        } else if (s.equals(app.df.HEL)){
            npFormat.setValue(app.df.lFormatsHel.indexOf(f));
        } else if (s.equals(app.df.FED)){
            npFormat.setValue(app.df.lFormatsFed.indexOf(f));
        } else if (s.equals(app.df.TRAP)){
            npFormat.setValue(app.df.lFormatsTrap.indexOf(f));
        } else if (s.equals(app.df.SKEET)){
            npFormat.setValue(app.df.lFormatsSkeet.indexOf(f));
        }
    }
}
