package cn.greatoo.easymill.util;

public final class RobotConstants {

    private RobotConstants() {

    }

    public static final int BIT0								=	0b0000000000000001;
    public static final int BIT1								=	0b0000000000000010;
    public static final int BIT2								=	0b0000000000000100;
    public static final int BIT3								=	0b0000000000001000;
    public static final int BIT4								=	0b0000000000010000;
    public static final int BIT5								=	0b0000000000100000;
    public static final int BIT6								=	0b0000000001000000;
    public static final int BIT7								=	0b0000000010000000;
    public static final int BIT8								=	0b0000000100000000;
    public static final int BIT9								=	0b0000001000000000;
    public static final int BIT10								=	0b0000010000000000;
    public static final int BIT11								=	0b0000100000000000;
    public static final int BIT12								=	0b0001000000000000;
    public static final int BIT13								=	0b0010000000000000;
    public static final int BIT14								=	0b0100000000000000;
    public static final int BIT15								=	0b1000000000000000;

    // COMMAND REGISTERS
    public static final int PERMISSIONS							=	54;
    public static final int PERMISSIONS_PICK_GIVEN				=	BIT0;
    public static final int PERMISSIONS_PUT_GIVEN				=	BIT1;
    public static final int PERMISSIONS_PICK_RELEASE_ACK		=	BIT2;
    public static final int PERMISSIONS_PUT_RELEASE_ACK			=	BIT3;
    public static final int PERMISSIONS_JAWS_CHANGE_ACK			=	BIT4;
    public static final int PERMISSIONS_CHANGE_OK				=	BIT5;
    public static final int PERMISSIONS_BAR_MOVED_ACK			=	BIT6;
    public static final int PERMISSIONS_MOVEWAIT_GIVEN			=	BIT7;
    public static final int PERMISSIONS_MOVEWAIT_CONTINUE		=	BIT8;
    public static final int PERMISSIONS_DO_PRAGE				=	BIT9;

    // ROBOT CONTROLLER VALUE
    public static final int CONTROLLER_VALUE_FAULT_LED			=	BIT0;
    public static final int CONTROLLER_VALUE_CMOS_BATTERY_LOW	=	BIT1;
    public static final int CONTROLLER_VALUE_SCAN_ERROR         =   BIT2;

    // REGISTERS
    public static final int REGISTER_IPC_TO_ROBOT				= 	58;
    public static final int REGISTER_IPC_TO_ROBOT_PRAGE_CLAMP	= 	BIT14;
    public static final int REGISTER_IPC_TO_ROBOT_PRAGE_UNCLAMP = 	BIT15;

    // ROBOT STATUS REGISTERS
    public static final int STATUS								=	104;
    public static final int STATUS_PICK_RELEASE_REQUEST			=	BIT0;
    public static final int STATUS_PUT_CLAMP_REQUEST			=	BIT1;
    public static final int STATUS_PICK_FINISHED				=	BIT2;
    public static final int STATUS_PUT_FINISHED					=	BIT3;
    public static final int STATUS_PICK_OUT_OF_MACHINE			=	BIT4;
    public static final int STATUS_PUT_OUT_OF_MACHINE			=	BIT5;
    // BIT6 is not used
    public static final int STATUS_GRIPS_CHANGED_FINISHED		=	BIT7;
    public static final int STATUS_ROBOT_IN_JAW_CHANGE_POINT	=	BIT8;
    public static final int STATUS_ROBOT_MOVED_BAR				=	BIT9;
    // TEACHING
    public static final int STATUS_AWAITING_TEACHING			=	BIT10;
    public static final int STATUS_TEACHING_FINISHED			=	BIT11;
    // MOVE_WAIT
    public static final int STATUS_WAITING_AFTER_MOVE			=	BIT12;
    public static final int STATUS_MOVEWAIT_FINISHED			=	BIT13;
    // PRAGE
    public static final int STATUS_IOACTION_FINISHED			=	BIT14;

    // COMMAND IDS
    //	public static final int COMMAND_ASK_STATUS					=	20;
    public static final int COMMAND_ASK_STATUS					=	22;
    //	public static final int COMMAND_WRITE_SERVICE_GRIPPER		=	40; (for turn-assist)
    //	public static final int COMMAND_WRITE_SERVICE_HANDLING		=	41; (for turn-assist)
    //	public static final int COMMAND_WRITE_SERVICE_POINT			=	42; (for turn-assist)
    public static final int COMMAND_WRITE_REGISTER				=	43;
    public static final int COMMAND_WRITE_POSITION_REGISTER		=	44;
    public static final int COMMAND_WRITE_USERFRAME				=	45;
    public static final int COMMAND_SET_PERMISSIONS				=	50;
    public static final int COMMAND_START_SERVICE				=	51;
    public static final int COMMAND_WRITE_AIRBLOW				= 	54;
    public static final int COMMAND_RESTART_PROGRAM				=	60;
    public static final int COMMAND_RESET						=	61;
    public static final int COMMAND_ABORT						=	62;
    public static final int COMMAND_CONTINUE					=	63;
    public static final int COMMAND_RECALC_TCPS					=	64;
    public static final int COMMAND_SET_SPEED					=	67;
    public static final int COMMAND_TO_TRANSPORT_POINT			=	68;
    public static final int COMMAND_ASK_POSITION				=	70;
    public static final int COMMAND_TO_HOME						=	71;
    public static final int COMMAND_JAW_CH						=	72;
    public static final int COMMAND_GRIPPER_ACTION 				=	73;
    public static final int COMMAND_TO_CUSTOM_POS				=	74;
    public static final int COMMAND_WRITE_SERVICE_GRIPPER		=	75;
    public static final int COMMAND_WRITE_SERVICE_HANDLING		=	76;
    public static final int COMMAND_WRITE_SERVICE_POINT			=	77;

    public static final int COMMAND_READ_USERFRAME				=	80;
    public static final int COMMAND_READ_REGISTER               =   82;
    public static final int COMMAND_WRITE_IP_POINT				= 	90;
    public static final int COMMAND_READ_IP_POINT				= 	91;
    public static final int COMMAND_WRITE_REF_POINT				= 	92;
    public static final int COMMAND_READ_REF_POINT				= 	93;
    public static final int COMMAND_WRITE_SPECIAL_POINT			= 	94;
    public static final int COMMAND_READ_SPECIAL_POINT			= 	95;
    public static final int COMMAND_WRITE_TOOLFRAME             =   96;
    public static final int COMMAND_READ_TOOLFRAME              =   97;
    public static final int COMMAND_MOTION_ENABLE               =   98;

    // RESPONSE IDS
    //	public static final int RESPONSE_ASK_STATUS					=	120;
    public static final int RESPONSE_ASK_STATUS					=	122;
    //	public static final int RESPONSE_WRITE_SERVICE_GRIPPER		=	140; (for turn-assist)
    //	public static final int RESPONSE_WRITE_SERVICE_HANDLING		=	141; (for turn-assist)
    //	public static final int RESPONSE_WRITE_SERVICE_POINT		=	142; (for turn-assist)
    public static final int RESPONSE_WRITE_REGISTER				=	143;
    public static final int RESPONSE_WIRTE_POSITION_REGISER		=	144;
    public static final int RESPONSE_WRITE_USERFRAME			=	145;
    public static final int RESPONSE_SET_PERMISSIONS			=	150;
    public static final int RESPONSE_START_SERVICE				=	151;
    public static final int RESPONSE_WRITE_AIRBLOW				= 	154;
    public static final int RESPONSE_RESTART_PROGRAM			=	160;
    public static final int RESPONSE_RESET						=	161;
    public static final int RESPONSE_ABORT						=	162;
    public static final int RESPONSE_CONTINUE					=	163;
    public static final int RESPONSE_RECALC_TCPS				=	164;
    public static final int RESPONSE_SET_SPEED					=	167;
    public static final int RESPONSE_TO_TRANSPORT_POINT			=	168;
    public static final int RESPONSE_ASK_POSITION				=	170;
    public static final int RESPONSE_TO_HOME					=	171;
    public static final int RESPONSE_JAW_CH						=	172;
    public static final int RESPONSE_GRIPPER_ACTION				= 	173;
    public static final int RESPONSE_TO_CUSTOM_POS				=   174;
    public static final int RESPONSE_WRITE_SERVICE_GRIPPER		=	175;
    public static final int RESPONSE_WRITE_SERVICE_HANDLING		=	176;
    public static final int RESPONSE_WRITE_SERVICE_POINT		=	177;

    public static final int RESPONSE_READ_USERFRAME				=	180;
    public static final int RESPONSE_READ_REGISTER              =   182;
    public static final int RESPONSE_WRITE_IP_POINT				= 	190;
    public static final int RESPONSE_READ_IP_POINT				= 	191;
    public static final int RESPONSE_WRITE_REF_POINT			= 	192;
    public static final int RESPONSE_READ_REF_POINT				= 	193;
    public static final int RESPONSE_WRITE_SPECIAL_POINT		= 	194;
    public static final int RESPONSE_READ_SPECIAL_POINT			= 	195;
    public static final int RESPONSE_WRITE_TOOLFRAME            =   196;
    public static final int RESPONSE_READ_TOOLFRAME             =   197;
    public static final int RESPONSE_MOTION_ENABLE              =   198;


    // SERVICE GRIPPER
    public static final int SERVICE_GRIPPER_SERVICE_TYPE_TOOL_CHANGE = 1;
    public static final int SERVICE_GRIPPER_SERVICE_TYPE_PICK = 12;
    public static final int SERVICE_GRIPPER_SERVICE_TYPE_PUT = 13;
    public static final int SERVICE_GRIPPER_SERVICE_TYPE_HOME = 15;
    public static final int SERVICE_GRIPPER_SERVICE_TYPE_JAW_CHANGE = 5;
    public static final int SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT = 14;

    public static final int SERVICE_GRIPPER_SERVICE_GRP_INNER_A = BIT0;
    public static final int SERVICE_GRIPPER_SERVICE_GRP_INNER_B = BIT1;

    // SERVICE HANDLING
    public static final int SERVICE_HANDLING_PP_MODE_BAR_MOVE 		=	BIT0;
    public static final int SERVICE_HANDLING_PP_MODE_BAR_BREAK 		=	BIT1;
    public static final int SERVICE_HANDLING_PP_MODE_NOT_TAKE_WP	=	BIT2;
    public static final int SERVICE_HANDLING_PP_MODE_TC_TEST		=	BIT3;
    public static final int SERVICE_HANDLING_PP_MODE_ORDER_12		=	BIT4;
    public static final int SERVICE_HANDLING_PP_MODE_ORDER_21		=	0;
    public static final int SERVICE_HANDLING_PP_MODE_TEACH 			=	BIT5;
    public static final int SERVICE_HANDLING_PP_MODE_AIRBLOW		=	BIT6;
    public static final int SERVICE_HANDLING_PP_MODE_NO_WAIT		=	BIT7;
    public static final int SERVICE_HANDLING_PP_MODE_PIECE			=	BIT8;

    public static final int SERVICE_HANDLING_PP_MODE_TIM			= 	BIT12;

    // SERVICE POINT
    public static final int SERVICE_POINT_XYZ_ALLOWED_XYZ			=	1;
    public static final int SERVICE_POINT_XYZ_ALLOWED_XY			=	2;
    public static final int SERVICE_POINT_XYZ_ALLOWED_ANGLE			=	3;
    public static final int SERVICE_POINT_XYZ_ALLOWED_Z				= 	5;
    public static final int SERVICE_POINT_XYZ_ALLOWED_XZX			= 	6;

    // COMMAND_PERMISSIONS
    public static final int PERMISSIONS_COMMAND_PICK						=	BIT0;
    public static final int PERMISSIONS_COMMAND_PUT							= 	BIT1;
    public static final int PERMISSIONS_COMMAND_PICK_RELEASE_ACK			=	BIT2;
    public static final int PERMISSIONS_COMMAND_PUT_CLAMP_ACK				=	BIT3;
    public static final int PERMISSIONS_COMMAND_JAWS_CHANGED_ACK			=	BIT4;
    public static final int PERMISSIONS_COMMAND_RUN_AFTER_JAWS_CHANGED_ACK 	=	BIT5;
    public static final int PERMISSIOSN_COMMAND_BAR_MOVE_ACK				=	BIT6;
    public static final int PERMISSIONS_COMMAND_MOVEWAIT					=	BIT7;
    public static final int PERMISSIONS_COMMAND_MOVEWAIT_CONTINUE			=	BIT8;
    public static final int PERMISSIONS_COMMAND_IOACTION					=	BIT9;

    // ERROR VALUES
    public static final int E_NO_ERROR										=	0;
    public static final int E_INVALID_SERVICE_TYPE							=	1;
    public static final int E_INVALID_USERFRAME								=	2;
    public static final int E_INVALID_GRIPTYPE_FOR_SERVICE					=	3;
    public static final int E_INVALID_DATA									=	5;
    public static final int E_NO_PNEUMATIC_PRESSURE							=	10;
    public static final int E_REQUESTED_BODY_NOT_IN_TOOLBAY					=	20;
    public static final int E_REQUESTED_SUBAGRIP_NOT_IN_TOOLBAY				=	21;
    public static final int E_REQUESTED_SUBBGRIP_NOT_IN_TOOLBAY				=	22;
    public static final int E_REQUESTED_BODY_NOT_FORESEEN_IN_TOOLBAY		=	23;
    public static final int E_REQUESTED_SUBAGRIP_NOT_FORESEEN_IN_TOOLBAY	=	24;
    public static final int E_REQUESTED_SUBBGRIP_NOT_FORESEEN_IN_TOOLBAY	=	25;
    public static final int E_DOCKING_BODY_GIVES_ERROR						=	26;
    public static final int E_DOCKING_SUBAGRIP_GIVES_ERROR					=	27;
    public static final int E_DOCKING_SUBBGRIP_GIVES_ERROR					=	28;
    public static final int E_UNDOCKING_BODY_GIVES_ERROR					=	29;
    public static final int E_UNDOCKING_SUBAGRIP_GIVES_ERROR				=	30;
    public static final int E_UNDOCKING_SUBBGRIP_GIVES_ERROR				=	31;
    public static final int E_WORKPIECE_NOT_GRIPPED							=	40;
    public static final int E_ROBOT_NOT_IN_START_POSITION					=	45;
    public static final int E_ROBOT_NOT_IN_IP								= 	46;
    public static final int E_MOTION_ENB                                    =   50;

    // CONTROLLER VALUES
    public static final int CV_FAULT_LED									=	BIT0;
    public static final int CV_CMOS_BATTERY_LOW 							= 	BIT1;
    public static final int CV_SCAN_ERROR                                   =   BIT2;
}
