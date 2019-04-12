package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
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

import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Gripper;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.UserFrame;

public class DBHandler {
    private final static Logger LOGGER = LogManager.getLogger(DBHandler.class.getName());
    private static DBHandler handler = null;
    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;
    private String programName;
    private Map<String, Program> programBuffer = new HashMap<>();
	private Map<Integer, UserFrame> userFrameBuffer = new HashMap<>();	
	private List<Gripper> griperBuffer = new ArrayList<>();
	private List<Stacker> statckerBuffer = new ArrayList<>();
	private List<Clamping> clampBuffer = new ArrayList<>();
	private List<Smooth> smoothBuffer = new ArrayList<>();	
	private Program OProgram;

	public Program getOProgram() {
		return OProgram;
	}

	public void setOProgram(Program oProgram) {
		OProgram = oProgram;
	}

	public String getProgramName() {
		return programName;
	}

	public List<Smooth> getSmoothBuffer() {
		return smoothBuffer;
	}

	public void setSmoothBuffer(List<Smooth> smoothBuffer) {
		this.smoothBuffer = smoothBuffer;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public Map<String, Program> getProgramBuffer() {
		return programBuffer;
	}

	public void setProgramBuffer(Map<String, Program> programBuffer) {
		this.programBuffer = programBuffer;
	}

	public Map<Integer, UserFrame> getUserFrameBuffer() {
		return userFrameBuffer;
	}

	public void setUserFrameBuffer(Map<Integer, UserFrame> userFrameBuffer) {
		this.userFrameBuffer = userFrameBuffer;
	}

    public List<Gripper> getGriperBuffer() {
		return griperBuffer;
	}

	public void setGriperBuffer(List<Gripper> griperBuffer) {
		this.griperBuffer = griperBuffer;
	}

	public List<Stacker> getStatckerBuffer() {		
		return statckerBuffer;
	}

	public void setStatckerBuffer(List<Stacker> statckerBuffer) {
		this.statckerBuffer = statckerBuffer;
	}

	public List<Clamping> getClampBuffer() {
		return clampBuffer;
	}

	public void setClampBuffer(List<Clamping> clampBuffer) {
		this.clampBuffer = clampBuffer;
	}

	static {
        createConnection();
        inflateDB();
    }


    private DBHandler() {

    }

    public static synchronized DBHandler getInstance() {
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
    
}
