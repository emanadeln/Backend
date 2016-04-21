package com.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;
import java.util.ArrayList;
public class UserModel {

	
	private String name;
	private String email;
	private String pass;
	private Integer id;
	private Double lat;
	private Double lon;
	
	public String getPass(){
		return pass;
	}
	
	public void setPass(String pass){
		this.pass = pass;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}
	
	public static UserModel addNewUser(String name, String email, String pass) {
		try {
			Connection conn = DBConnection.getActiveConnection();
			String sql = "Insert into users (`name`,`email`,`password`) VALUES  (?,?,?)";
			// System.out.println(sql);

			PreparedStatement stmt;
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setString(3, pass);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				UserModel user = new UserModel();
				user.id = rs.getInt(1);
				user.email = email;
				user.pass = pass;
				user.name = name;
				user.lat = 0.0;
				user.lon = 0.0;
				return user;
			}
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	
	public static UserModel login(String email, String pass) {
		try {
			Connection conn = DBConnection.getActiveConnection();
			String sql = "Select * from users where `email` = ? and `password` = ?";
			PreparedStatement stmt;
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, pass);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				UserModel user = new UserModel();
				user.id = rs.getInt(1);
				user.email = rs.getString("email");
				user.pass = rs.getString("password");
				user.name = rs.getString("name");
				user.lat = rs.getDouble("lat");
				user.lon = rs.getDouble("long");
				return user;
			}
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean updateUserPosition(Integer id, Double lat, Double lon) {
		try{
			Connection conn = DBConnection.getActiveConnection();
			String sql = "Update users set lat = ? , longt = ? where userID = ?";
			PreparedStatement stmt;
			stmt = conn.prepareStatement(sql);
			stmt.setDouble(1, lat);
			stmt.setDouble(2, lon);
			stmt.setInt(3, id);
			stmt.executeUpdate();
			return true;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static int getUserId(String email) throws SQLException
    {
        int UserId = 0;
        
        Connection conn = DBConnection.getActiveConnection();
        PreparedStatement stmt;
        
        String sql = "SELECT  userID FROM users WHERE email = ?";
        
		stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, email);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) 
        {
            UserId = rs.getInt("userID");
        }
       rs.close();
       conn.close();
       return UserId;
    }
	
	public static boolean follow(Integer id,String email) 
	{
		try{
			Connection conn = DBConnection.getActiveConnection();
			int idToFollow=getUserId(email);
			System.out.println("id"+idToFollow);
			
			String sql = "INSERT INTO Followers VALUES ( ? ,?)";
			PreparedStatement stmt;
			
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setInt(2, idToFollow);
			stmt.executeUpdate();
			return true;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean unfollowUser(int id, String email) throws SQLException
	{
		int friendId = getUserId(email);
		try {
			Connection conn = DBConnection.getActiveConnection();
			String sql = "DELETE FROM Followers WHERE FollowerID = ? AND FollowedUserID = ?";
			PreparedStatement stmt = null;
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1,id);
			stmt.setInt(2, friendId);
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getLastLocation(Integer id) {

		String lastLocation = null ;
		try {

			Connection conn = DBConnection.getActiveConnection();
			String sql = "Select * from users where userID = ?";
			PreparedStatement stmt;
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			UserModel user = new UserModel();
			if (rs.next()) 
			{
				  user.lat = rs.getDouble("lat");
		          user.lon = rs.getDouble("longt");
	        }

			lastLocation =getLastLocationName(user.lat , user.lon) + ", latitude : "+Double.toString(user.lat)+", longitude : "+Double.toString(user.lon) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lastLocation ;
	}
    public static String getLastLocationName(Double lat , Double lon){
        String lastLocationName = null ;
		try {

			Connection conn = DBConnection.getActiveConnection();
			String sql = "Select * from places where lat = ? AND longt = ?";
			PreparedStatement stmt;
			stmt = conn.prepareStatement(sql);
			stmt.setDouble(1, lat);
			stmt.setDouble(2, lon);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) 
			{
				lastLocationName = rs.getString("name") ;
	        }
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lastLocationName ;
    }
    
    public static ArrayList <UserModel> getAllFollowers (int followedUserID)
    {
    	ArrayList <String> str = new ArrayList <>();
    	ArrayList <UserModel> followers = new ArrayList <>();
    	try
    	{
	    	Connection conn = DBConnection.getActiveConnection();
	    	String sql = "Select name from users , followers where followers.FollowerID = users.userID and followedUserID = ?";
	    	PreparedStatement stmt;
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, followedUserID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{	
				str.add(rs.getString("name"));
			}
			int n= str.size();
			for(int i=0;i<n;i++)
			{
				followers.add(new UserModel());
			}	
			
			for(int i=0;i<n;i++)
			{
				followers.get(i).setName(str.get(i));

			}	
    	}
    	catch(SQLException e)
    	{
			e.printStackTrace();
    	}
    	return followers;
    }
	public String toString() 
	{ 	
		String s = null;
		if (name != null)
		{
			s="Name: " +this.name;
		}
		if(email != null)
		{
			s+="Email: " +this.email;
			
		}
		if(id != null)
		{
			String id=Integer.toString(this.id);
			s+="ID: "+id;
		}
		
	    return s;
	} 
	
}
