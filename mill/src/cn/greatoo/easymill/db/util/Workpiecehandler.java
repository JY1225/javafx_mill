package cn.greatoo.easymill.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import cn.greatoo.easymill.entity.WorkPiece;
import cn.greatoo.easymill.entity.WorkPiece.Material;

public class Workpiecehandler {
	
	Connection conn = DBHandler.getInstance().getConnection();

	   public void saveWorkPiece(final WorkPiece workPiece) throws SQLException {
	        int type = workPiece.getType().getTypeId();
	        int shape = workPiece.getShape().getShapeId();
	        int material = workPiece.getMaterial().getId();
	        float height = workPiece.getHeight();
	        float length = workPiece.getLength();
	        float width = workPiece.getWidth();
	        float diameter = workPiece.getDiameter();
	    	float weight = workPiece.getWeight();	      	        
	        if (workPiece.getId() > 0) {
	            PreparedStatement stmt = conn.prepareStatement("UPDATE WORKPIECE SET TYPE = ?, SHAPE = ?, MATERIAL = ?, HEIGTH = ?, LENGTH = ?, WIDTH = ?, DIAMETER = ?, WEIGHT = ? WHERE ID = ?");
	            stmt.setInt(1, type);
	            stmt.setInt(2, shape);
	            stmt.setFloat(3,material);
	            stmt.setFloat(4, height);
	            stmt.setFloat(5, width);
	            stmt.setFloat(6, diameter);
	            stmt.setFloat(7, weight);
	            stmt.setFloat(8, material);
	            stmt.setInt(10, workPiece.getId());
	            stmt.executeUpdate();
	        } else {
	            PreparedStatement stmt = conn.prepareStatement("INSERT INTO WORKPIECE (TYPE, SHAPE, MATERIAL, HEIGTH, LENGTH, WIDTH, DIAMETER, WEIGHT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
	            stmt.setInt(1, type);
	            stmt.setInt(2, shape);
	            stmt.setFloat(3,material);
	            stmt.setFloat(4, height);
	            stmt.setFloat(5, width);
	            stmt.setFloat(6, diameter);
	            stmt.setFloat(7, weight);
	            stmt.setFloat(8, material);
	            stmt.setInt(10, workPiece.getId());            
	            stmt.executeUpdate();
	            ResultSet keys = stmt.getGeneratedKeys();
	            if ((keys != null) && (keys.next())) {
	                workPiece.setId(keys.getInt(1));
	            }
	        }
	    }
	  
	    public WorkPiece getWorkPieceById(final int processFlowId, final int workPieceId) throws SQLException {
	        WorkPiece workPiece = null;
	        if (processFlowId != 0) {
	            Map<Integer, WorkPiece> buffer = DBHandler.getInstance().getWorkPieceBuffer().get(processFlowId);
	            if (buffer != null) {
	                workPiece = buffer.get(workPieceId);
	                if (workPiece != null) {
	                    return workPiece;
	                }
	            } else {
	                Map<Integer, WorkPiece> newBuffer = new HashMap<Integer, WorkPiece>();
	                DBHandler.getInstance().getWorkPieceBuffer().put(processFlowId, newBuffer);
	            }
	        }
	        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM WORKPIECE WHERE ID = ?");
	        stmt.setInt(1, workPieceId);
	        ResultSet results = stmt.executeQuery();
	        if (results.next()) {
	            int typeId = results.getInt("TYPE");
	            int shapeId = results.getInt("SHAPE");
	            int materialId = results.getInt("MATERIAL");
		        float height = results.getFloat("HEIGHT");
		        float length = results.getFloat("LENGTH");
		        float width = results.getFloat("WIDTH");
		        float diameter = results.getFloat("DIAMETER");
	            float weight = results.getFloat("WEIGHT");
	            Material material = Material.getMaterialById(materialId);
	            
	            workPiece = new WorkPiece(WorkPiece.Type.getTypeById(typeId), length, width, height, diameter, material, weight);
	            workPiece.setId(workPieceId);
	        }
	        stmt.close();
	        if (processFlowId != 0) {
	        	DBHandler.getInstance().getWorkPieceBuffer().get(processFlowId).put(workPieceId, workPiece);
	        }
	        return workPiece;
	    }
	
		private void deleteWorkPiece(Integer workPieceId) throws SQLException {
			PreparedStatement stmtDeleteCoordinates = conn.prepareStatement("delete from workpiece where id=?");
			stmtDeleteCoordinates.setInt(1, workPieceId);
			stmtDeleteCoordinates.executeUpdate();
		}
		
	    
}
