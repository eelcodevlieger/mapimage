package com.github.mapimage.domain;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Sources:
 * https://en.wikipedia.org/wiki/Wikipedia:WikiProject_Politics_of_the_United_Kingdom/Index_of_United_Kingdom_political_parties_meta_attributes
 * manual mapping: https://webtest.parliament.uk/uk-general-elections/constituencies
 *
 * Excel formula:
 * <pre>
 *     =CONCAT(UPPER(SUBSTITUTE(SUBSTITUTE(SUBSTITUTE(SUBSTITUTE(SUBSTITUTE(SUBSTITUTE(E2, " ", "_"), "-", "_"), "'", ""), "&", "_AND_"), "(", ""), ")", "")),"(")
 * </pre>
 */
@Slf4j
public enum Party {
    ABOLISH_THE_SCOTTISH_PARLIAMENT_PARTY("#1D8DFF"),
    ABOLISH_THE_WELSH_ASSEMBLY_PARTY("#810000"),
    ADVANCE_TOGETHER("#FF008F"),
    ALBA_PARTY("#005EB8"),
    ALL_FOR_IRELAND_LEAGUE("#66FF99"),
    ALLIANCE_FOR_GREEN_SOCIALISM("#00A86B", "AGS", "GREEN SOC"),
    ALLIANCE_PARTY_OF_NORTHERN_IRELAND("#F6CB2F", "APNI", "ALLIANCE"),
    ANTI_FEDERALIST_LEAGUE("#F6CB2F"),
    ANTI_H_BLOCK("#008800"),
    ANTI_PARTITION_OF_IRELAND_LEAGUE("#DDFFDD"),
    ANTI_WASTE_LEAGUE("lightblue"),
    AONTU("#44532A"),
    ASHFIELD_INDEPENDENTS("pink", "ASH", "ASHFIELD"),
    ASPIRE_POLITICAL_PARTY("#FF5800"),
    BASINGSTOKE__AND__DEANE_INDEPENDENTS("#026B04"),
    BLAENAU_GWENT_PEOPLES_VOICE("#177245", "BGPV"),
    BREAKTHROUGH_PARTY("#F38B3D"),
    BRITISH_DEMOCRATIC_PARTY_2013("#284571"),
    BELFAST_LABOUR_PARTY("#cd5c5c"),
    BOSTON_BYPASS_INDEPENDENTS("#FFFF00"),
    BOSTON_DISTRICT_INDEPENDENTS("#F8F9FA"),
    BREXIT_PARTY("#12B6CF"),
    BRITISH_MOVEMENT("black"),
    BRITISH_NATIONAL_PARTY("#2e3b74", "BNP"),
    BRITISH_NATIONAL_PARTY_1960("#000080"),
    BRITWELLIAN("#bb00bb"),
    BURNING_PINK("#FF69B4"),
    CAMDEN_CHARTER("white"),
    CANNABIS_IS_SAFER_THAN_ALCOHOL("#D2B48C", "CSA"),
    CANVEY_ISLAND_INDEPENDENT_PARTY("#000000"),
    CHANGE_UK("#222221"),
    CHRISTIAN_PEOPLES_ALLIANCE("#813887", "CPA"),
    CHRISTIAN_PARTY_UK("#9966CC", "Christian Party, Proclaiming Christ's Lordship", "SCP"),
    CHRISTCHURCH_INDEPENDENTS("#999999"),
    CHURCH_OF_THE_MILITANT_ELVIS_PARTY("#FF00FF"),
    COALITION_CONSERVATIVE("#0087DC"),
    COALITION_LABOUR("#e0afaf"),
    COALITION_LIBERAL("#FFF890"),
    THE_COMMON_GOOD_POLITICAL_PARTY("#F77FBE"),
    COMMON_WEALTH_PARTY("#ff7f50"),
    COMMUNIST_LEAGUE_UK_1988("#C71585"),
    COMMUNIST_PARTY_OF_BRITAIN("#F93822"),
    COMMUNIST_PARTY_OF_ENGLAND_MARXIST_LENINIST("#660000"),
    COMMUNIST_PARTY_OF_GREAT_BRITAIN("red"),
    COMMUNIST_PARTY_OF_IRELAND("#E3170D"),
    COMMUNIST_PARTY_OF_IRELAND_MARXIST_LENINIST("#660000"),
    COMMUNIST_PARTY_OF_NORTHERN_IRELAND("red"),
    COMMUNITY_ACTION_PARTY("#33FF33"),
    COMMUNITY_CAMPAIGN_HART("#8d19ff"),
    COMMUNITY_GROUP("#808080", "C Grp"),
    THE_COMMUNITY_GROUP_LONDON_BOROUGH_OF_HOUNSLOW("#228B22"),
    CONSERVATIVE_AND_LIBERAL_UNIONIST("#0281aa"),
    CONSERVATIVE_AND_NATIONAL_LIBERAL("#AFEEEE"),
    CONSERVATIVE_PARTY_UK("#0087DC", "CON"),
    CONSERVATIVE_RESIDENT("#00ADEF"),
    CONSERVATIVE_TRADE_UNIONIST("white"),
    CONSTITUTIONALIST_UK("#8C92AC"),
    COUNTRYSIDE_PARTY_UK("olive"),
    CROSSBENCH("grey"),
    DEMOCRATIC_LABOUR_PARTY_UK("#E32636"),
    DEMOCRATIC_LEFT_IRELAND("#C700C7"),
    DEMOCRATIC_PARTY_UK_1942("lightyellow"),
    DEMOCRATIC_UNIONIST_PARTY("#D46A4C", "DUP"),
    EAST_LEEDS_INDEPENDENTS("#e0c200"),
    ECOLOGY_PARTY("yellowgreen"),
    ENGLISH_DEMOCRATS_PARTY("#915F6D", "ED", "EDP", "ENG DEM"),
    EPSOM_AND_EWELL_RESIDENTS_ASSOCIATION("pink"),
    FEDERATION_OF_LABOUR_IRELAND("#DC241f"),
    FELLOWSHIP_PARTY("#7FFFD4"),
    FOR_BRITAIN_MOVEMENT("#431B5B"),
    FORWARD_WALES("#FF3333"),
    GARFORTH_AND_SWILLINGTON_INDEPENDENTS("#52F72E"),
    GREEN_PARTY_OF_NORTHERN_IRELAND("#8dc63f"),
    GREEN_PARTY_OF_ENGLAND_AND_WALES("#02A95B"),
    GREEN_PARTY_UK("#528D6B", "GRN", "GREEN PARTY"),
    HAVERING_RESIDENTS_ASSOCIATION("#264404"),
    HEAVY_WOOLLEN_DISTRICT_INDEPENDENTS("#696969"),
    HERITAGE_PARTY_UK("#0A00A5"),
    HIGHLANDS_AND_ISLANDS_ALLIANCE("purple"),
    HOME_RULE_LEAGUE("#99FF66"),
    HORWICH_AND_BLACKROD_FIRST("#00a8b0"),
    HUMANIST_PARTY_UK("orange"),
    INDEPENDENCE_FOR_SCOTLAND_PARTY("#2980B9"),
    INDEPENDENT_POLITICIAN("#DCDCDC"),
    INDEPENDENT_BRITWELLIAN_RESIDENTS("pink"),
    INDEPENDENT_CONSERVATIVE("#DDEEFF"),
    INDEPENDENT_GREEN("#ccffcc"),
    INDEPENDENT_KIDDERMINSTER_HOSPITAL_AND_HEALTH_CONCERN("hotpink", "KHHC", "ICHC"),
//    INDEPENDENT_LABOUR("#FFBBBB"),
//    INDEPENDENT_LABOUR_PARTY("#B22222"),
    INDEPENDENT_LIBERAL("#FFFFAA"),
    INDEPENDENT_LIBERAL_DEMOCRAT("#FFEEAA"),
    INDEPENDENT_NETWORK("#483D8B", "INET"),
    INDEPENDENT_REPUBLICAN_IRELAND("#d1ffb2"),
    INDEPENDENT_RESIDENT("#dddddd"),
    INDEPENDENT_SOCIAL_DEMOCRAT("#D9B3FF"),
    INDEPENDENT_SOCIALIST("red"),
    INDEPENDENT_UNION_POLITICAL_PARTY("#203961"),
    INDEPENDENT_UNIONIST("#aadfff"),
    INDEPENDENT_WORKING_CLASS_ASSOCIATION("#0095B6"),
    IRISH_INDEPENDENCE_PARTY("#32CD32"),
    IRISH_LABOUR_PARTY("#CC0000"),
    IRISH_NATIONAL_FEDERATION("#00FA9A"),
    IRISH_NATIONAL_LEAGUE("#99FF66"),
    IRISH_PARLIAMENTARY_PARTY("#99FF66"),
    IRISH_REPEAL("#DDFFDD"),
    IRISH_UNIONIST_PARTY("#9999FF"),
    ISLAMIC_PARTY_OF_BRITAIN("#337263"),
    JURY_TEAM("#708090"),
    THE_JUSTICE__AND__ANTI_CORRUPTION_PARTY("Crimson"),
    CROSS_COMMUNITY_LABOUR_ALTERNATIVE("#cd5c5c"),
    LABOUR_CO_OPERATIVE("#E4003B"),
    LABOUR_CO_OPERATIVE_AND_TRADE_UNION("white"),
    LABOUR_PARTY_UK("#E4003B", "LAB"),
    LABOUR_PARTY_OF_NORTHERN_IRELAND("#DC241f"),
    LABOUR_REPRESENTATION_COMMITTEE("#b22222"),
    LABOUR_UNIONIST("#DDEEFF"),
    LEFT_LIST("red"),
    LEGALISE_CANNABIS_ALLIANCE("#669966"),
    LEWISHAM_PEOPLE_BEFORE_PROFIT("#9400d3"),
    LIBERAL_DEMOCRATIC_FOCUS_TEAM("#FAA61A"),
    LIBERAL_DEMOCRATS_UK("#FAA61A", "LIBERAL DEMOCRATS", "LD"),
    LIBERAL_PARTY_UK("#FFD700", "Liberal Party"),
    LIBERAL_PARTY_UK_1989("#EB7A43"),
    LIBERAL_UNIONIST_PARTY("#2061A2"),
    LIBERTARIAN_PARTY_UK("#FCC820"),
    LIBERTY_GB("#000168"),
    LINCOLN_DEMOCRATIC_LABOUR_ASSOCIATION("#FF6600"),
    LLAIS_GWYNEDD("#006400"),
    LOCAL_EDUCATION_ACTION_BY_PARENTS("#CCCCCC"),
    LORDS_SPIRITUAL("#7F00FF"),
    LOUGHTON_RESIDENTS_ASSOCIATION("#50C878"),
    MEBYON_KERNOW("#d5c229"),
    MONEY_REFORM_PARTY("#997A8D"),
    MORLEY_BOROUGH_INDEPENDENT("#006600"),
    NATIONAL_DEMOCRATIC_AND_LABOUR_PARTY("#ffdead"),
    NATIONAL_DEMOCRATIC_PARTY_NORTHERN_IRELAND("#DDFFDD"),
    NATIONAL_DEMOCRATS_UNITED_KINGDOM("#2F4F4F"),
    NATIONAL_FRONT_UK("MidnightBlue"),
    NATIONAL_HEALTH_ACTION_PARTY("#0071BB", "NHAP"),
    NATIONAL_PARTY_UK_1917("#327"),
    NATIONAL_GOVERNMENT_UNITED_KINGDOM("#cccccc"),
    NATIONALIST_PARTY_NORTHERN_IRELAND("#32cd32"),
    NATIONALIST_PARTY_UK("DarkGoldenrod"),
    NATIONAL_LABOUR_ORGANISATION("green"),
    NATIONAL_LIBERAL_PARTY_UK_1922("#FFF890"),
    NATIONAL_LIBERAL_PARTY_UK_1931("#AFEEEE"),
    NATIONAL_LIBERAL_AND_CONSERVATIVE("#AFEEEE"),
    NATIONAL_LIBERAL_PARTY_UK_1999("#FF6600"),
    NATIONAL_PARTY_OF_SCOTLAND("#FFFF66"),
    NATURAL_LAW_PARTY("#ffe4e1"),
    NEWCASTLE_INDEPENDENTS("#000000"),
    NEWHAM_INDEPENDENT("#DDDDDD", "NIP"),
    NEW_BRITAIN_PARTY("#999999"),
    NEW_ENGLAND_PARTY("#2F4F4F"),
    NEW_NATIONALIST_PARTY_UNITED_KINGDOM("#F8F9FA"),
    NEW_PARTY_UK("black"),
    THE_NEW_PARTY_UK_2003("#0000FF"),
    NEWTOWNABBEY_RATEPAYERS_ASSOCIATION("#F8F9FA"),
    NI21("#008080"),
    THE_NORTH_EAST_PARTY("#800000", "North East"),
    NORTHERN_IRELAND_CONSERVATIVES("#0087DC"),
    NORTHERN_IRELAND_LABOUR_PARTY("#DC241f"),
    NORTHERN_IRELAND_UNIONIST_PARTY("darkorange"),
    NORTHERN_IRELAND_WOMENS_COALITION("aqua"),
    NORTHERN_INDEPENDENCE_PARTY("#9E1C34"),
    NORTHERN_PARTY("#FF8C00"),
    NO_OVERALL_CONTROL("black"),
    NO_TO_THE_EU_YES_TO_DEMOCRACY("darkgrey"),
    OFFICIAL_MONSTER_RAVING_LOONY_PARTY("#FFF000"),
    OPERATION_CHRISTIAN_VOTE("#9966CC"),
    OXTED_AND_LIMPSFIELD_RESIDENTS_GROUP("lime"),
    PEACE_AND_PROGRESS_PARTY("#CCCCFF"),
    PEACE_PARTY_UK("#F58231"),
    PEELITE("#99FF99"),
    PEOPLE_AGAINST_BUREAUCRACY("black"),
    PEOPLE_BEFORE_PROFIT("#E91D50", "PBPA", "PBP Alliance"),
    PEOPLES_DEMOCRACY_IRELAND("red"),
    THE_PEOPLES_INDEPENDENT_PARTY("#CFDC96"),
    PEOPLES_JUSTICE_PARTY_UK("#7fffd4"),
    PLAID_CYMRU("#005B54", "PC"),
    POOLE_ENGAGE_PARTY("#97b4c9"),
    POOLE_PEOPLE("#6600CC"),
    PORTISHEAD_INDEPENDENTS("#000000"),
    PROGRESSIVE_UNIONIST_PARTY("#2B45A2"),
    PRO_ASSEMBLY_UNIONIST("#FFA07A"),
    PRO_DEMOCRACY_LIBERTAS_EU("#B0E0E6"),
    PROLIFE_ALLIANCE("#333333"),
    PROTESTANT_UNIONIST_PARTY("#D46A4C"),
    PUTTING_CUMBRIA_FIRST("#72A041"),
    PUTTING_HARTLEPOOL_FIRST("#017F7E"),
    RADICALS_UK("#FF3333"),
    RAINHAM__AND__WENNINGTON_INDEPENDENT_RESIDENTS_GROUP("#dddddd"),
    RATEPAYERS_ASSOCIATION("#dddddd"),
    RATEPAYERS_AND_CITIZENS_ASSOCIATION("#dddddd"),
    RATEPAYERS_AND_RESIDENTS_ASSOCIATIONS("#dddddd"),
    RED_FRONT_UK("Red"),
    RECLAIM_PARTY("#C03F31"),
    REFERENDUM_PARTY("#bf475c"),
    REFORM_UK("#12B6CF", "RUK"),
    REJOIN_EU("#003399"),
    RENEW_PARTY("#16C0D7"),
    REPUBLICAN_CLUBS("#EE0000"),
    REPUBLICAN_LABOUR_PARTY("#85de59"),
    RESIDENTS_ASSOCIATION("#d3d3d3"),
    RESIDENTS_FOR_UTTLESFORD("#00a88f"),
    RESPECT_PARTY("#46801c", "RESP"),
    REVOLUTIONARY_COMMUNIST_PARTY_UK_1978("#880000"),
    ROCHFORD_DISTRICT_RESIDENTS("#930000"),
    ROCK_N_ROLL_LOONY_PARTY("#c71585"),
    RUNNYMEDE_INDEPENDENT_RESIDENTS_GROUP("#264404"),
    SAORADH("#4A5D23"),
    SAVE_CHASE_FARM("#0066FF"),
    SAVE_OUR_BEESTON_AND_HOLBECK_INDEPENDENTS("#F8F9FA"),
    SAY_NO_TO_EUROPEAN_UNION("#DDDDDD"),
    SCOTTISH_CHRISTIAN_PARTY("#9966CC"),
    SCOTTISH_CONSERVATIVES("#0087DC"),
    SCOTTISH_DEMOCRATIC_ALLIANCE("MediumOrchid"),
    SCOTTISH_FISHING_PARTY("#000000"),
    SCOTTISH_GREEN_PARTY("#00B140"),
    SCOTTISH_JACOBITE_PARTY("#9966CC"),
    SCOTTISH_LABOUR_PARTY("#E4003B"),
    SCOTTISH_LIBERTARIAN_PARTY("#F0DC83"),
    SCOTTISH_NATIONAL_PARTY("#FDF38E", "SNP"),
    SCOTTISH_PARTY("#99CCFF"),
    SCOTTISH_PEOPLES_ALLIANCE("#87cefa"),
    SCOTTISH_PENSIONERS_PARTY("#BBD9DB"),
    SCOTTISH_PROHIBITION_PARTY("purple"),
    SCOTTISH_SENIOR_CITIZENS_UNITY_PARTY("#CC66CC"),
    SCOTTISH_SOCIALIST_ALLIANCE("#990033"),
    SCOTTISH_SOCIALIST_PARTY("#ff0000"),
    SCOTTISH_UNIONIST_PARTY_1986("#5555FF"),
    SCOTTISH_VOICE("#660099"),
    SCOTTISH_WORKERS_REPRESENTATION_COMMITTEE("#b22222"),
    SDP_LIBERAL_ALLIANCE("#FFD700"),
    SINN_FÉIN("#326760", "SF"),
    SOCIAL_DEMOCRATIC_AND_LABOUR_PARTY("#2AA82C", "SDLP"),
    SOCIAL_DEMOCRATIC_FEDERATION("red"),
    SOCIAL_DEMOCRATIC_PARTY_UK("#6C2f56"),
    SOCIAL_DEMOCRATIC_PARTY_UK_1988("#2C478A"),
    SOCIAL_DEMOCRATIC_PARTY_UK_1990_PRESENT("#D25469"),
    SOCIAL_AND_LIBERAL_DEMOCRATS("#FAA61A"),
    SOCIALIST_ALLIANCE_ENGLAND("red"),
    SOCIALIST_CURRENT_ORGANISATION("red"),
    SOCIALIST_ENVIRONMENTAL_ALLIANCE("#BB0000"),
    SOCIALIST_EQUALITY_PARTY_UK("#960018"),
    SOCIALIST_GREEN_UNITY_COALITION("#DD0000"),
    SOCIALIST_LABOUR_PARTY_UK("#EE1C25", "SL"),
    SOCIALIST_PARTY_ENGLAND_AND_WALES("#ED1941"),
    SOCIALIST_PARTY_OF_GREAT_BRITAIN("#DC241f"),
    SOCIALIST_REPUBLICAN_PARTY_IRELAND("#228B22"),
    SOCIALIST_UNITY_UK("#FF4D00"),
    SOCIALIST_WORKERS_PARTY_BRITAIN("#FF355E"),
    SOLIDARITY_SCOTLAND("#a22f32"),
    SOMETHING_NEW_POLITICAL_PARTY("#000000"),
    SOUTH_HOLLAND_INDEPENDENTS("#87CEEB"),
    SOVEREIGNTY("#F8F9FA"),
    SPEAKER_OF_THE_HOUSE_OF_COMMONS_UNITED_KINGDOM("black", "SPEAKER", "SPK"),
    ST_NEOTS_INDEPENDENTS("#F8F9FA"),
    TENDRING_FIRST("#EC8425"),
    THIRD_WAY_UK_ORGANISATION("#2C8028"),
    THURROCK_INDEPENDENTS("#FBEA24"),
    TRADE_UNIONIST_AND_SOCIALIST_COALITION("#EC008C", "TUSC"),
    TRADITIONAL_UNIONIST_VOICE("#0C3A6A", "TUV"),
    UK_INDEPENDENCE_PARTY("#6D3177", "UKIP"),
    ULSTER_CONSERVATIVES_AND_UNIONISTS("#9999FF", "UCUNF"),
    ULSTER_DEMOCRATIC_PARTY("black"),
    ULSTER_LIBERAL_PARTY("#DAA520"),
    ULSTER_POPULAR_UNIONIST_PARTY("#ffdead"),
    ULSTER_UNIONIST_PARTY("#48A5EE", "UU", "UUP"),
    UNIONIST_PARTY_OF_NORTHERN_IRELAND("#ffa07a"),
    UNIONIST_PARTY_SCOTLAND("#5555FF"),
    UK_UNIONIST_PARTY("#660066"),
    UNION_MOVEMENT("#2F4F4F"),
    UNITED_ULSTER_UNIONIST_PARTY("#ff8c00"),
    UNITED_UNIONIST_COALITION("#888888"),
    UNITY_NORTHERN_IRELAND("olive"),
    VANGUARD_UNIONIST_PROGRESSIVE_PARTY("darkorange"),
    VERITAS_POLITICAL_PARTY("#663399", "Ver"),
    VETERANS_AND_PEOPLES_PARTY("#BF9F62"),
    VOLT_UK("#502379"),
    VOTE_FOR_YOURSELF_RAINBOW_DREAM_TICKET("#FFC0CB"),
    PROPEL_POLITICAL_PARTY("#0b8e36"),
    WERRINGTON_FIRST("#754B53"),
    WESSEX_REGIONALIST_PARTY("#318B58"),
    WEST_SUFFOLK_INDEPENDENTS("#6EFFC5"),
    WHIG_BRITISH_POLITICAL_PARTY("#FF7F00"),
    WOMENS_PARTY_UK("#ff1493"),
    WOMENS_EQUALITY_PARTY("#432360"),
    WORKERS_PARTY_OF_BRITAIN("#780021", "WPB"),
    WORKERS_PARTY_OF_IRELAND("#F8F9FA"),
    WORKERS_REVOLUTIONARY_PARTY_UK("#AA0000"),
    YORKSHIRE_PARTY("#00AEEF"),
    // MANUALLY ADDED:
    TRUST("#0090CB"),
    SOCIALIST_ALTERNATIVE("#ED1941", "S Alt"),
	BIRKENHEAD_SOCIAL_JUSTICE_PARTY("#B2393E"),
    INDEPENDENT_SAVE_OUR_GREEN_BELT("#00FF00", "ISGB"),
    INDEPENDENT_SAVE_WITHYBUSH_SAVE_LIVES("#DCDCDC", "ISWSL", "SWSL"),
    THE_INDEPENDENT_GROUP_FOR_CHANGE("#222221", "IGC"),
    NO_VOTE("black"), // placeholder Party to represent people who did not vote
    OTHER("white"), // placeholder Party to represent tiny unmapped parties
    INDEPENDENT("#DDDDDD", "IND"); // placeholder Party for small independent parties

    public final Color color;
    private final String[] abbreviations;

    private static final int PARTY_UNMAPPED_PERCENTAGE_THRESHOLD_ERROR = 6;
    private static final int PARTY_UNMAPPED_PERCENTAGE_THRESHOLD_WARN = 1;

    private static final Map<String, Party> byAbbreviation = new HashMap<>();
    static {
        for (var party : Party.values()) {
            for (var abbreviation : party.abbreviations) {
                var oldValue = byAbbreviation.put(abbreviation.toUpperCase(), party);
                if(oldValue != null) {
                    System.err.println("Abbreviations used should be unique [abbreviation=" + abbreviation + "]");
                    System.err.flush();
                    System.exit(-1);
                }
            }
        }
    }

    Party(final String cssColor){
        this(cssColor, null);
    }
    
    Party(final String cssColor, final String... abbreviations){
        this.color = getColor(cssColor);

        final List<String> abbreviationList = abbreviations != null ? Arrays.asList(abbreviations) : Collections.emptyList();
        final Set<String> abbreviationSet = new HashSet<>(abbreviationList);

        var partyAbbr = this.name().replaceAll("_", " ");
        var strippedPartyAbbr = partyAbbr.replaceAll(" PARTY( UK)?$", "");

        abbreviationSet.add(partyAbbr);
        abbreviationSet.add(strippedPartyAbbr);

        this.abbreviations = abbreviationSet.toArray(new String[abbreviationSet.size()]);
    }

    @SneakyThrows
    private Color getColor(String cssColor) {
        // https://www.w3schools.com/cssref/css_colors.php
        final Map<String, String> cssColorToHex = new HashMap<>();
        cssColorToHex.put("lightblue", "#ADD8E6");
        cssColorToHex.put("lightyellow", "#FFFFE0");
        cssColorToHex.put("yellowgreen", "#9ACD32");
        cssColorToHex.put("olive", "#808000");
        cssColorToHex.put("grey", "#808080");
        cssColorToHex.put("darkgrey", "#A9A9A9");
        cssColorToHex.put("purple", "#800080");
        cssColorToHex.put("hotpink", "#FF69B4");
        cssColorToHex.put("Crimson", "#DC143C");
        cssColorToHex.put("MidnightBlue", "#191970");
        cssColorToHex.put("DarkGoldenrod", "#B8860B");
        cssColorToHex.put("darkorange", "#FF8C00");
        cssColorToHex.put("aqua", "#00FFFF");
        cssColorToHex.put("lime", "#00FF00");
        cssColorToHex.put("MediumOrchid", "#BA55D3");

        cssColor = cssColorToHex.getOrDefault(cssColor, cssColor);

        if(cssColor.startsWith("#")){
            if(cssColor.length() == 4){
                cssColor = "#" + cssColor.charAt(1) + cssColor.charAt(1) + cssColor.charAt(2) + cssColor.charAt(2) + cssColor.charAt(3) + cssColor.charAt(3);
            }
            return Color.decode(cssColor);
        }

        return (Color) Color.class.getDeclaredField(cssColor.toLowerCase()).get(null);
    }

    public static Party getByAbbreviation(final String abbreviation, final int percentage){
        var party = byAbbreviation.get(abbreviation.toUpperCase());
        if (party == null) {
			if(percentage >= PARTY_UNMAPPED_PERCENTAGE_THRESHOLD_ERROR) {
                System.err.println("Party abbreviation not found/mapped [abbreviation=" + abbreviation + ", percentage=" + percentage + "]");
                System.err.flush();
                System.exit(-1);
            }else if(percentage >= PARTY_UNMAPPED_PERCENTAGE_THRESHOLD_WARN) {
                log.warn("Party abbreviation not found/mapped [abbreviation={}, percentage={}]", abbreviation, percentage);
            }else {
                log.debug("Party abbreviation not found/mapped - rendering as OTHER [abbreviation={}, percentage={}]", abbreviation, percentage);
			}
            return Party.OTHER;
        }
        return party;
    }
}
