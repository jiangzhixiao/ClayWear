package com.umfundi.wear.claywear.itemRenderer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flexicious.nestedtreedatagrid.FlexDataGrid;
import com.flexicious.nestedtreedatagrid.interfaces.IFlexDataGridCell;
import com.umfundi.wear.claywear.ClayWearApplication;
import com.umfundi.wear.claywear.SportScoringActivity;
import com.umfundi.wear.claywear.model.StandScore;

@SuppressLint("NewApi")
public class StandBoxViewRenderer extends LinearLayout {

    private TextView textView;
    private Object data;
    private ClayWearApplication app;

    // TODO final added to Context to get around
    // todo need to redraw the grid somehow!

    public StandBoxViewRenderer (Context context) {
        super(context);
        app  = (ClayWearApplication)context.getApplicationContext();

 //       final FlexDataGrid grid = ((IFlexDataGridCell)getParent()).getColumn().getLevel().getGrid();

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setOrientation(LinearLayout.VERTICAL);
        setHorizontalGravity(Gravity.CENTER);
        textView = new TextView(context);
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        textView.setTextSize(20.0f);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(textView);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FlexDataGrid grid = ((IFlexDataGridCell)getParent()).getColumn().getLevel().getGrid();
                app.currentStand = Integer.parseInt(((StandScore)getData()).getStand());
                grid.setSelectedIndex(app.currentStand-1);

                app.currentTarget = 0;
                app.currentCycle = 1;
                grid.reDraw();
                // Message the activity to do a data sync
                Message msg = Message.obtain();
                msg.what=999;
                SportScoringActivity._handler.sendMessage(msg);
            }
        });
    }

public void setData(Object data)
    {
        this.data = data;
        StandScore s = (StandScore)data;

        String ss = s.getStand();
        textView.setText((CharSequence)ss);
    }

public Object getData(){
        return this.data;
    }

}