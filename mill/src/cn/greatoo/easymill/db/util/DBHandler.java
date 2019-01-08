package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.greatoo.easymill.cnc.AbstractCNCMachine;
import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.DeviceSettings;
import cn.greatoo.easymill.cnc.ECNCOption;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.cnc.GenericMCode;
import cn.greatoo.easymill.cnc.MCodeAdapter;
import cn.greatoo.easymill.cnc.SimpleWorkArea;
import cn.greatoo.easymill.cnc.WorkAreaManager;
import cn.greatoo.easymill.cnc.Zone;
import cn.greatoo.easymill.db.GeneralMapper;
import cn.greatoo.easymill.device.AbstractDevice;
import cn.greatoo.easymill.device.AbstractDevice.DeviceType;
import cn.greatoo.easymill.device.ClampingManner;
import cn.greatoo.easymill.device.DeviceManager;
import cn.greatoo.easymill.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import cn.greatoo.easymill.device.stacking.stackplate.BasicStackPlate;
import cn.greatoo.easymill.device.stacking.stackplate.BasicStackPlateLayout;
import cn.greatoo.easymill.entity.UserFrame;
import cn.greatoo.easymill.external.communication.socket.CNCSocketCommunication;
import cn.greatoo.easymill.process.DuplicateProcessFlowNameException;
import cn.greatoo.easymill.process.ProcessFlow;
import cn.greatoo.easymill.process.ProcessFlowManager;
import cn.greatoo.easymill.robot.AbstractRobotActionSettings;
import cn.greatoo.easymill.util.Clamping;
import cn.greatoo.easymill.util.Coordinates;
import cn.greatoo.easymill.workpiece.WorkPiece;

public class DBHandler {


    private final static Logger LOGGER = LogManager.getLogger(DBHandler.class.getName());

    private static DBHandler handler = null;
    private static final String DB_URL = "jdbc:derby:database;create=true;user=admin;password=admin";//jdbc:derby:roboDB;create=true";/
    private static Connection conn = null;
    private static Statement stmt = null;
	private static final int CLAMPING_MANNER_LENGTH = 1;
	private static final int CLAMPING_MANNER_WIDTH = 2;
	private ProcessFlow processFlow;
	private ProcessFlow activeProcessFlow;

	

    static {
        createConnection();
        inflateDB();
    }

	private String saveProcessName;

	private GeneralMapper generalMapper;

	private DeviceManager deviceManager;

    private static ProcessFlowManager processFlowManager;

    private DBHandler() {
    }

    public static DBHandler getInstance() {
        if (handler == null) {
            handler = new DBHandler();
        }
        return handler;
    }
    
    private static void inflateDB() {
        List<String> tableData = new ArrayList<>();
        try {
            Set<String> loadedTables = getDBTables();
            System.out.println("Already loaded tables " + loadedTables);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(DBHandler.class.getClass().getResourceAsStream("/resources/database/tables.xml"));
            NodeList nList = doc.getElementsByTagName("table-entry");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                Element entry = (Element) nNode;
                String tableName = entry.getAttribute("name");
                String query = entry.getAttribute("col-data");
                if (!loadedTables.contains(tableName.toLowerCase())) {
                    tableData.add(String.format("CREATE TABLE %s (%s)", tableName, query));
                }
            }
            if (tableData.isEmpty()) {
                System.out.println("Tables are already loaded");
            }
            else {
                System.out.println("Inflating new tables.");
                createTables(tableData);
            }
        }
        catch (Exception ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
    }

    private static void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "不能连接数据库。请检查程序是否已经启动", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private static Set<String> getDBTables() throws SQLException {
        Set<String> set = new HashSet<>();
        DatabaseMetaData dbmeta = conn.getMetaData();
        readDBTable(set, dbmeta, "TABLE", null);
        return set;
    }

    private static void readDBTable(Set<String> set, DatabaseMetaData dbmeta, String searchCriteria, String schema) throws SQLException {
        ResultSet rs = dbmeta.getTables(null, schema, null, new String[]{searchCriteria});
        while (rs.next()) {
            set.add(rs.getString("TABLE_NAME").toLowerCase());
        }
    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        }
        catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        }
        finally {
        }
        return result;
    }

    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        }
        finally {
        }
    }
    
    /**
     * JY
     * renturn CNCMachine
     * 
     */
    public AbstractCNCMachine getCNCMillingMachine(final int id,CNCSocketCommunication cncSocketConnection){
    	AbstractCNCMachine cncMillingMachine = null;
    	try {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CNCMILLINGMACHINE WHERE ID = ?");
		stmt.setInt(1, id);//1
		ResultSet results = stmt.executeQuery();		
		if (results.next()) {
			int deviceInterfaceId = results.getInt("DEVICEINTERFACE");//1
			int clampingWidthR = results.getInt("CLAMPING_WIDTH_R");//90
			boolean usesNewDevInt = results.getBoolean("NEW_DEV_INT");//true
			int nbFixtures = results.getInt("NB_FIXTURES");//1
			float rRoundPieces = results.getFloat("R_ROUND_PIECES");//90
			EWayOfOperating wayOfOperating = EWayOfOperating.getWayOfOperatingById(results.getInt("WAYOFOPERATING"));//2
			PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM DEVICEINTERFACE WHERE ID = ?");
			stmt2.setInt(1, deviceInterfaceId);
			ResultSet results2 = stmt2.executeQuery();
			if (results2.next()) {
				cncMillingMachine = CNCMachine.getInstance(cncSocketConnection, getMCodeAdapter(id), wayOfOperating);			
				Map<ECNCOption, Boolean> cncOptions = getCNCOptions(id);
				if (cncOptions.get(ECNCOption.TIM_ALLOWED) != null) {
					cncMillingMachine.setTIMAllowed(cncOptions.get(ECNCOption.TIM_ALLOWED));
				}
				if (cncOptions.get(ECNCOption.MACHINE_AIRBLOW) != null) {
					cncMillingMachine.setMachineAirblow(cncOptions.get(ECNCOption.MACHINE_AIRBLOW));
				}
				if (cncOptions.get(ECNCOption.WORKNUMBER_SEARCH) != null) {
				    cncMillingMachine.setWorkNumberSearch(cncOptions.get(ECNCOption.WORKNUMBER_SEARCH));
				}
				if (cncOptions.get(ECNCOption.CLAMPING_PRESSURE_SELECTABLE) != null) {
				    cncMillingMachine.setClampingPressureSelectable(cncOptions.get(ECNCOption.CLAMPING_PRESSURE_SELECTABLE));
				}
				cncMillingMachine.setId(id);
			}
		}
    	}catch (SQLException ex) {
	          LOGGER.log(Level.ERROR, "{}", ex);
	      }
		return cncMillingMachine;
	}
    
    public int getZoneNr(int id) {
    	int zonenr = 0;
    	try {
    		PreparedStatement stmt = conn.prepareStatement("SELECT ZONE_NR FROM ZONE WHERE ID = ?");
    		stmt.setInt(1, id);
    		ResultSet results = stmt.executeQuery();
    		if (results.next()) {
    			zonenr = results.getInt("ZONE_NR");
    		}
    	}catch (SQLException ex) {
	          LOGGER.log(Level.ERROR, "{}", ex);
	      }
		return zonenr;
    }
    
    public int getWorkArea(int id) {
    	int zonenr = 0;
    	try {
    		PreparedStatement stmt = conn.prepareStatement("SELECT NUMBER FROM USERFRAME WHERE ID = ?");
    		stmt.setInt(1, id);
    		ResultSet results = stmt.executeQuery();
    		if (results.next()) {
    			zonenr = results.getInt("NUMBER");
    		}
    	}catch (SQLException ex) {
	          LOGGER.log(Level.ERROR, "{}", ex);
	      }
		return zonenr;
    }
    
    private Map<ECNCOption, Boolean> getCNCOptions(final int id){    	
		Map<ECNCOption, Boolean> resultMap = new HashMap<ECNCOption, Boolean>();
		try {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CNC_OPTION WHERE CNC_ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			ECNCOption option = ECNCOption.getCNCOptionById(results.getInt("OPTION_ID"));
			boolean value = results.getBoolean("OPTION_VALUE");
			resultMap.put(option, value);
		}
		}catch (SQLException ex) {
	          LOGGER.log(Level.ERROR, "{}", ex);
	      }
		return resultMap;
	}
    
    /**
     * JY
     * renturn MCodeAdapter
     * RS1
     */
    public MCodeAdapter getMCodeAdapter(final int cncMachineId){
    	MCodeAdapter mCodeAdapter = null;
    	try {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MCODEADAPTER WHERE ID = ?");
		stmt.setInt(1, cncMachineId);//1
		ResultSet results = stmt.executeQuery();		
		if (results.next()) {
			String robotServiceInput1Name = results.getString("ROBOTSERVICEINPUT1");//RS1
			String robotServiceInput2Name = results.getString("ROBOTSERVICEINPUT2");//RS2
			String robotServiceInput3Name = results.getString("ROBOTSERVICEINPUT3");//RS3
			String robotServiceInput4Name = results.getString("ROBOTSERVICEINPUT4");//RS4
			String robotServiceInput5Name = results.getString("ROBOTSERVICEINPUT5");//RS5
			String robotServiceOutput1Name = results.getString("ROBOTSERVICEOUTPUT1");//RSA
			List<String> robotServiceInputNames = new ArrayList<String>();
			robotServiceInputNames.add(robotServiceInput1Name);
			robotServiceInputNames.add(robotServiceInput2Name);
			robotServiceInputNames.add(robotServiceInput3Name);
			robotServiceInputNames.add(robotServiceInput4Name);
			robotServiceInputNames.add(robotServiceInput5Name);
			List<String> robotServiceOutputNames = new ArrayList<String>();
			robotServiceOutputNames.add(robotServiceOutput1Name);
			mCodeAdapter = new MCodeAdapter(getMCodes(cncMachineId), robotServiceInputNames, robotServiceOutputNames);
		}
    	}catch (SQLException ex) {
          LOGGER.log(Level.ERROR, "{}", ex);
      }
		return mCodeAdapter;
	}
    public List<GenericMCode> getMCodes(final int cncMachineId) throws SQLException{
    	List<GenericMCode> mCodes = new ArrayList<GenericMCode>();
    	try {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MCODE WHERE MCODEADAPTER = ?");
		stmt.setInt(1, cncMachineId);//1
		ResultSet results = stmt.executeQuery();		
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			boolean usesRobotServiceInput1 = results.getBoolean("ROBOTSERVICEINPUT1");
			boolean usesRobotServiceInput2 = results.getBoolean("ROBOTSERVICEINPUT2");
			boolean usesRobotServiceInput3 = results.getBoolean("ROBOTSERVICEINPUT3");
			boolean usesRobotServiceInput4 = results.getBoolean("ROBOTSERVICEINPUT4");
			boolean usesRobotServiceInput5 = results.getBoolean("ROBOTSERVICEINPUT5");
			Set<Integer> robotServiceInputsUsed = new HashSet<Integer>();
			if (usesRobotServiceInput1) {
				robotServiceInputsUsed.add(0);
			}
			if (usesRobotServiceInput2) {
				robotServiceInputsUsed.add(1);
			}
			if (usesRobotServiceInput3) {
				robotServiceInputsUsed.add(2);
			}
			if (usesRobotServiceInput4) {
				robotServiceInputsUsed.add(3);
			}
			if (usesRobotServiceInput5) {
				robotServiceInputsUsed.add(4);
			}
			boolean usesRobotServiceOutput1 = results.getBoolean("ROBOTSERVICEOUTPUT1");
			Set<Integer> robotServiceOutputsUsed = new HashSet<Integer>();
			if (usesRobotServiceOutput1) {
				robotServiceOutputsUsed.add(0);
			}
			int index = results.getInt("INDEX");
			GenericMCode mcode = new GenericMCode(id, index, name, robotServiceInputsUsed, robotServiceOutputsUsed);
			mCodes.add(index, mcode);
		}
    	}catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
		return mCodes;
	}

    private static void createTables(List<String> tableData) throws SQLException {
        Statement statement = conn.createStatement();
        statement.closeOnCompletion();
        for (String command : tableData) {
            System.out.println(command);
            statement.addBatch(command);
        }
        statement.executeBatch();
    }

    public Connection getConnection() {
    	if (conn == null) {
    		createConnection();
		}
		return conn;
    }
    
	public ProcessFlow getLastProcessFlow() {
		List<ProcessFlow> processFlows;
		try {
			processFlows = getLastOpenedProcessFlows(1);
			if (processFlows.size() > 0) {
				return processFlows.get(0);
			}
		} catch (SQLException e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	public List<ProcessFlow> getAllProcessFlows() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT ID FROM PROCESSFLOW ORDER BY LASTOPENED DESC");
		ResultSet results = stmt.executeQuery();
		List<ProcessFlow> processFlows = new ArrayList<ProcessFlow>();
		while (results.next()) {
			int id = results.getInt("ID");
			ProcessFlow processFlow = getLightWeightedProcessFlowById(id);
			processFlows.add(processFlow);
		}
		return processFlows;
	}
	
	private ProcessFlow getLightWeightedProcessFlowById(final int id) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT ID, NAME, LASTOPENED FROM PROCESSFLOW WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		ProcessFlow processFlow = null;
		if (results.next()) {

			String name = results.getString("NAME");
			Timestamp lastOpened = results.getTimestamp("LASTOPENED");
			processFlow = new ProcessFlow(name,null,null);
			processFlow.setId(id);
			processFlow.setLastOpened(lastOpened);
		}
		
		return processFlow;
	}
	
	public List<ProcessFlow> getLastOpenedProcessFlows(final int amount) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT ID FROM PROCESSFLOW ORDER BY LASTOPENED DESC");
		stmt.setMaxRows(amount);
		ResultSet results = stmt.executeQuery();
		List<ProcessFlow> processFlows = new ArrayList<ProcessFlow>();
		while (results.next()) {
			int id = results.getInt("ID");
			ProcessFlow processFlow = getProcessFlowById(id);
			processFlows.add(processFlow);
		}
		return processFlows;
	}
	
	public void updateLastOpened(final ProcessFlow processFlow) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("UPDATE PROCESSFLOW SET LASTOPENED = ? WHERE ID = ?");
		stmt.setTimestamp(1, processFlow.getLastOpened());
		stmt.setInt(2, processFlow.getId());
		stmt.executeUpdate();
	}
	
	public void updateProcessFlow(final ProcessFlow processFlow) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("UPDATE PROCESSFLOW SET NAME = ?, LASTOPENED = ?, CLAMPING_MANNER = ? , SINGLE_CYCLE = ? WHERE ID = ?");
		stmt.setString(1, processFlow.getName());
		stmt.setTimestamp(2, processFlow.getLastOpened());
		int clampingMannerId = CLAMPING_MANNER_LENGTH;
		if (processFlow.getClampingType().getType() == ClampingManner.Type.WIDTH) {
			clampingMannerId = CLAMPING_MANNER_WIDTH;
		}
		stmt.setInt(3, clampingMannerId);
		stmt.setBoolean(4, processFlow.isSingleCycle());
		stmt.setInt(5, processFlow.getId());
		try {
			stmt.executeUpdate();
			deleteStepsAndSettings(processFlow);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.error(e);
			conn.rollback();
		}

	}
		
	private void deleteStepsAndSettings(final ProcessFlow processFlow) throws SQLException {
		// delete all coordinates and work pieces (these are not cascaded)
		// delete all coordinates
		PreparedStatement stmtgetCoordinatesToDelete = conn.prepareStatement(""  
                + "		(select step_teachedcoordinates.coordinates from step_teachedcoordinates "
                + "			JOIN step "
                + "			ON step.id = step_teachedcoordinates.step "
                + "			where step.processflow=?"
                + "		) " 	
                + " 		union "																						
                + "		(select robotactionsettings.smoothpoint from robotactionsettings "
                + "			JOIN step "
                + "			ON step.id = robotactionsettings.step "
                + "			where step.processflow=?"
                + "		) "
				);	
		stmtgetCoordinatesToDelete.setInt(1, processFlow.getId());
		stmtgetCoordinatesToDelete.setInt(2, processFlow.getId());
		ResultSet resultCoordinates = stmtgetCoordinatesToDelete.executeQuery();
		while(resultCoordinates.next()) {
			deleteCoordinate(resultCoordinates.getInt(1));
		}
		// delete all work pieces (it suffices to delete the work pieces from the pick setting
		PreparedStatement stmtGetWorkPiecesToDelete = conn.prepareStatement("" 	
				+ "select distinct workpiece from robotpicksettings "
				+ "join robotactionsettings "
				+ "on robotactionsettings.id = robotpicksettings.id "
				+ "join step "
				+ "on robotactionsettings.step = step.id "
				+ "where step.processflow=?"
				+ "");
		stmtGetWorkPiecesToDelete.setInt(1, processFlow.getId());
		ResultSet resultset =stmtGetWorkPiecesToDelete.executeQuery();
		while(resultset.next()) {
			deleteWorkPiece(resultset.getInt("workpiece"));
		}
		
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM DEVICESETTINGS WHERE PROCESSFLOW = ?");
		stmt.setInt(1, processFlow.getId());
		stmt.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
		PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM ROBOTSETTINGS WHERE PROCESSFLOW = ?");
		stmt2.setInt(1, processFlow.getId());
		stmt2.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
		PreparedStatement stmt3 = conn.prepareStatement("DELETE FROM STEP WHERE PROCESSFLOW = ?");
		stmt3.setInt(1, processFlow.getId());
		stmt3.executeUpdate();	// note the cascade delete settings take care of deleting all referenced rows
	}
 
	public void saveProcessFlow(final ProcessFlow processFlow) throws DuplicateProcessFlowNameException, SQLException {	
		processFlow.setCreation(new Timestamp(System.currentTimeMillis()));
		processFlow.setLastOpened(new Timestamp(System.currentTimeMillis()));
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO PROCESSFLOW (NAME, CREATION, LASTOPENED, CLAMPING_MANNER, SINGLE_CYCLE) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, processFlow.getName());
		stmt.setTimestamp(2, processFlow.getCreation());
		stmt.setTimestamp(3, processFlow.getLastOpened());
		int clampingMannerId = 1;
		if (processFlow.getClampingType().getType() == ClampingManner.Type.WIDTH) {
			clampingMannerId = 2;
		}
		stmt.setInt(4, clampingMannerId);
		stmt.setBoolean(5, processFlow.isSingleCycle());
		try {
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {

				processFlow.setId(resultSet.getInt(1));
				conn.commit();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			LOGGER.log(Level.ERROR, "{}", e);
			conn.rollback();
		}
}
	public int getProcessFlowIdForName(final String name) throws SQLException {
		int id = -1;
		PreparedStatement stmt =conn.prepareStatement("SELECT ID FROM PROCESSFLOW WHERE NAME = ?");
		stmt.setString(1, name);
		ResultSet resultSet = stmt.executeQuery();
		if (resultSet.next()) {
			id = resultSet.getInt("ID");
		}
		return id;
	}
	
	public Map<AbstractDevice, DeviceSettings> getDeviceSettings(final int processId) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM DEVICESETTINGS WHERE PROCESSFLOW = ?");
		stmt.setInt(1, processId);
		ResultSet results = stmt.executeQuery();
		Map<AbstractDevice, DeviceSettings> settings = new HashMap<AbstractDevice, DeviceSettings>();
		while (results.next()) {
			int deviceId = results.getInt("DEVICE");
			int id = results.getInt("ID");
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			Map<SimpleWorkArea, Clamping> clampings = getDefaultClampingPerWorkArea(id, device);
			if (device instanceof BasicStackPlate) {
				AbstractStackPlateDeviceSettings stackPlateSettings = getBasicStackPlateSettings(processId, id, (BasicStackPlate) device, clampings);
				settings.put(device, stackPlateSettings);
			}  else {
				DeviceSettings deviceSettings = new DeviceSettings(clampings);
				deviceSettings.setId(id);
				settings.put(device, deviceSettings);
			}
		}
		return settings;
	}
	
	private AbstractStackPlateDeviceSettings getBasicStackPlateSettings(final int processFlowId, final int deviceSettingsId, final BasicStackPlate stackPlate, final Map<SimpleWorkArea, Clamping> clampings) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM STACKPLATESETTINGS WHERE ID = ?");
		stmt.setInt(1, deviceSettingsId);
		ResultSet results = stmt.executeQuery();
		AbstractStackPlateDeviceSettings basicStackPlateSettings = null;
		if (results.next()) {
			int amount = results.getInt("AMOUNT");
			float orientation = results.getInt("ORIENTATION");
			int rawWorkPieceId = results.getInt("RAWWORKPIECE");
			int finishedWorkPieceId = results.getInt("FINISHEDWORKPIECE");
			int layers = results.getInt("LAYERS");
			float studHeight = results.getFloat("STUDHEIGHT");
			int gridId = results.getInt("GRID_ID");
			WorkPiece rawWorkPiece = generalMapper.getWorkPieceById(processFlowId, rawWorkPieceId);
			WorkPiece finishedWorkPiece = generalMapper.getWorkPieceById(processFlowId, finishedWorkPieceId);
			basicStackPlateSettings = new AbstractStackPlateDeviceSettings(rawWorkPiece, finishedWorkPiece, orientation, layers, amount, studHeight, gridId);
			basicStackPlateSettings.setClampings(clampings);
		}
		return basicStackPlateSettings;
	}
	
	private Map<SimpleWorkArea, Clamping> getDefaultClampingPerWorkArea(final int deviceSettingsId, final AbstractDevice device) throws SQLException {
		Map<SimpleWorkArea, Clamping> defaultClampings = new HashMap<SimpleWorkArea, Clamping>();
		PreparedStatement stmt = conn.prepareStatement("SELECT CLAMPING, DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA "           			  +
								"FROM DEVICESETTINGS_WORKAREA_CLAMPING "                     					  +
							    "JOIN WORKAREA_CLAMPING "                                                         +
							      "ON DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA_CLAMPING = WORKAREA_CLAMPING.ID " +
							   "WHERE DEVICESETTINGS_WORKAREA_CLAMPING.DEVICESETTINGS = ? "  					  +
				     			 "AND DEVICESETTINGS_WORKAREA_CLAMPING.DEFAULT_FL = ?");
		stmt.setInt(1, deviceSettingsId);
		stmt.setBoolean(2, true);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			int workAreaId = results.getInt("WORKAREA");
			int clampingId = results.getInt("CLAMPING");
			SimpleWorkArea workArea = device.getWorkAreaById(workAreaId);
			try {
				Clamping clamping = workArea.getWorkAreaManager().getClampingById(clampingId).clone();
				defaultClampings.put(workArea, clamping);
				addRelatedClampings(deviceSettingsId, clamping, workArea);
				// Do not set default because this is a cloned clamping, which is thus not the same. 
				//workArea.setDefaultClamping(clamping);
			} catch (CloneNotSupportedException e) {
				LOGGER.error(e);
			}
		}
		return defaultClampings;
	}
	
	private void addRelatedClampings(final int deviceSettingsId, final Clamping clamping, final SimpleWorkArea workArea) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT CLAMPING "													              +
									"FROM DEVICESETTINGS_WORKAREA_CLAMPING "                     					  +
								    "JOIN WORKAREA_CLAMPING "                                                         +
								      "ON DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA_CLAMPING = WORKAREA_CLAMPING.ID " +
								   "WHERE DEVICESETTINGS_WORKAREA_CLAMPING.DEVICESETTINGS = ? "  					  +
								     "AND DEVICESETTINGS_WORKAREA_CLAMPING.WORKAREA = ? " 		  					  +
					     			 "AND DEVICESETTINGS_WORKAREA_CLAMPING.DEFAULT_FL = ?");
			stmt.setInt(1, deviceSettingsId);
			stmt.setInt(2, workArea.getId());
			stmt.setBoolean(3, false);
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				try {
					int clampingId = results.getInt("CLAMPING");
					Clamping relClamping = workArea.getWorkAreaManager().getClampingById(clampingId).clone();
					clamping.addRelatedClamping(relClamping);
				} catch (CloneNotSupportedException e) {
					LOGGER.error(e);
				}
			}
	}
	
	
	public Set<AbstractDevice> getAllDevices() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT * FROM DEVICE");
		HashSet<AbstractDevice> devices = new HashSet<AbstractDevice>();
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			DeviceType type = DeviceType.getTypeById(results.getInt("TYPE"));
			Set<Zone> zones = getAllZonesByDeviceId(id);
			switch (type) {
				case DEVICE_TYPE_CNCMILLING:
					AbstractCNCMachine cncMillingMachine = getCNCMillingMachine(id, name, zones);
					devices.add(cncMillingMachine);
					break;
				case DEVICE_TYPE_STACKPLATE:
					BasicStackPlate basicStackPlate = getBasicStackPlate(id, name, zones);
					devices.add(basicStackPlate);
					break;
				default:
					throw new IllegalStateException("Unknown device type: [" + type + "].");
			}
		}
		return devices;
	}
	
	private AbstractCNCMachine getCNCMillingMachine(final int id, final String name, final Set<Zone> zones) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CNCMILLINGMACHINE WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		AbstractCNCMachine cncMillingMachine = null;
		if (results.next()) {
			int deviceInterfaceId = results.getInt("DEVICEINTERFACE");
			int clampingWidthR = results.getInt("CLAMPING_WIDTH_R");
			boolean usesNewDevInt = results.getBoolean("NEW_DEV_INT");
			int nbFixtures = results.getInt("NB_FIXTURES");
			float rRoundPieces = results.getFloat("R_ROUND_PIECES");
			EWayOfOperating wayOfOperating = EWayOfOperating.getWayOfOperatingById(results.getInt("WAYOFOPERATING"));
			PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM DEVICEINTERFACE WHERE ID = ?");
			stmt2.setInt(1, deviceInterfaceId);
			ResultSet results2 = stmt2.executeQuery();
			if (results2.next()) {
//				int socketConnectionId = results2.getInt("SOCKETCONNECTION");
//				SocketConnection socketConnection = connectionMapper.getSocketConnectionById(socketConnectionId);
//				if(usesNewDevInt) {
//					cncMillingMachine = new CNCMillingMachineDevIntv2(name, wayOfOperating, getMCodeAdapter(id), zones, socketConnection, clampingWidthR, nbFixtures, rRoundPieces);
//				} else {
//					cncMillingMachine = new CNCMillingMachine(name, wayOfOperating, getMCodeAdapter(id), zones, socketConnection, clampingWidthR, nbFixtures, rRoundPieces);
//				}
				Map<ECNCOption, Boolean> cncOptions = getCNCOptions(id);
				if (cncOptions.get(ECNCOption.TIM_ALLOWED) != null) {
					cncMillingMachine.setTIMAllowed(cncOptions.get(ECNCOption.TIM_ALLOWED));
				}
				if (cncOptions.get(ECNCOption.MACHINE_AIRBLOW) != null) {
					cncMillingMachine.setMachineAirblow(cncOptions.get(ECNCOption.MACHINE_AIRBLOW));
				}
				if (cncOptions.get(ECNCOption.WORKNUMBER_SEARCH) != null) {
				    cncMillingMachine.setWorkNumberSearch(cncOptions.get(ECNCOption.WORKNUMBER_SEARCH));
				}
				if (cncOptions.get(ECNCOption.CLAMPING_PRESSURE_SELECTABLE) != null) {
				    cncMillingMachine.setClampingPressureSelectable(cncOptions.get(ECNCOption.CLAMPING_PRESSURE_SELECTABLE));
				}
				cncMillingMachine.setId(id);
			}
		}
		return cncMillingMachine;
	}
	
	
	private BasicStackPlate getBasicStackPlate(final int id, final String name, final Set<Zone> zones) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM STACKPLATE WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		BasicStackPlate stackPlate = null;
		if (results.next()) {
			int horizontalHoleAmount = results.getInt("HORIZONTALHOLEAMOUNT");
			int verticalHoleAmount = results.getInt("VERTICALHOLEAMOUNT");
			float holeDiameter = results.getFloat("HOLEDIAMETER");
			float studDiameter = results.getFloat("STUDDIAMETER");
			float horizontalPadding = results.getFloat("HORIZONTALPADDING");
			float verticalPaddingTop = results.getFloat("VERTICALPADDINGTOP");
			float verticalPaddingBottom = results.getFloat("VERTICALPADDINGBOTTOM");
			float horizontalHoleDistance = results.getFloat("HORIZONTALHOLEDISTANCE");
			float verticalHoleDistance = results.getFloat("VERTICALHOLEDISTANCE");
			float interferenceDistance = results.getFloat("INTERFERENCEDISTANCE");
			float overflowPercentage = results.getFloat("OVERFLOWPERCENTAGE");
			float horizontalR = results.getFloat("HORIZONTAL_R");
			float tiltedR = results.getFloat("TILTED_R");
			double maxOverflow = results.getDouble("MAX_OVERFLOW");
			double maxUnderflow = results.getDouble("MAX_UNDERFLOW");
			double minOverlap = results.getDouble("MIN_OVERLAP");
			BasicStackPlateLayout layout = new BasicStackPlateLayout(horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPaddingTop, 
					verticalPaddingBottom, horizontalHoleDistance, verticalHoleDistance, interferenceDistance, overflowPercentage, horizontalR, tiltedR, maxOverflow, maxUnderflow, minOverlap);
			stackPlate = new BasicStackPlate(name, zones, layout);
			stackPlate.setId(id);
		}
		return stackPlate;
	}
	
	private Set<Zone> getAllZonesByDeviceId(final int deviceId) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ZONE WHERE DEVICE = ?");
		stmt.setInt(1, deviceId);
		ResultSet results = stmt.executeQuery();
		Set<Zone> zones = new HashSet<Zone>();
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			int zoneNr = results.getInt("ZONE_NR");
			Set<WorkAreaManager> workAreas = getAllWorkAreasByZoneId(id);
			Zone zone;
			zone = new Zone(name, workAreas, zoneNr);
			zone.setId(id);
			zones.add(zone);
		}
		return zones;
	}
	
	private Set<WorkAreaManager> getAllWorkAreasByZoneId(final int zoneId) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(""
				+ "SELECT * FROM WORKAREA "
				+ "WHERE ZONE = ?"
				+ "ORDER BY USERFRAME");
		stmt.setInt(1, zoneId);
		ResultSet results = stmt.executeQuery();
		Set<WorkAreaManager> workAreas = new HashSet<WorkAreaManager>();
		while (results.next()) {
			int id = results.getInt("ID");
			int userFrameId = results.getInt("USERFRAME");
			UserFrame userFrame = generalMapper.getUserFrameById(userFrameId);
			Set<Clamping> possibleClampings = getClampingsByWorkAreaId(id);
			WorkAreaManager workArea = new WorkAreaManager(userFrame, possibleClampings);
			workArea.setId(id);
			//AirblowSquare boundaries = getWorkAreaBoundaries(id);
//			if (boundaries != null) {
//				workArea.setWorkAreaBoundary(new WorkAreaBoundary(workArea, boundaries));
//			}
			getAllSimpleWorkAreasByManagerId(workArea);
			workAreas.add(workArea);
		}
		return workAreas;
	}
	
	private void getAllSimpleWorkAreasByManagerId(final WorkAreaManager waManager) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(""
				+ "SELECT * FROM SIMPLE_WORKAREA "
				+ "WHERE WORKAREA = ?"
				+ "ORDER BY SEQ_NB");
		stmt.setInt(1, waManager.getId());
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			int id = results.getInt("ID");
			String name = results.getString("NAME");
			int sequenceNb = results.getInt("SEQ_NB");
			SimpleWorkArea workArea = new SimpleWorkArea(waManager, name, sequenceNb);
			workArea.setId(id);
		}
	}

	
	private Set<Clamping> getClampingsByWorkAreaId(final int workAreaId) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM WORKAREA_CLAMPING WHERE WORKAREA = ? ORDER BY ID");
		stmt.setInt(1, workAreaId);
		ResultSet results = stmt.executeQuery();
		Set<Clamping> clampings = new HashSet<Clamping>();
		while (results.next()) {
			int clampingId = results.getInt("CLAMPING");
			Clamping clamping = getClampingById(clampingId);
			clampings.add(clamping);
		}
		return clampings;
	}
	
	private Clamping getClampingById(final int clampingId) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CLAMPING WHERE ID = ?");
		stmt.setInt(1, clampingId);
		ResultSet results = stmt.executeQuery();
		Clamping clamping = null;
		if (results.next()) {
			int type = results.getInt("TYPE");
			int relativePositionId = results.getInt("RELATIVE_POSITION");
			Coordinates relativePosition = generalMapper.getCoordinatesById(0, relativePositionId);
			int smoothToId = results.getInt("SMOOTH_TO");
			Coordinates smoothTo = generalMapper.getCoordinatesById(0, smoothToId);
			int smoothFromId = results.getInt("SMOOTH_FROM");
			Coordinates smoothFrom = generalMapper.getCoordinatesById(0, smoothFromId);
			float height = results.getFloat("HEIGHT");
			String imageUrl = results.getString("IMAGE_URL");
			String name = results.getString("NAME");
			int fixtureTypeInt = results.getInt("FIXTURE_TYPE");
//			EFixtureType fixtureType = EFixtureType.getFixtureTypeFromCodeValue(fixtureTypeInt);
//			switch(type) {
//				case CLAMPING_TYPE_CENTRUM:
//					clamping = new Clamping(Clamping.Type.CENTRUM, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
//					break;
//				case CLAMPING_TYPE_FIXED_XP:
//					clamping = new Clamping(Clamping.Type.FIXED_XP, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
//					break;
//				case CLAMPING_TYPE_NONE:
//					clamping = new Clamping(Clamping.Type.NONE, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
//					break;
//				case CLAMPING_TYPE_FIXED_XM:
//					clamping = new Clamping(Clamping.Type.FIXED_XM, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
//					break;
//				case CLAMPING_TYPE_FIXED_YP:
//					clamping = new Clamping(Clamping.Type.FIXED_YP, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
//					break;
//				case CLAMPING_TYPE_FIXED_YM:
//					clamping = new Clamping(Clamping.Type.FIXED_YM, name, height, relativePosition, smoothTo, smoothFrom, imageUrl, fixtureType);
//					break;
//				default:
//					throw new IllegalStateException("Unknown clamping type: [" + type + "].");
//			}
//			Integer bottomCoordAirblow = new Integer(results.getInt("AIRBLOW_BOTTOM"));
//			Integer topCoordAirblow = new Integer(results.getInt("AIRBLOW_TOP"));
//			if (bottomCoordAirblow != null && topCoordAirblow != null) {
//				clamping.setDefaultAirblowPoints(new AirblowSquare(generalMapper.getCoordinatesById(0, bottomCoordAirblow), generalMapper.getCoordinatesById(0, topCoordAirblow)));
//			}
			clamping.setId(clampingId);
		}
		return clamping;
	}
	
	public ProcessFlow getProcessFlowById(final int id) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM PROCESSFLOW WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		ProcessFlow processFlow = null;
		if (results.next()) {
			String name = results.getString("NAME");
			Timestamp creation = results.getTimestamp("CREATION");
			Timestamp lastOpened = results.getTimestamp("LASTOPENED");
			int clampingMannerId = results.getInt("CLAMPING_MANNER");
			boolean isSingleCycle = results.getBoolean("SINGLE_CYCLE");

			// We have 1 deviceSetting per device - per processFlow

			processFlow = new ProcessFlow(name, creation, lastOpened);
			processFlow.setId(id);
			processFlow.setSingleCycle(isSingleCycle);
			if (clampingMannerId == CLAMPING_MANNER_LENGTH) {
				processFlow.getClampingType().setType(ClampingManner.Type.LENGTH);
			} else if (clampingMannerId == CLAMPING_MANNER_WIDTH) {
				processFlow.getClampingType().setType(ClampingManner.Type.WIDTH);
			} else {
				throw new IllegalStateException("Unknown clamping manner type: " + clampingMannerId);
			}
		}
		return processFlow;
	}
	
    public void saveCoordinates(final Coordinates coordinates) throws SQLException {
        if (coordinates.getId() <=0) {
            PreparedStatement stmt =  conn.prepareStatement("INSERT INTO COORDINATES (X, Y, Z, W, P, R) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setFloat(1, coordinates.getX());
            stmt.setFloat(2, coordinates.getY());
            stmt.setFloat(3, coordinates.getZ());
            stmt.setFloat(4, coordinates.getW());
            stmt.setFloat(5, coordinates.getP());
            stmt.setFloat(6, coordinates.getR());
    		try {
    			stmt.executeUpdate();
    			conn.commit();

    		} catch (SQLException e) {
    			e.printStackTrace();
    			LOGGER.log(Level.ERROR, "{}", e);
    			conn.rollback();
    		}
            ResultSet keys = stmt.getGeneratedKeys();
            if ((keys != null) && (keys.next())) {
                coordinates.setId(keys.getInt(1));
            }
        } else {
            PreparedStatement stmt =  conn.prepareStatement("UPDATE COORDINATES SET X = ?, Y = ?, Z = ?, W = ?, P = ?, R = ? WHERE ID = ?");
            stmt.setFloat(1, coordinates.getX());
            stmt.setFloat(2, coordinates.getY());
            stmt.setFloat(3, coordinates.getZ());
            stmt.setFloat(4, coordinates.getW());
            stmt.setFloat(5, coordinates.getP());
            stmt.setFloat(6, coordinates.getR());
            stmt.setInt(7, coordinates.getId());
    		try {
    			stmt.executeUpdate();
    			conn.commit();

    		} catch (SQLException e) {
    			e.printStackTrace();
    			LOGGER.log(Level.ERROR, "{}", e);
    			conn.rollback();
    		}
        }
    }
 
	public void saveRobotActionSettings(AbstractRobotActionSettings AbstractRobotActionSettings) throws SQLException {					
		saveCoordinates(AbstractRobotActionSettings.getSmoothPoint());
		
		PreparedStatement stmt =  conn.prepareStatement("INSERT INTO ROBOTACTIONSETTINGS (STEP, GRIPPERHEAD, SMOOTHPOINT, ROBOT, GRIPINNER) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, (AbstractRobotActionSettings.getId()));
		stmt.setInt(2, AbstractRobotActionSettings.getGripperHead().getId());
		if ((AbstractRobotActionSettings.getSmoothPoint() != null)) {
			stmt.setInt(3, (AbstractRobotActionSettings.getSmoothPoint().getId()));
		} else {
			stmt.setNull(3, java.sql.Types.INTEGER);
		}
		stmt.setInt(4, (AbstractRobotActionSettings.getRobot().getId()));
		stmt.setBoolean(5, AbstractRobotActionSettings.isGripInner());
		stmt.executeUpdate();
		ResultSet keys = stmt.getGeneratedKeys();
		if ((keys != null) && (keys.next())) {
			AbstractRobotActionSettings.setId(keys.getInt(1));
		}
	}
	
	private void deleteWorkPiece(Integer workPieceId) throws SQLException {
		PreparedStatement stmtDeleteCoordinates = conn.prepareStatement("delete from workpiece where id=?");
		stmtDeleteCoordinates.setInt(1, workPieceId);
		stmtDeleteCoordinates.executeUpdate();
	}
	
	private void deleteCoordinate(Integer coordinateId) throws SQLException {
		PreparedStatement stmtDeleteCoordinates = conn.prepareStatement("delete from coordinates where id=?");
		stmtDeleteCoordinates.setInt(1, coordinateId);
		stmtDeleteCoordinates.executeUpdate();
	}
    

    
}
