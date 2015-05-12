package com.umfundi.wear.claywear.dao;

//
// Singleton dataFactory class for layouts.
//
//
//
//
// encapsulate the layout and all the sub stuff so we are not passing stuff back and forth
//
// todo add stream designators to tinfo? so map the A,B,C streams to trap triggers?
//
// todo strip out RAF and similar???

import com.umfundi.wear.claywear.model.Round;

import java.util.Arrays;
import java.util.List;

public class RoundDataFactory {

//    private Context context;

    public String SPORTING;
    public String ESP;
    public String FIVES;
    public String FITS;
    public String SPTR;
    public String COPK;
    public String SPRA;
    public String SUPER;

    public String SKEET;
    public String ESK;
    public String ASK;
    public String SKD;
    public String OSK;

    public String TRAP;
    public String DTL;
    public String ABT;
    public String DRS;
    public String OTR;
    public String DTR;
    public String UT;
    public String ATA;
    public String ATD;

    public String FED;
    public String F32;
    public String FT3;
    public String FT5;
    public String ISF;

    public String HEL;

    public String REPPAIRS;
    public String SIMPAIRS;

    public String REPTRIPS;
    public String SIMTRIPS;

    public String SINGLES;
    public String REPORT;
    public String RAF;
    public String SIM;

    public String REPORT3;
    public String SIM3;
    public String RAF3;
    public String OSIM;
    public String SIMO;
    public String ORAF;
    public String RAFO;
    public String RSIM;

    public String[] sportingTargets;
    public String[] superTargets;
    public String[] aAllDisciplines;
    public String[] aFormatsSporting;
    public String[] aFormatsSkeet;
    public String[] aFormatsTrap;

    public String[] aFormatsFed;
    public String[] aFormatsHel;
    public String[] formatsString;

    public List<String> listSporting;
    public List<String> lSuper;
    public List<String> lAllDisciplines;
    public List<String> lFormatsTrap;
    public List<String> lFormatsSkeet;
    public List<String> lFormatsSporting;
    public List<String> lFormatsFed;
    public List<String> lFormatsHel;

    public List<String> lSportingTargets;
    public List<String> lAllTargetTypes;
    public List<String> lAllFormats;

    // todo default target setting ( should this be in the preferences? )
    public boolean sdef;

    // the round is the top of the heirachy at the moment.
    public Round theRound;


    private static RoundDataFactory instance;

    public static RoundDataFactory getInstance() {
        if (instance == null)
            instance = new RoundDataFactory();
        return instance;
    }

    public void initArrays() {
        // arrays to go into lists
        // keep the sporting start indeces the same as the supersporting
        sportingTargets = new String[]{SINGLES, REPORT, SIM, RAF};
        superTargets = new String[]{SINGLES, REPORT, SIM, RAF, REPORT3, SIM3, RAF3, OSIM, SIMO, ORAF, RAFO, RSIM};

        aAllDisciplines = new String[]{SPORTING, TRAP, SKEET, FED, HEL};
        aFormatsSporting = new String[]{ESP, FIVES, FITS, SPTR, COPK, SPRA, SUPER};
        aFormatsSkeet = new String[]{ESK, ASK, SKD, OSK};
        aFormatsTrap = new String[]{DTL, ABT, DRS, OTR, DTR, UT, ATA, ATD};
        aFormatsFed = new String[]{F32, FT3, FT5, ISF};
        aFormatsHel = new String[]{HEL};

        formatsString = new String[]{ESP, FIVES, FITS, SPTR, COPK, SPRA, ESK, ASK, SKD, OSK, DTL, ABT, DRS, OTR, DTR, UT, ATA, ATD, F32, FT3, FT5, ISF, HEL};

        listSporting = Arrays.asList(sportingTargets);
        lAllDisciplines = Arrays.asList(aAllDisciplines);
        lFormatsSporting = Arrays.asList(aFormatsSporting);
        lFormatsSkeet = Arrays.asList(aFormatsSkeet);
        lFormatsTrap = Arrays.asList(aFormatsTrap);
        lFormatsFed = Arrays.asList(aFormatsFed);
        lFormatsHel = Arrays.asList(aFormatsHel);

        lSportingTargets = Arrays.asList(sportingTargets);
        // Currently the same as SuperSporting as that discipline uses every target type ( use for mode selection )
        lAllTargetTypes = Arrays.asList(superTargets);
        lAllFormats = Arrays.asList(formatsString);

        sdef = true;
    }

    //
    // Creates a new round. A round is the top of the heirachy ( for the moment! )
    //
    public void createRound() {
        theRound = new Round();
    }

    public RoundDataFactory() {

    }


}
