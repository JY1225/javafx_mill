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

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.RobotSetting;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.Step;
import cn.greatoo.easymill.entity.UserFrame;
import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;

public class DBHandler {


    private final static Logger LOGGER = LogManager.getLogger(DBHandler.class.getName());
    private static DBHandler handler = null;
    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;
	private Map<Integer, UserFrame> userFrameBuffer;
    private Map<Integer, Map<Integer, Coordinates>> coordinatesBuffer;
    private Map<Integer, Map<Integer, Step>> stepBuffer;
    private Map<Integer, Map<Integer, WorkPiece>> workPieceBuffer;
	private Map<Integer, Gripper> grippersBuffer;
	private Map<Integer, RobotSetting> RobotSettingBuffer;
	private Map<Integer, SocketConnection> socketConnectionBuffer;
	
	public Map<Integer, SocketConnection> getSocketConnectionBuffer() {
		return socketConnectionBuffer;
	}

	public void setSocketConnectionBuffer(Map<Integer, SocketConnection> socketConnectionBuffer) {
		this.socketConnectionBuffer = socketConnectionBuffer;
	}

	public Map<Integer, Map<Integer, Step>> getStepBuffer() {
		return stepBuffer;
	}

	public Map<Integer, RobotSetting> getRobotSettingBuffer() {
		return RobotSettingBuffer;
	}

	public void setRobotSettingBuffer(Map<Integer, RobotSetting> robotSettingBuffer) {
		RobotSettingBuffer = robotSettingBuffer;
	}

	public void setStepBuffer(Map<Integer, Map<Integer, Step>> stepBuffer) {
		this.stepBuffer = stepBuffer;
	}

	private Map<Integer, Map<Integer, Smooth>> smoothsBuffer;
    
	public Map<Integer, Map<Integer, Smooth>> getSmoothsBuffer() {
		return smoothsBuffer;
	}

	public void setSmoothsBuffer(Map<Integer, Map<Integer, Smooth>> smoothsBuffer) {
		this.smoothsBuffer = smoothsBuffer;
	}

	public Map<Integer, UserFrame> getUserFrameBuffer() {
		return userFrameBuffer;
	}

	public void setUserFrameBuffer(Map<Integer, UserFrame> userFrameBuffer) {
		this.userFrameBuffer = userFrameBuffer;
	}

	public Map<Integer, Map<Integer, Coordinates>> getCoordinatesBuffer() {
		return coordinatesBuffer;
	}

	public void setCoordinatesBuffer(Map<Integer, Map<Integer, Coordinates>> coordinatesBuffer) {
		this.coordinatesBuffer = coordinatesBuffer;
	}

	public Map<Integer, Map<Integer, WorkPiece>> getWorkPieceBuffer() {
		return workPieceBuffer;
	}

	public void setWorkPieceBuffer(Map<Integer, Map<Integer, WorkPiece>> workPieceBuffer) {
		this.workPieceBuffer = workPieceBuffer;
	}

	public Map<Integer, Gripper> getGrippersBuffer() {
		return grippersBuffer;
	}

	public void setGrippersBuffer(Map<Integer, Gripper> grippersBuffer) {
		this.grippersBuffer = grippersBuffer;
	}


    public void clearBuffers(final int processFlowId) {   	
        coordinatesBuffer.remove(processFlowId);
        workPieceBuffer.remove(processFlowId);
    }

    static {
        createConnection();
        inflateDB();
    }


    private DBHandler() {
    	
        this.userFrameBuffer = new HashMap<Integer, UserFrame>();
        this.coordinatesBuffer = new HashMap<Integer, Map<Integer, Coordinates>>();
        this.workPieceBuffer = new HashMap<Integer, Map<Integer, WorkPiece>>();
    }

    public static DBHandler getInstance() {
        if (handler == null) {
            handler = new DBHandler();
        }
        return handler;
    }
    
    private static void inflateDB() {
        List<String> tableData = new ArrayList<>();
        String tableName = null;
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
                tableName = entry.getAttribute("name");
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
     * 
     * @param program
     */
    public void saveProgram(Program program) {
    	try {
    		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM PROGRAM WHERE NAME = ?");		
			stmt.setString(1, program.getName());
			ResultSet results = stmt.executeQuery();
			if(results != null) {
				PreparedStatement updateStmt = conn.prepareStatement("UPDATE PROGRAM SET NAME = ?");		
				stmt.setString(1, program.getName());
			}else {
				
			}
		} catch (SQLException e) {			
			e.printStackTrace();
		}
		
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
    
    
	public SocketConnection getSocketConnectionById(final int socketConnectionId) throws SQLException {
		SocketConnection socketConnection = socketConnectionBuffer.get(socketConnectionId);
		if (socketConnection != null) {
			return socketConnection;
		}
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM SOCKETCONNECTION WHERE ID = ?");
		stmt.setInt(1, socketConnectionId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			String ipAddress = results.getString("IPADDRESS");
			int portNumber = results.getInt("PORTNR");
			boolean client = results.getBoolean("CLIENT");
			String name = results.getString("NAME");
			if (client) {
				socketConnection = new SocketConnection(SocketConnection.Type.CLIENT, name, ipAddress, portNumber);
			} else {
				socketConnection = new SocketConnection(SocketConnection.Type.SERVER, name, ipAddress, portNumber);
			}
			socketConnection.setId(socketConnectionId);
		}
		stmt.close();
		socketConnectionBuffer.put(socketConnectionId, socketConnection);
		return socketConnection;
	}
    
}
