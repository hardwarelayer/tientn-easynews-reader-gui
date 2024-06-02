package tientn.easynews.game.data;

import java.util.Map;
import static java.util.Map.entry;
import lombok.Getter;
import lombok.Setter;

public interface Constants {

  final int GAME_TICK_PER_DAY = 4;

  final String DEFAULT_GAME_SAVE_FILE_NAME = "default_save_game_file.ttgs";
  final int CITY_DEFAULT_SECURITY_LEVEL = 4;
  final int CITY_BUILDING_PRODUCE_DEFAULT_ORDER_QTY = 10;

  static final String CITY_TALK_LOGICS = "talk_logics";
  static final String CITY_REACT_TRIGGER_PROMPT = "prompt";
  static final String CITY_REACT_TRIGGER_CONFIRM = "confirm";
  static final String CITY_REACT_TRIGGER_CONDITION = "condition";
  static final String CITY_REACT_TRIGGER_COMPLETE = "complete";

  static final String CITY_BUILDER_ACTION_TYPE = "builder";
  static final String CITY_POP_ACTION_TYPE = "pop";
  static final String CITY_PRODUCE_ACTION_TYPE = "building_produce";

  static final String CITY_BUILDING_COST = "cost";
  static final String CITY_BUILDING_PRODUCE = "produce";
  static final String CITY_BUILDING_STATUS_IDLING = "idle";
  static final String CITY_BUILDING_STATUS_PRODUCE = "producing";

  static final String CITY_BUILDING_PRODUCE_TYPE1 = "pop";
  static final String CITY_BUILDING_PRODUCE_TYPE2 = "storage";
  static final String CITY_BUILDING_PRODUCE_TYPE3 = "food";
  static final String CITY_BUILDING_PRODUCE_TYPE4 = "brick";
  static final String CITY_BUILDING_PRODUCE_TYPE5 = "tool";
  static final String CITY_BUILDING_PRODUCE_TYPE6 = "steel";
  static final String CITY_BUILDING_PRODUCE_TYPE7 = "defense";
  static final String CITY_BUILDING_PRODUCE_TYPE8 = "weapon";
  static final String CITY_BUILDING_PRODUCE_TYPE9 = "archer";
  static final String CITY_BUILDING_PRODUCE_TYPE10 = "footman";
  static final String CITY_BUILDING_PRODUCE_TYPE11 = "calvary";

  static final String CITY_BUILDING_REACTION = "reaction";
  static final String CITY_BUILDING_REACT_TYPE = "builder";
  static final String CITY_BUILDING_REACT_TRIGGER_BUILD = "build";
  static final String CITY_BUILDING_REACT_TRIGGER_REMOVE = "remove";
  static final String CITY_BUILDING_REACT_TRIGGER_PRODUCE = "lack_material";

  static final String CITY_POP_REACT_TRIGGER_INCREASE = "value_increase";
  static final String CITY_POP_REACT_TRIGGER_DECREASE = "value_decrease";

  static final int CITY_LAND_BARE_STORAGE_CAP = 4;
  static final int BLOCK_PER_LAND_UNIT = 4;
  static final String CITY_BUILD_BLOCK_NAME = "land_block";
  static final String CITY_BUILD_TIME_NAME = "time";

  static final String CITY_BASIC_MATERIAL_1 = "food";
  static final String CITY_BASIC_MATERIAL_2 = "wood";
  static final String CITY_BASIC_MATERIAL_3 = "iron";
  static final String CITY_BASIC_MATERIAL_4 = "stone";
  static final String CITY_BASIC_MATERIAL_5 = "clay";
  static final String[] basicCityMaterials = {CITY_BASIC_MATERIAL_1, CITY_BASIC_MATERIAL_2, CITY_BASIC_MATERIAL_3, CITY_BASIC_MATERIAL_4, CITY_BASIC_MATERIAL_5};

  static final String CITY_BUILDING_HOUSE_TYPE = "house";
  static final String CITY_BUILDING_HOUSE_CITIZEN_NAME = "Citizen house";
  static final int CITY_BUILDING_HOUSE_CITIZEN_CAP = 4;
  static final String CITY_BUILDING_WAREHOUSE_TYPE = "warehouse";
  static final String CITY_BUILDING_STOCKADE_TYPE = "stockade";
  static final String CITY_BUILDING_ARMOURY_TYPE = "armoury";
  static final String CITY_BUILDING_ARCHERY_TYPE = "archery";
  static final String CITY_BUILDING_BARRACK_TYPE = "barrack";
  static final String CITY_BUILDING_STABLE_TYPE = "stable";
  static final String CITY_BUILDING_FARM_TYPE = "farm";
  static final String CITY_BUILDING_FISHFARM_TYPE = "aquafarm";

  static final String GUI_LABEL_SELECTED_CITY = "Selected City";
  static final String GUI_LABEL_CONSTRUCTION = "Construction";
  static final String GUI_LABEL_ADMINISTRATION = "Administration";
  static final String GUI_LABEL_ADMIN_PRODUCE = "Produce";
  static final String GUI_LABEL_MILLITARY = "Millitary";
  static final String GUI_LABEL_MIL_PATROL = "Patrol";
  static final String GUI_LABEL_BUILDING = "Building";

  static final String CITY_BASIC_INFO_POP = "Population";
  static final String CITY_BASIC_INFO_FREE_SPACE = "Free spaces";
  static final String CITY_BASIC_INFO_SECURITY = "Security";
  static final String CITY_BASIC_INFO_HAPPINESS = "Happiness";
  static final String CITY_BASIC_INFO_DEFENSE = "Defense";
  static final String CITY_BASIC_INFO_COORDINATE = "Coordinate";
  static final String CITY_BASIC_INFO_OWNER = "Owner";
  static final String CITY_BASIC_INFO_LEVEL = "Level";
  static final String CITY_BASIC_INFO_TROOP = "Troop";
  static final String CITY_BASIC_INFO_CAPABILITY = "Capability";
  static final String CITY_BASIC_INFO_CAP_LAND = "Land";
  static final String CITY_BASIC_INFO_CAP_FARM = "Farm";
  static final String CITY_BASIC_INFO_CAP_IRON = "Iron";
  static final String CITY_BASIC_INFO_CAP_STONE = "Stone";
  static final String CITY_BASIC_INFO_CAP_CLAY = "Clay";
  static final String CITY_BASIC_INFO_CAP_WATER = "Water";

  static final String GAME_PLAYER1 = "human1";
  static final String GAME_PLAYER2 = "bandit";
  static final String GAME_PLAYER3 = "ai1";

  public class QtyWithCap {
    @Getter @Setter private int qty;
    @Getter @Setter private int cap;
    public QtyWithCap(final int qty, final int cap) {
      this.qty = qty;
      this.cap = cap;
    }
    @Override
    public String toString() {
      return new StringBuilder(
        String.valueOf(this.qty) + "/" + 
        String.valueOf(this.cap)
        ).toString();
    }
  }

  public class GameEvent {
    @Getter @Setter private String city;
    @Getter @Setter private String type;
    @Getter @Setter private String trigger;
    @Getter @Setter private String react;
    @Getter @Setter private String msg;
    public GameEvent(final String city, final String type, final String trigger, final String react, final String msg) {
      this.city = city;
      this.type = type;
      this.trigger = trigger;
      this.react = react;
      this.msg = msg;
    }
    @Override
    public String toString() {
      return new StringBuilder(
        String.valueOf(this.city) + "|" + 
        String.valueOf(this.type) + "|" + 
        String.valueOf(this.trigger) + "|" + 
        String.valueOf(this.react) + "|" + 
        String.valueOf(this.msg)
        ).toString();
    }
  }
  final int MAP_WIDTH = 850;
  final int MAP_HEIGHT = 250;
  final int TILE_WIDTH = 25; // tile width
  final int TILE_HEIGHT = 25; // tile height
  final int MAP_HORZ_TILES = 34;
  final int MAP_VERT_TILES = 10;
  final int TILESET_COLS = 10; //size of Tile enum array, theory X
  final int TILESET_ROWS = 10; //size of Tile enum array, theory Y
  final int CITY_ICON_WIDTH = 10;
  final int CITY_ICON_HEIGHT = 10;

  static final Map<Integer, String> ECO_BUILD_NAMES = Map.ofEntries(
    entry(0, "Apartment"),
    entry(1, "Minishop"),
    entry(2, "Market"),
    entry(3, "Bike Shop"),
    entry(4, "Motorcycle Shop"),
    entry(5, "Automobile Shop"),
    entry(6, "Post Office"),
    entry(7, "Newspaper"),
    entry(8, "Workshop"),
    entry(9, "Gunshop"),
    entry(10, "Armoury"),
    entry(11, "Consumer Goods Workshop"),
    entry(12, "Small Weapon Workshop"),
    entry(13, "Apparel Workshop"),
    entry(14, "Logistic Center"),
    entry(15, "Minibank"),
    entry(16, "Local Bank"),
    entry(17, "Region Bank"),
    entry(18, "Central Bank"),
    entry(19, "Custom House")
    );

  static final Map<Integer, String> RES_BUILD_NAMES = Map.ofEntries(
    entry(0, "Village library"),
    entry(1, "Town library"),
    entry(2, "City library"),
    entry(3, "Metropolitant library"),
    entry(4, "Region library"),
    entry(5, "Central library"),
    entry(6, "Elementary School"),
    entry(7, "Primary School"),
    entry(8, "College"),
    entry(9, "Univesity"),
    entry(10, "Experimental facility"),
    entry(11, "Sanitary lab"),
    entry(12, "Electronic lab"),
    entry(13, "Weapon lab"),
    entry(14, "Flight Research lab"),
    entry(15, "Logistic Research lab"),
    entry(16, "Mass transport lab"),
    entry(17, "Army School"),
    entry(18, "Army College"),
    entry(19, "Army University")
    );

  static final String TTG_EXTENSION = ".ttg";
  static final String DIALOG_OK = "TTG_OK";
  static final String DIALOG_CANCEL = "TTG_CANCEL";

  static final int KANJI_TOTAL_SUBSET_SIZE = 20;
  static final int KANJI_MIN_TEST_CORRECT = 10;
  static final String KANJI_COMMA_DELIMITER = ",";

  //for jCoint price calculation
  static final double BUILDING_ECO_PRICE_MULTIPLIER = 0.75;
  static final double BUILDING_RES_PRICE_MULTIPLIER = 1.0;
  static final int BUILDING_ECO_PRICE_MINIMUM = 10;
  static final int BUILDING_RES_PRICE_MINIMUM = 20;

  //turn history parser constants
  static final String HI_KOMBAT_MOVE_TITLE = "Combat Move";
  static final String HI_NONKOMBAT_MOVE_TITLE = "Non Combat Move";
  static final String HI_PURCHASE_TITLE = "Purchase Units";
  static final String HI_BATTLE_TITLE = "Combat";
  static final String HI_PLACE_TITLE = "Place Units";

  static final String HI_CSV_SEPARATOR = "|";
  static final String HI_PLACE_SEPARATOR = " placed in ";

  static final String HI_TAG_DUMMY_START = "@START";
  static final String HI_PURCHASE_PATT = " buy ";
  static final String HI_BATTLE_LOC_TITLE = "Battle in ";
  static final String HI_TAG_BATTLE_SUMM = "@BATTLESUM:";
  static final String HI_BATTLE_REM_UNIT_PAT1 = " win "; //" remaining. ";
  static final String HI_BATTLE_REM_UNIT_PAT2 = " with ";
  static final String HI_TAG_BATTLE_REMM = "@BATTLEREM:";
  static final String HI_TAG_BATTLE_CASUALTIES = "@BATTLECAT:";
  static final String HI_BATTLE_ATK_PATT = " attack with ";
  static final String HI_BATTLE_DEF_PATT = " defend with ";
  static final String HI_TAG_BATTLE_ATK = "@BATTLEATK:";
  static final String HI_TAG_BATTLE_DEF = "@BATTLEDEF:";
  static final String HI_TAG_START_BATTLE_LOC = "@BATTLELOC:";
  static final String HI_MOVE_FROM_PATT = " moved from ";
  static final String HI_MOVE_TO_PATT = " to ";
  static final String HI_MOVE_TAKE_PATT = " take ";
  static final String HI_TAG_MOVE_TAKE_PROV = "@MOVETAKEN:";
  static final String HI_ROUND_IDX_PATT = ", round ";
  static final String HI_NONE_VAL = "None";

  //player turn order types
  static final int TURN_SEQ_FIRST = 0;
  static final int TURN_SEQ_MIDDLE = 1;
  static final int TURN_SEQ_LAST = 2;

  //map units
  static final String MAP_UNIT_INFANTRY = "Infantry";
  static final String MAP_UNIT_ELITE = "Elite";
  static final String MAP_UNIT_MARINE = "Marine";
  static final String MAP_UNIT_MECH_INFANTRY = "Mech.Inf";
  static final String MAP_UNIT_TANK = "Tank";
  static final String MAP_UNIT_APC = "ArmoredCar";
  static final String MAP_UNIT_TANKETTE = "Tankette";
  static final String MAP_UNIT_FLAK = "AAGun";
  static final String MAP_UNIT_ARTILLERY = "Artillery";
  static final String MAP_UNIT_EARLY_FIGHTER = "EarlyFighter";
  static final String MAP_UNIT_FIGHTER = "L.Fighter";
  static final String MAP_UNIT_FIGHTER2 = "Fighter";
  static final String MAP_UNIT_FIGHTER3 = "Adv.Fighter";
  static final String MAP_UNIT_BOMBER = "Bomber";
  static final String MAP_UNIT_BOMBER2 = "S.Bomber";
  static final String MAP_UNIT_SHIP_TORP_BOAT = "T.Boat";
  static final String MAP_UNIT_SHIP_SUBMARINE = "Submarine";
  static final String MAP_UNIT_SHIP_SUBMARINE2 = "S.Submarine";
  static final String MAP_UNIT_SHIP_CRUISER = "Cruiser";
  static final String MAP_UNIT_SHIP_CARRIER = "Carrier";
  static final String MAP_UNIT_SHIP_CARRIER2 = "B.Carrier";
  static final String MAP_UNIT_SHIP_TRANSPORT = "Transport";
  static final String MAP_UNIT_SHIP_TRANSPORT2 = "B.Transport";
  static final String MAP_UNIT_SHIP_DESTROYER = "Destroyer";
  static final String MAP_UNIT_SHIP_BATTLESHIP = "Battleship";

  //territory frontline icon, use in TerritoryAttachment
  static final int MAX_FRONTLINE_ICON_VISIBLE_TURNS = 4;

  //turn news
  static final String JBGTURN_NEWS_SMALLATTACK_PREFIX = "JTN_SMALL_ATTK_PREFX";
  static final String JBGTURN_NEWS_PAPER_NAME = "Tien's World War Edition";

  static final int TEST_WORD_IN_MAJOR_LIST = 0;
  static final int TEST_WORD_IN_ARTICLE = 1;
  static final int DEFAULT_KANJI_SUBSET_SIZE = 11;
  static final int MAX_DUMMY_IN_WORDMATCH_MODE = 5;
  static final int WORDMATCH_MAX_REMIND_CHARS = 6;
  static final int AUTODISP_REMIND_CHARS_LIMIT = 55; 
  static final int JCOIN_AMOUNT_FOR_ADD_KANJI_WORD = 2;
  static final int WORDMATCH_NORMAL_WORD_TIME = 25;
  static final int AUTO_KANJI_DISPLAY_WORD_PER_POINT = 10;
  static final int WORDMATCH2_KANJI_REPEAT = 2;

  static final int MIN_WIDTH=1200;
  static final int MIN_HEIGHT=1000;

  static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
  static final String JDBC_CON_STR = "jdbc:mysql://localhost:3306/doctiengnhat";
  static final String DB_USER = "tientn";
  static final String DB_PASSWORD = "123";  

}