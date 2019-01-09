package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import cn.greatoo.easymill.cnc.ECNCOption;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.cnc.GenericMCode;
import cn.greatoo.easymill.cnc.MCodeAdapter;
import cn.greatoo.easymill.external.communication.socket.CNCSocketCommunication;
public class DBHandler {


    private final static Logger LOGGER = LogManager.getLogger(DBHandler.class.getName());

    private static DBHandler handler = null;
    private static final String DB_URL = "jdbc:derby:database;create=true;user=irscw;password=password";//jdbc:derby:roboDB;create=true";/
    private static Connection conn = null;
    private static Statement stmt = null;

	

    static {
        createConnection();
        inflateDB();
    }


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
