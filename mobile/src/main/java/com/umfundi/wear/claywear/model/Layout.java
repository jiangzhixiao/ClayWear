package com.umfundi.wear.claywear.model;

import android.util.Log;

import com.umfundi.wear.claywear.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

public class Layout {

    // Todo set this to the manifest version
    private String fileVersion = "1.00";

    private String layoutID;                        // This needs to be universally unique ( perhaps tied to the PC or device that created it, so a time/date and UUID )
    private String lockState;                       // Locked prevents an uploaded Layout from being modified by this user - fixed by round/competition owner?
    // TODO - a scheme to unlock!
    private String description;                     //

    private String Discipline;                      // Flush, Sporting, FITASC etc
    private String Format;                          // Compact, Supersport, ESP etc
    private Map<Integer, Map> LayoutData;           //
//    private List<StandScore> theScoreCard;          // The scorecard is the scoring representation of this layout.

    // the scores associated with this round
    public Map<Integer, Object> Scores;

    protected ClayWearApplication app;              //

    public Layout(String lid, String discipline, String format, ClayWearApplication a) {
        layoutID = lid;
        Discipline = discipline;
        Format = format;
        app = a;
        buildDefaultLayoutPattern();
    }

    public void buildDefaultLayoutPattern() {
        // Create a blank layout
        LayoutData = new LinkedHashMap<Integer, Map>();
        Scores = new LinkedHashMap<Integer, Object>();

        if ((Format.compareTo(app.df.FITS) == 0)) {
            for ( int stand=1;stand <4;stand++){
                // Add a stand to the end of the layout
                LayoutData.put(LayoutData.size() + 1, generateNewStand(stand));
            }
            // English Sporting and Sportasc = 10 stands
        } else if ((Format.compareTo(app.df.COPK) == 0) ||
                    (Format.compareTo(app.df.SPTR) == 0) ||
                     (Format.compareTo(app.df.DTL) == 0) ||
                      (Format.compareTo(app.df.ABT) == 0) ||
                       (Format.compareTo(app.df.DRS) == 0) ||
                        (Format.compareTo(app.df.ATA) == 0) ||
                         (Format.compareTo(app.df.ATD) == 0) ||
                          (Format.compareTo(app.df.FIVES) == 0)) {
            //5 Stands for these formats
            for ( int stand=1;stand <6;stand++){
                // Add a stand to the end of the layout
                LayoutData.put(LayoutData.size() + 1, generateNewStand(stand));
            }
        } else if ((Format.compareTo(app.df.ESK) == 0) ||
                    (Format.compareTo(app.df.ASK) == 0) ||
                     (Format.compareTo(app.df.OSK) == 0)) {
            //8 Stands for SKEET - 7 plus option
            for ( int stand=1;stand <9;stand++){
                // Add a stand to the end of the layout
                LayoutData.put(LayoutData.size() + 1, generateNewStand(stand));
            }
        } else if ((Format.compareTo(app.df.SUPER) == 0)) {
            //9 stands of supersport
            for ( int stand=1;stand <10;stand++){
                // Add a stand to the end of the layout
                LayoutData.put(LayoutData.size() + 1, generateNewStand(stand));
            }
        } else if ((Format.compareTo(app.df.ESP) == 0) || (Format.compareTo(app.df.SPRA) == 0)) {
            //10 Stands
            for (int stand = 1; stand < 11; stand++) {
                // Add a stand to the end of the layout
                LayoutData.put(LayoutData.size() + 1, generateNewStand(stand));
            }
        } else if ((Format.compareTo(app.df.DTR) == 0)) {
            for ( int stand=1;stand <16;stand++){
                // Add a stand to the end of the layout
                LayoutData.put(LayoutData.size() + 1, generateNewStand(stand));
            }
        }else if ((Format.compareTo(app.df.SKD) == 0) ||
                   (Format.compareTo(app.df.OTR) == 0) ||
                    (Format.compareTo(app.df.UT) == 0)) {
            //8 Stands for SKEET - 7 plus option
            for ( int stand=1;stand <26;stand++){
                // Add a stand to the end of the layout
                LayoutData.put(LayoutData.size() + 1, generateNewStand(stand));
            }
        }
    }

    //
    // A new Stand adds defaults according to the Format we are shooting and the stand # we are building!
    // todo HEL,FED
    private Map<Integer,Map> generateNewStand(int stand) {
        Map<Integer,Map> tempStand = new LinkedHashMap<Integer, Map>();
        if (Format.compareTo(app.df.ESP) == 0) {
            tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.REPORT), 10 ));
            // Double Rise 5 sim pairs per stand
        } else if (Format.compareTo(app.df.DRS) == 0) {
            tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 10));
            // Sportasc
        }else if (Format.compareTo(app.df.SPRA) == 0) {
            tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 4));
            tempStand.put(2, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.REPORT), 2));
            tempStand.put(3, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.REPORT), 2));
            tempStand.put(4, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
        } else if (Format.compareTo(app.df.FITS) == 0) {
            // For FITASC we cant be sure so lets add a 5 single shot cycle and a report pair and a sim pair
            // user must change cycles individually
            tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 1));
            tempStand.put(2, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.REPORT), 2));
            tempStand.put(3, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
        } else if (Format.compareTo(app.df.SUPER) == 0) {
            // For Supersporting we cant be sure so lets add 3 Report Triples
            // user must change cycle individually
            tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.REPORT3), 9));
        } else if ((Format.compareTo(app.df.COPK) == 0) ||
                (Format.compareTo(app.df.SPTR) == 0) ||
                (Format.compareTo(app.df.FIVES) == 0)) {
            // For Sport trap we go a single, report pair and sim pair
            // user must change cycle individually
            tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 1));
            tempStand.put(2, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.REPORT), 2));
            tempStand.put(3, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
        } else if ((Format.compareTo(app.df.ESK) == 0) ) {
            // For English Skeet the format changes based on stand being shot
            if (stand == 8 )
                tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 1));
            else {
                tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 2));
                if (stand != 3 && stand != 7 )
                    tempStand.put(2, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
            }
        } else if ((Format.compareTo(app.df.ASK) == 0) ) {
            // For American Skeet the format changes based on stand being shot
            if (stand == 8)
                tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 1));
            else {
                tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 2));
                if (stand != 3 && stand != 4 && stand != 5)
                    tempStand.put(2, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
            }
        } else if ((Format.compareTo (app.df.OSK) == 0) ) {
            // For English Skeet the format changes based on stand being shot
            if (stand == 8 )
                tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 1));
            else {
                tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 2));
                if (stand != 3 && stand != 7 )
                    tempStand.put(2, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
            }
        } else if ((Format.compareTo(app.df.SKD) == 0) ) {
            // Skeet Doubles - effectively 25 stands!
            if (stand == 7)
                tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
            else {
                tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 1));
                if (stand == 4 || stand == 8) {
                    tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 1));
                    if (stand == 4)
                        tempStand.put(2, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
                } else
                    tempStand.put(3, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 1));
            }
        } else if ((Format.compareTo(app.df.DTR) == 0) ) {
            tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
        } else if ((Format.compareTo(app.df.DTL) == 0) ||
                (Format.compareTo(app.df.ABT) == 0) ||
                (Format.compareTo(app.df.ATA) == 0) )  {
            for (int cycle = 1; cycle < 6; cycle++)
                tempStand.put(cycle, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 1));
        } else if ((Format.compareTo(app.df.ATD) == 0) ||
                (Format.compareTo(app.df.ABT) == 0) ||
                (Format.compareTo(app.df.ATA) == 0) )  {
            for (int cycle = 1; cycle < 6; cycle++)
                tempStand.put(cycle, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
        } else if ((Format.compareTo(app.df.UT) == 0) ||
                (Format.compareTo(app.df.OTR) == 0) ) {
            tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SINGLES), 1));
        } else if ((Format.compareTo(app.df.SKD) == 0)) {
            tempStand.put(1, addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.SIM), 2));
        }
        return tempStand;
    }

    //
    // Adds a stand at the location in the layout
    // inserts if necessary.
    //
    //
    public void addStandAt(int location){
        Map<Integer, ArrayList> aStand;
        if (location == LayoutData.size()){
            // Add a stand to the end of the layout
            LayoutData.put(LayoutData.size() + 1, generateNewStand(location));
        } else {
            // shuffle the indices!
            for (int i = LayoutData.size();i >= location; i--) {
                aStand = LayoutData.get(i);
                // move the one up.
                LayoutData.put(i+1,aStand);
                // and delete the old space data - clean but not entirely necessary as the put dereferences the old
                // object in there. but lets play nice!
                LayoutData.remove(i);
            }
            // add a stand to the cleared out position.
            LayoutData.put(location,generateNewStand(location));
        }
    }

    public void removeStandAt(int location){
        Map<Integer, ArrayList> aStand;
        int e = LayoutData.size();
        if (location == e){
            LayoutData.remove(location);
        } else {
            // remove at the location and inc the pointer
            LayoutData.remove(location++);
            // shuffle the indices!
            while (location <= e) {
                aStand = LayoutData.get(location);
                // move the last up one.
                LayoutData.put(location- 1, aStand);
                LayoutData.remove(location);
                location++;
            }
        }
    }

    // todo !!! Cycles stuff still not complete 0 offset kills this!!!
    public void addCycleAt(int stand, int location){
        Map<Integer, Map> theStand = LayoutData.get(stand);
        // todo - cockup!!!!! getting a cycle with a stand number!!!!
        Map<Integer, ArrayList> theCycle = theStand.get(location);
        if (location == theStand.size()){
            theStand.put(theStand.size()+1, generateNewCycle());
        } else {
            // shuffle the indices!
            // work from the end backwards putting at +1 offset.
            // Dont do 0!!!!
            for (int i = theStand.size();i >= location; i--) {
                theCycle = theStand.get(i);
                // move the last up one.
                theStand.put(i+1,theCycle);
            }
            // Put a new cycle into the space we've made
            theStand.put(location, generateNewCycle());
        }
        LayoutData.put(stand,theStand);
    }

    public void removeCycleAt(int stand, int location){
        Map<Integer, Map> theStand = LayoutData.get(stand);
        if (theStand.size() > 1) {
            Map<Integer, ArrayList> theCycle;
            int e = theStand.size();
            if (location == e) {
                theStand.remove(location);
            } else {
                // remove at the location and inc the pointer
                theStand.remove(location++);
                // shuffle the indices!
                while (location <= e) {
                    theCycle = theStand.get(location);
                    // move the last up one.
                    theStand.put(location - 1, theCycle);
                    theStand.remove(location);
                    location++;
                }

            }
            LayoutData.put(stand, theStand);
        }
    }

    private Map<Integer, ArrayList> generateNewCycle(){
        // For ESP we should not come this route!!!!
        // todo - check formats
        //
        // Sportasc
        Map<Integer, ArrayList> aCycle = new LinkedHashMap<Integer, ArrayList>();
        // We add a report pair of targets to everything as a new cycle.
        return addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), app.df.lAllTargetTypes.indexOf(app.df.REPORT), 2);

    }

    //
    // build a target list of length 'n' ( Number of targets! ) and type 'type'
    // and add it to the cycle Pattern 'thePattern'
    // Return the modified pattern.
    //
    // add targets to a cycle
    // @peram   format          the String name of the format we are working with
    // @param   n               the targets to add
    // @peram   aCycle          the cycle Array
    // @return  aCycle          the adjusted cycle Array
    //
    // todo change n to number of cycles not number of targets?
    //
    private Map<Integer, ArrayList> addCycleTarget(LinkedHashMap<Integer, ArrayList> aCycle, int type, int n) {
        Log.i("Layout.java","method addCycleTarget");
        ArrayList<Integer> tinfo = new ArrayList<Integer>();

        tinfo.add(0,type);
        int o = 0;

        if (type == app.df.lAllTargetTypes.indexOf(app.df.SINGLES)){
            o = n;
            tinfo.add(1,1);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.RAF)){
            o=n/2;
            tinfo.add(1,2);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.SIM)){
            o=n/2;
            tinfo.add(1,2);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.REPORT)) {
            o=n/2;
            tinfo.add(1,2);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.RAF3)){
            o=n/3;
            tinfo.add(1,3);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.SIM3)){
            o=n/3;
            tinfo.add(1,3);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.REPORT3)){
            o=n/3;
            tinfo.add(1,3);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.ORAF)){
            o=n/3;
            tinfo.add(1,3);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.RAFO)){
            o=n/3;
            tinfo.add(1,3);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.OSIM)){
            o=n/3;
            tinfo.add(1,3);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.SIMO)){
            o=n/3;
            tinfo.add(1,3);
        } else if (type == app.df.lAllTargetTypes.indexOf(app.df.RSIM)){
            o=n/3;
            tinfo.add(1,3);
        }
        tinfo.add(2,o);
        aCycle.put(0,tinfo);

        ArrayList<Boolean> scores = new ArrayList<Boolean>();
        // Build an array of score booleans to match the
        for ( int i = 0; i<n ; i++)
            scores.add(i,new Boolean(app.df.sdef));
        aCycle.put(1,scores);
        return aCycle;
    }

    // change this target type.
    // if we go to singles, make it 10 targets for ESP,
    // pairs, sims, rafaels ESP adds 5 pairs
    // sportask, sporttrap compac 1 pair
    public void setCycleTargetType(int stand, int cycle, int type){
        // Make a hole in the layout to be replaced with the new stand
        // Build a blank cycle
        Log.i("Layout.java","method setCycleTargetType");
        Map<Integer, ArrayList> aCycle;
        // English Sporting we replace with 10 targets
        if (Format.equals(app.df.ESP)) {
            aCycle = addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), type, 10);
        } else {
            // the add Targets method need te number of targets.
            if (type == app.df.lAllTargetTypes.indexOf(app.df.SINGLES)){
                aCycle = addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), type, 1);
            } else if (type == app.df.lAllTargetTypes.indexOf(app.df.RAF) ||
                    type == app.df.lAllTargetTypes.indexOf(app.df.SIM) ||
                    type == app.df.lAllTargetTypes.indexOf(app.df.REPORT)) {
                aCycle = addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), type, 2);
            } else
                aCycle = addCycleTarget(new LinkedHashMap<Integer, ArrayList>(), type, 3);
        }

        // get a copy of the existing Stand
        Map<Integer, Map> aStand = LayoutData.get(stand);
        // remove the old cycle from the stand
        aStand.remove(cycle);

        // put the generated cycle into the copy of the original stand
        aStand.put(cycle,aCycle);
        LayoutData.remove(stand);
        LayoutData.put(stand, aStand);
    }

    //
    // adjust the number of targets in a given cycle!
    // this looks at the existing content and adds more of the same type or
    // truncates the target list.
    //
    public void adjCycleTargetCount(int newVal, int stand, int cycle) {
        // If we reduce the number of targets we strip out the extra targets
        Log.i("Layout.java","method adjCycleTargetCount");
        Map<Integer, Map> aStand = LayoutData.get(stand);
        Map<Integer, ArrayList> aCycle = aStand.get(cycle);
        // get the existing info
        ArrayList<Integer> tinfo = aCycle.get(0);
        // put the new length into the info stream
        tinfo.remove(2);
        tinfo.add(2,newVal);
        // remove old info
        aCycle.remove(0);
        // put the new info back into the cycle
        aCycle.put(0,tinfo);

        ArrayList<Boolean> scores = new ArrayList<Boolean>();
        // Build an array of score booleans to match them
        for ( int i = 0; i<newVal*tinfo.get(1); i++)
            scores.add(i,new Boolean(app.df.sdef));
        aCycle.put(1,scores);

        // remove the old stand
        aStand.remove(cycle);
        // and put back the new one
        aStand.put(cycle,aCycle);
        // and finally repeat with the stand into the layout!
        LayoutData.remove(stand);
        LayoutData.put(stand, aStand);
    }


/*
    public Layout(JSONObject json) throws JSONException {
        fileVersion = json.getString("Version");
        Discipline = json.getString("Format");
        layoutID = json.getString("LayoutID");
        if (json.has("lock"))
            lockState = json.getString("Lock");
        if (json.has("Period"))
            period = json.getInt("Period");
        if (json.has("Description"))
            description = json.getString("Description");
        if (json.has("Pegs"))
            standpegs = json.getInt("Pegs");
        if (json.has("Patterns")) {
            patterns = new ArrayList<Pattern>();
            JSONArray parray = new JSONArray(json.getString("Patterns"));
            JSONObject j = new JSONObject();
            for (int z = 0; z < parray.length(); z++) {
                j = (JSONObject) parray.get(z);
                Pattern p = new Pattern(j.getInt("T1"), j.getInt("T2"), j.getInt("T3"), j.getInt("T4"), j.getInt("T5"), j.getInt("T6"), j.getInt("T7"), j.getInt("T8"), j.getInt("Cycle"), j.getInt("Turn"), j.getInt("Stand"));
                patterns.add(j.getInt("Idx"), p);
            }
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jobj = new JSONObject();
        JSONArray json = new JSONArray();
        int idx = 0;
        for (Pattern p : patterns)
            json.put(p.toJSON(idx++));
        jobj.put("Patterns", json);
        jobj.put("Period", period);
        jobj.put("Pegs", standpegs);
        jobj.put("Discipline", Discipline);
        jobj.put("Format", Format);
        jobj.put("Lock", lockState);
        jobj.put("LayoutID", layoutID);
        jobj.put("Version", fileVersion);
        return jobj;
    }

*/

    public String getDiscipline() {
        return Discipline;
    }

    public void setDiscipline(String m) {
        Discipline = m;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String m) {
        Format = m;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        description = d;
    }

    public String getLock() {
        return lockState;
    }

    public void setLock(String l) {
        lockState = l;
    }

    public void setLayoutData(Map<Integer, Map> data) {
        LayoutData = data;
    }

    public Map<Integer, Map> getLayoutData() {
        return LayoutData;
    }

    public int countStands(){
        return LayoutData.size();
    }

    public int getCycleCount(int stand){
        Map<Integer, ArrayList> aStand = LayoutData.get(stand);
        return aStand.size();
    }

    public int getTargetLength(int stand, int cycle){
        Map<Integer, Map> aStand = LayoutData.get(stand);
        Map<Integer, ArrayList> aCycle = aStand.get(cycle);
        ArrayList tinfo = aCycle.get(0);
        return (int) tinfo.get(2);
    }

    //
    // Get total number of targets in the layout
    //
    public int getTotalTargets() {
        int total = 0;
        for (int stand = 1; stand<= LayoutData.size(); stand++){
            Map<Integer, Map> aStand = LayoutData.get(stand);
            for (int cycle = 1; cycle <= aStand.size(); cycle ++ ) {
                Map<Integer, ArrayList> aCycle = aStand.get(cycle);
                ArrayList<Integer> tinfo = aCycle.get(0);
                total += tinfo.get(1)*tinfo.get(2);
            }
        }
        return total;
    }

    //
    // Get the number of targets in a particular cycle
    // todo also visible from target stream 0 ( tinfo )
    public int getTargetCount(int stand, int cycle) {
        Map<Integer, Map> aStand = LayoutData.get(stand);
        Map<Integer, ArrayList> aCycle = aStand.get(cycle);
        ArrayList<Integer> tinfo = aCycle.get(0);
        return tinfo.get(1)*tinfo.get(2);
    }

    public int getMaxWidth(){
        int w = 0;
        for (int stand = 1; stand<= LayoutData.size(); stand++) {
            Map<Integer, Map> aStand = LayoutData.get(stand);
            for (int cycle = 1; cycle <= aStand.size(); cycle++) {
                Map<Integer, ArrayList> aCycle = aStand.get(cycle);
                ArrayList<Integer> tinfo = aCycle.get(0);
                int l = tinfo.get(1)*tinfo.get(2);
                if (l>w)
                    w=l;
            }
        }
        return w;
    }

    public int getTargetType(int stand, int cycle){
        Map<Integer, Map> aStand = LayoutData.get(stand);
        Map<Integer, ArrayList> aCycle = aStand.get(cycle);
        // get the existing info
        ArrayList<Integer> tinfo = aCycle.get(0);
        return tinfo.get(0);
    }

    // Returns a scorecard template that matches this layout.
    public List getScorePattern(){
        int l = LayoutData.size();
        List<StandScore> theScorePattern = new ArrayList<StandScore>();

        for ( int stand = 1; stand<=l; stand++){
            // get the stands.
            Map<Integer, Map> aStand = LayoutData.get(stand);
            // todo - set the default target to a preference value not fixed 1
            // todo - set the scoring type to the current score type!
            int t = 1;
            if (Format.equals(app.df.ESP))
                t=0;
            StandScore data = new StandScore(1, aStand, t);
            data.setStand(valueOf(stand));
            theScorePattern.add(data);
        }
        return theScorePattern;
    }

    public String getLayoutId(){
        return layoutID;
    }


}