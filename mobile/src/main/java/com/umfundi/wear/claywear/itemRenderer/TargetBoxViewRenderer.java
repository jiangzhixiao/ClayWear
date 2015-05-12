package com.umfundi.wear.claywear.itemRenderer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flexicious.nestedtreedatagrid.FlexDataGrid;
import com.flexicious.nestedtreedatagrid.interfaces.IFlexDataGridCell;
import com.umfundi.wear.claywear.ClayWearApplication;
import com.umfundi.wear.claywear.R;
import com.umfundi.wear.claywear.SportScoringActivity;
import com.umfundi.wear.claywear.model.Layout;
import com.umfundi.wear.claywear.model.StandScore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

@SuppressLint("NewApi")
public class TargetBoxViewRenderer extends LinearLayout {

	private ImageView imageView;
	private Object data;
    private ClayWearApplication app;
	
	public TargetBoxViewRenderer(final Context context) {
		super(context);
        app  = (ClayWearApplication)context.getApplicationContext();

		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		setOrientation(LinearLayout.HORIZONTAL);
		setHorizontalGravity(Gravity.CENTER);
		imageView = new ImageView(context);
		imageView.setLayoutParams(new LayoutParams(50, LayoutParams.MATCH_PARENT));

        // todo - if not a simple hit or miss format then we set it to a text box!

		addView(imageView);
		
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                // need to check if this target box is within the number of targets on the stand.
				FlexDataGrid grid = ((IFlexDataGridCell)getParent()).getColumn().getLevel().getGrid();
                Integer b = (Integer) getValueByDataField();
                if( b != null) {
                    // todo different depending on format - now ints so could be 0,1,2 for dtl or just 0,1 toggle
                    if (b==0)
                        b=1;
                    else
                        b=0;

                    setValueByDataField(b);
                    // todo - in some formats we need to indicate scores not just hits!
                    if (((StandScore) data).getScoreFormat() == 0) {
                        if (b != 0) {
                            imageView.setImageResource(R.drawable.checkmarkcircle);
                        } else
                            imageView.setImageResource(R.drawable.dashimage);
                    } else {
                        // todo we need to change the format to text view  OR
                        // overlay images of numbers!!!
                    }
                    if (data instanceof StandScore) {
                        // todo - why are we doing this??? The setters don't appear to be setting the array!!!!
                        // i think the reflection circumvents the setters.
                        ((StandScore) data).valsToArray();
                        ((StandScore) data).calculateTotal();
                    }
                }

				((IFlexDataGridCell)getParent()).getRowInfo().refreshCells();
                app.currentStand = Integer.parseInt(((StandScore)getData()).getStand());
                int c = ((IFlexDataGridCell)getParent()).getColumn().getColIndex();
                grid.setSelectedIndex(app.currentStand-1);
                app.currentTarget = c-1;

                // get the layout data from the current layout in the round
                Layout theLayout = (Layout)app.df.theRound.getLayouts().get(app.df.theRound.getActiveLayoutIdx());
                Map<Integer,Map> layoutData = theLayout.getLayoutData();
                Map<Integer, Map> aStand = layoutData.get(app.currentStand);

                // todo - different for sporting and others!!!
                Map<Integer, ArrayList> aCycle = aStand.get(1);
                // get the existing info
                ArrayList<Integer> tinfo = aCycle.get(0);

                // find the cycle size
                app.currentCycle = (app.currentTarget/tinfo.get(1))+1;

				grid.reDraw();
                // tell the UI thread to update the wearable.
                Message msg = Message.obtain();
                msg.what=999;
                SportScoringActivity._handler.sendMessage(msg);
			}
		});

	}

	public void setData(Object data){
		this.data = data;
		if(data instanceof StandScore) {
            Integer i;
            i = (Integer) getValueByDataField();
            if (i == null)
                imageView.setImageResource(R.drawable.hash);
            // todo - in some formats we need to indicate scores not just hits!
            else if (((StandScore) data).getScoreFormat() == 0) {
                if (i != 0) {
                    imageView.setImageResource(R.drawable.checkmarkcircle);
                } else
                    imageView.setImageResource(R.drawable.dashimage);
            } else {
                // todo we need to change the format to text view  OR
                // overlay images of numbers!!!
            }
        }
	}

	public Object getData(){
		return data;
	}
	
	private Object getValueByDataField() {
        IFlexDataGridCell cell = null;
        if (getParent() instanceof IFlexDataGridCell)
            cell = (IFlexDataGridCell) getParent();
        if (cell == null)
            return null;
        String dataField = cell.getColumn().getDataField();
        // the datafield now has '1' through '200' in it, derived from the column title.
        Field field = null;
        try {
            field = data.getClass().getDeclaredField(dataField);
            field.setAccessible(true);
            return field.get(data);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

	private void setValueByDataField(Object value){
		IFlexDataGridCell cell = null;
		if(getParent() instanceof IFlexDataGridCell)
			cell = (IFlexDataGridCell) getParent();
		if(cell == null)
			return;
		String dataField = cell.getColumn().getDataField();
		Field field = null;
		try {
			field = data.getClass().getDeclaredField(dataField);
			field.setAccessible(true);
			field.set(data, value);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

}