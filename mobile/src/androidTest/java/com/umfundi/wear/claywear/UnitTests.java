package com.umfundi.wear.claywear;

import android.test.InstrumentationTestCase;

import com.umfundi.wear.claywear.dao.RoundDataFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// import com.umfundi.wear.claywear.dao.DataFactory;

/**
 * Created by ianmolesworth on 28/03/15.
 */

public class UnitTests extends InstrumentationTestCase {

    private com.umfundi.wear.claywear.MobileMainActivity mActivity;

    private Map<Integer, Map>roundPattern;
    private Map<Integer, Map> layoutPattern;

    private Map<Integer, ArrayList> standPattern;
    private ArrayList<Integer> targetPattern;

    private Map<Integer, Map> testPattern;
    private ArrayList<Integer> testArray;

/*    public void TestCase(String s) {

    } */

    protected void setUp() throws Exception{
        super.setUp();
    }


    public void testPatternType(){

        final RoundDataFactory df = DataFactory.getInstance();
        Map<Integer, Map> layoutPattern;
        Map<Integer, Map> standPattern;
        Map<Integer, Map> typePattern;
        Map<Integer, ArrayList> turnPattern;

        // Initialise strings ( done from resources when actually run )

        // Target types
        df.SINGLES = "Singles";
        df.REPORT = "Report";
        df.RAF = "Rafael";
        df.SIM = "Sim pair";
        df.REPORT3 = "Report Triple";
        df.SIM3 = "Sim Triple";
        df.RAF3 = "Triple Rafael";
        df.OSIM = "Single report Sim pair";
        df.SIMO = "Sim pair on report Single";
        df.ORAF = "Single on Report Rafael pair";
        df.RAFO = "Rafael on report Single";
        df.RSIM = "Rafael and Single";

        // Disciplines and their Formats
        df.SPORTING = "Sporting";
        df.ESP = "Esp";
        df.FIVES = "Fives";
        df.FITS = "Fitasc";
        df.SPTR = "Sportrap";
        df.COPK = "Compak";
        df.SPRA = "Sportasc";

        // todo Currently unused!
        df.FED = "Fedecat";
        df.F32 = "Fan 32";
        df.FT3 = "Trap 3";
        df.FT5 = "Trap 5";
        df.ISF = "Int Sport Fed";

        df.HEL = "Helice";

        df.SKEET = "Skeet";
        df.ESK = "English Skeet";
        df.ASK = "American Skeet";
        df.SKD = "Skeet Doubles";
        df.OSK = "Olympic Skeet";

        df.TRAP = "Trap";
        df.DTL = "DTL";
        df.ABT = "ABT";
        df.DRS = "Double Rise";
        df.OTR = "Olympic Trap";
        df.DTR = "Double Trap";
        df.UT = "Universal Trench";
        df.ATA = "ATA";
        df.ATD = "ATA Doubles";

        df.initArrays();

        String currentDiscipline = df.SPORTING;

        // Build a sporting layout
        String currentFormat = df.ESP;
        layoutPattern = df.buildDefaultLayoutPattern(currentFormat);

        // Check there are 10 stands
        int s = layoutPattern.size();
        assertEquals(s,10);

        // Get stand and check it has 1 turn
        standPattern = layoutPattern.get(1);
        s = standPattern.size();
        assertEquals(s,1);

        // Check that the turn is numbered "1" not 0
        assert (standPattern.containsKey(1));
        turnPattern = standPattern.get(1);

        testPattern = new HashMap<Integer, Map> ();


        assert true;
    }
}
