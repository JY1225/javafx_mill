package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.cnc.AbstractCNCMachine;
import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.cnc.EWayOfOperating;
import cn.greatoo.easymill.cnc.GenericMCode;
import cn.greatoo.easymill.cnc.MCodeAdapter;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;

public class CNCHandler {
	private final static Logger LOGGER = LogManager.getLogger(CNCHandler.class.getName());
	private static Connection conn = DBHandler.getInstance().getConnection();

	/**
	 * JY renturn CNCMachine
	 * 
	 */
	public static AbstractCNCMachine getCNCMillingMachine(final int id) {
		AbstractCNCMachine cncMillingMachine = null;
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CNCMILLINGMACHINE WHERE ID = ?");
			stmt.setInt(1, id);// 1
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				int socketConnectionId = results.getInt("SOCKETCONNECTION");
				PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM SOCKETCONNECTION WHERE ID = ?");
				stmt2.setInt(1, socketConnectionId);
				ResultSet results2 = stmt2.executeQuery();
				SocketConnection socketConnection = null;
				if (results2.next()) {
					socketConnection = new SocketConnection(SocketConnection.Type.CLIENT, results2.getString("NAME"),
							results2.getString("IPADDRESS"), results2.getInt("PORTNUMBER"));
					socketConnection.setId(results2.getInt("ID"));
				}
				EWayOfOperating wayOfOperating = EWayOfOperating
						.getWayOfOperatingById(results.getInt("WAYOFOPERATING"));// 2				
				cncMillingMachine = CNCMachine.getInstance(socketConnection, getMCodeAdapter(id), wayOfOperating);
				cncMillingMachine.setId(id);

			}
		} catch (SQLException ex) {
			LOGGER.log(Level.ERROR, "{}", ex);
		}
		return cncMillingMachine;
	}

	/**
	 * JY renturn MCodeAdapter RS1
	 */
	public static MCodeAdapter getMCodeAdapter(final int cncMachineId) {
		MCodeAdapter mCodeAdapter = null;
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MCODEADAPTER WHERE ID = ?");
			stmt.setInt(1, cncMachineId);// 1
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				String robotServiceInput1Name = results.getString("RSI1");// RS1
				String robotServiceInput2Name = results.getString("RSI2");// RS2
				String robotServiceInput3Name = results.getString("RSI3");// RS3
				String robotServiceInput4Name = results.getString("RSI4");// RS4
				String robotServiceInput5Name = results.getString("RSI5");// RS5
				String robotServiceOutput1Name = results.getString("RSA");// RSA
				List<String> robotServiceInputNames = new ArrayList<String>();
				robotServiceInputNames.add(robotServiceInput1Name);
				robotServiceInputNames.add(robotServiceInput2Name);
				robotServiceInputNames.add(robotServiceInput3Name);
				robotServiceInputNames.add(robotServiceInput4Name);
				robotServiceInputNames.add(robotServiceInput5Name);
				List<String> robotServiceOutputNames = new ArrayList<String>();
				robotServiceOutputNames.add(robotServiceOutput1Name);
				mCodeAdapter = new MCodeAdapter(getMCodes(cncMachineId), robotServiceInputNames,
						robotServiceOutputNames);
				mCodeAdapter.setId(cncMachineId);
			}
		} catch (SQLException ex) {
			LOGGER.log(Level.ERROR, "{}", ex);
		}
		return mCodeAdapter;
	}

	public static List<GenericMCode> getMCodes(final int cncMachineId) throws SQLException {
		List<GenericMCode> mCodes = new ArrayList<GenericMCode>();
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MCODE WHERE MCODEADAPTER = ?");
			stmt.setInt(1, cncMachineId);// 1
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				int id = results.getInt("ID");
				String name = results.getString("NAME");
				boolean usesRobotServiceInput1 = results.getBoolean("RSI1");
				boolean usesRobotServiceInput2 = results.getBoolean("RSI2");
				boolean usesRobotServiceInput3 = results.getBoolean("RSI3");
				boolean usesRobotServiceInput4 = results.getBoolean("RSI4");
				boolean usesRobotServiceInput5 = results.getBoolean("RSI5");
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
				boolean usesRobotServiceOutput1 = results.getBoolean("RSACTIVE");
				Set<Integer> robotServiceOutputsUsed = new HashSet<Integer>();
				if (usesRobotServiceOutput1) {
					robotServiceOutputsUsed.add(0);
				}
				int index = results.getInt("INDEX");
				GenericMCode mcode = new GenericMCode(id, index, name, robotServiceInputsUsed, robotServiceOutputsUsed);
				mCodes.add(index, mcode);
			}
		} catch (SQLException ex) {
			LOGGER.log(Level.ERROR, "{}", ex);
		}
		return mCodes;
	}

	/**
	 * 保存机床配置
	 * 
	 * @param AbstractCNCMachine
	 * @throws SQLException
	 */
	public static void saveCNC(AbstractCNCMachine cncMillingMachine) throws SQLException {
		if (cncMillingMachine.getId() <= 0) {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO SOCKETCONNECTION(NAME, IPADDRESS, PORTNUMBER) VALUES (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, cncMillingMachine.getSocketConnection().getName());
			stmt.setString(2, cncMillingMachine.getSocketConnection().getIpAddress());
			stmt.setFloat(3, cncMillingMachine.getSocketConnection().getPortNumber());
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			int socketConnectionId = 0;
			if ((keys != null) && (keys.next())) {
				socketConnectionId = keys.getInt(1);
			}

			PreparedStatement stmt2 = conn.prepareStatement(
					"INSERT INTO CNCMILLINGMACHINE(WAYOFOPERATING, SOCKETCONNECTION) VALUES (?, ?)");
			stmt2.setInt(1, cncMillingMachine.getWayOfOperating().getId());
			stmt2.setInt(2, socketConnectionId);
			stmt2.executeUpdate();


			PreparedStatement stmt3 = conn.prepareStatement(
					"INSERT INTO MCODEADAPTER(RSI1, RSI2,RSI3,RSI4,RSI5,RSA) VALUES (?, ?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt3.setString(1, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(0));
			stmt3.setString(2, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(1));
			stmt3.setString(3, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(2));
			stmt3.setString(4, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(3));
			stmt3.setString(5, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(4));
			stmt3.setString(6, cncMillingMachine.getMCodeAdapter().getRobotServiceOutputNames().get(0));
			stmt3.executeUpdate();
			ResultSet keys3 = stmt3.getGeneratedKeys();
			int mcodeId = 0;
			if ((keys3 != null) && (keys3.next())) {
				mcodeId = keys3.getInt(1);
			}
			
			List<GenericMCode> mCodes = cncMillingMachine.getMCodeAdapter().getGenericMCodes();
			for (int i = 0; i < mCodes.size(); i++) {
				PreparedStatement stmt4 = conn.prepareStatement(
						"INSERT INTO MCODE(INDEX, MCODEADAPTER,NAME,RSI1,RSI2,RSI3,RSI4,RSI5,RSACTIVE) VALUES (?,?,?,?,?,?,?,?,?)");
				stmt4.setInt(1, i);
				stmt4.setInt(2, mcodeId);
				stmt4.setString(3, mCodes.get(i).getName());
				stmt4.setBoolean(4, mCodes.get(i).getRobotServiceInputsRequired().contains(0));
				stmt4.setBoolean(5, mCodes.get(i).getRobotServiceInputsRequired().contains(1));
				stmt4.setBoolean(6, mCodes.get(i).getRobotServiceInputsRequired().contains(2));
				stmt4.setBoolean(7, mCodes.get(i).getRobotServiceInputsRequired().contains(3));
				stmt4.setBoolean(8, mCodes.get(i).getRobotServiceInputsRequired().contains(4));
				stmt4.setBoolean(9, mCodes.get(i).getRobotServiceOutputsUsed().contains(0));
				stmt4.executeUpdate();				
			}

		} else {
			PreparedStatement stmt5 = conn.prepareStatement(
					"UPDATE SOCKETCONNECTION SET NAME = ?, IPADDRESS = ?, PORTNUMBER = ? WHERE ID = ?");
			stmt5.setString(1, cncMillingMachine.getSocketConnection().getName());
			stmt5.setString(2, cncMillingMachine.getSocketConnection().getIpAddress());
			stmt5.setFloat(3, cncMillingMachine.getSocketConnection().getPortNumber());
			stmt5.setInt(4, cncMillingMachine.getSocketConnection().getId());
			stmt5.executeUpdate();
			
			PreparedStatement stmt6 = conn.prepareStatement("UPDATE CNCMILLINGMACHINE SET WAYOFOPERATING = ?, SOCKETCONNECTION = ? WHERE ID = ?");
			stmt6.setInt(1, cncMillingMachine.getWayOfOperating().getId());
			stmt6.setFloat(2, cncMillingMachine.getSocketConnection().getId());
			stmt6.setInt(3, cncMillingMachine.getId());
			stmt6.executeUpdate();
			
			PreparedStatement stmt7 = conn.prepareStatement("UPDATE MCODEADAPTER SET RSI1 = ?, RSI2 = ?,RSI3=?,RSI4=?,RSI5=?,RSA=? WHERE ID = ?");
			stmt7.setString(1, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(0));
			stmt7.setString(2, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(1));
			stmt7.setString(3, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(2));
			stmt7.setString(4, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(3));
			stmt7.setString(5, cncMillingMachine.getMCodeAdapter().getRobotServiceInputNames().get(4));
			stmt7.setString(6, cncMillingMachine.getMCodeAdapter().getRobotServiceOutputNames().get(0));
			stmt7.setInt(7, cncMillingMachine.getId());
			stmt7.executeUpdate();
			
			List<GenericMCode> mCodes = cncMillingMachine.getMCodeAdapter().getGenericMCodes();
			for (int i = 0; i < mCodes.size(); i++) {
				PreparedStatement stmt8 = conn.prepareStatement(
						"UPDATE MCODE SET NAME=?,RSI1=?,RSI2=?,RSI3=?,RSI4=?,RSI5=?,RSACTIVE=? WHERE MCODEADAPTER = ? AND INDEX=?");							
				stmt8.setString(1, mCodes.get(i).getName());
				stmt8.setBoolean(2, mCodes.get(i).getRobotServiceInputsRequired().contains(0));
				stmt8.setBoolean(3, mCodes.get(i).getRobotServiceInputsRequired().contains(1));
				stmt8.setBoolean(4, mCodes.get(i).getRobotServiceInputsRequired().contains(2));
				stmt8.setBoolean(5, mCodes.get(i).getRobotServiceInputsRequired().contains(3));
				stmt8.setBoolean(6, mCodes.get(i).getRobotServiceInputsRequired().contains(4));
				stmt8.setBoolean(7, mCodes.get(i).getRobotServiceOutputsUsed().contains(0));				
				stmt8.setInt(8, cncMillingMachine.getId());
				stmt8.setInt(9, i);	
				stmt8.executeUpdate();				
			}

		}

	}

}
