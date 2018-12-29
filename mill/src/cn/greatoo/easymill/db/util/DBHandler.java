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
import cn.greatoo.easymill.cnc.ECNCOption;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.cnc.GenericMCode;
import cn.greatoo.easymill.cnc.MCodeAdapter;
import cn.greatoo.easymill.external.communication.socket.CNCSocketCommunication;
import cn.greatoo.easymill.process.DuplicateProcessFlowNameException;
import cn.greatoo.easymill.process.ProcessFlow;
import cn.greatoo.easymill.robot.AbstractRobotActionSettings;
import cn.greatoo.easymill.util.Coordinates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import cn.greatoo.easymill.device.ClampingManner;
import cn.greatoo.easymill.process.ProcessFlowManager;
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
//    public boolean deleteBook(Book book) {
//        try {
//            String deleteStatement = "DELETE FROM BOOK WHERE ID = ?";
//            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
//            stmt.setString(1, book.getId());
//            int res = stmt.executeUpdate();
//            if (res == 1) {
//                return true;
//            }
//        }
//        catch (SQLException ex) {
//            LOGGER.log(Level.ERROR, "{}", ex);
//        }
//        return false;
//    }
//
//    public boolean isBookAlreadyIssued(Book book) {
//        try {
//            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE bookid=?";
//            PreparedStatement stmt = conn.prepareStatement(checkstmt);
//            stmt.setString(1, book.getId());
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                int count = rs.getInt(1);
//                System.out.println(count);
//                return (count > 0);
//            }
//        }
//        catch (SQLException ex) {
//            LOGGER.log(Level.ERROR, "{}", ex);
//        }
//        return false;
//    }
//
//    public boolean deleteMember(MemberListController.Member member) {
//        try {
//            String deleteStatement = "DELETE FROM MEMBER WHERE id = ?";
//            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
//            stmt.setString(1, member.getId());
//            int res = stmt.executeUpdate();
//            if (res == 1) {
//                return true;
//            }
//        }
//        catch (SQLException ex) {
//            LOGGER.log(Level.ERROR, "{}", ex);
//        }
//        return false;
//    }
//
//    public boolean isMemberHasAnyBooks(MemberListController.Member member) {
//        try {
//            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE memberID=?";
//            PreparedStatement stmt = conn.prepareStatement(checkstmt);
//            stmt.setString(1, member.getId());
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                int count = rs.getInt(1);
//                System.out.println(count);
//                return (count > 0);
//            }
//        }
//        catch (SQLException ex) {
//            LOGGER.log(Level.ERROR, "{}", ex);
//        }
//        return false;
//    }
//
//    public boolean updateBook(Book book) {
//        try {
//            String update = "UPDATE BOOK SET TITLE=?, AUTHOR=?, PUBLISHER=? WHERE ID=?";
//            PreparedStatement stmt = conn.prepareStatement(update);
//            stmt.setString(1, book.getTitle());
//            stmt.setString(2, book.getAuthor());
//            stmt.setString(3, book.getPublisher());
//            stmt.setString(4, book.getId());
//            int res = stmt.executeUpdate();
//            return (res > 0);
//        }
//        catch (SQLException ex) {
//            LOGGER.log(Level.ERROR, "{}", ex);
//        }
//        return false;
//    }
//
//    public boolean updateMember(MemberListController.Member member) {
//        try {
//            String update = "UPDATE MEMBER SET NAME=?, EMAIL=?, MOBILE=? WHERE ID=?";
//            PreparedStatement stmt = conn.prepareStatement(update);
//            stmt.setString(1, member.getName());
//            stmt.setString(2, member.getEmail());
//            stmt.setString(3, member.getMobile());
//            stmt.setString(4, member.getId());
//            int res = stmt.executeUpdate();
//            return (res > 0);
//        }
//        catch (SQLException ex) {
//            LOGGER.log(Level.ERROR, "{}", ex);
//        }
//        return false;
//    }

//  public static void main(String[] args) throws Exception {
//   DBHandler.getInstance();
//   }

    public ObservableList<PieChart.Data> getBookGraphStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String qu1 = "SELECT COUNT(*) FROM BOOK";
            String qu2 = "SELECT COUNT(*) FROM ISSUE";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Books (" + count + ")", count));
            }
            rs = execQuery(qu2);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Issued Books (" + count + ")", count));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public ObservableList<PieChart.Data> getMemberGraphStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String qu1 = "SELECT COUNT(*) FROM MEMBER";
            String qu2 = "SELECT COUNT(DISTINCT memberID) FROM ISSUE";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Members (" + count + ")", count));
            }
            rs = execQuery(qu2);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Active (" + count + ")", count));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return data;
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
        return conn;
    }

    public ProcessFlow getProcessFlow() {
        if (processFlow == null) {
            processFlow = DBHandler.getInstance().getLastProcessFlow();
            if (processFlow == null) {
                processFlow = processFlowManager.createNewProcessFlow();
            }

            processFlowManager.setActiveProcessFlow(activeProcessFlow);
        }
        return processFlow;
    }
    
	public ProcessFlow getLastProcessFlow() {
		List<ProcessFlow> processFlows;
		try {
			processFlows = DBHandler.getInstance().getLastOpenedProcessFlows(1);
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
	
	public void save(ProcessFlow processflow) throws DuplicateProcessFlowNameException {
		try {
			//in this case a new name is given, so we need to check whether we can create a copy or not
			//when a process already exists with this name, we will create a warning and will not save
			saveProcessName =processflow.getName();
			int saveProcessID = (DBHandler.getInstance().getProcessFlowIdForName(saveProcessName));
			if(saveProcessID != -1) {
				processFlowManager.updateProcessFlow(processflow);
			} else {
				DBHandler.getInstance().saveAsNewProcess(processflow);
			}
			
		} catch (SQLException e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}
    public void saveAsNewProcess(ProcessFlow processFlow) throws SQLException, DuplicateProcessFlowNameException {   	
    	try {
				LOGGER.info("Saving processflow with name: [" + processFlow.getName() + "].");
				DBHandler.getInstance().saveProcessFlow(processFlow);
		} catch (SQLException e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
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
	
    
    
//  try {
//  String update = "UPDATE BOOK SET TITLE=?, AUTHOR=?, PUBLISHER=? WHERE ID=?";
//  PreparedStatement stmt = conn.prepareStatement(update);
//  stmt.setString(1, book.getTitle());
//  stmt.setString(2, book.getAuthor());
//  stmt.setString(3, book.getPublisher());
//  stmt.setString(4, book.getId());
//  int res = stmt.executeUpdate();
//  return (res > 0);
//}
//catch (SQLException ex) {
//  LOGGER.log(Level.ERROR, "{}", ex);
//}
//return false;
//}
    
}
