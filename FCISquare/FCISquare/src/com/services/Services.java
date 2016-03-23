package com.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONObject;

import com.models.DBConnection;
import com.models.UserModel;

import java.util.ArrayList;

@Path("/")
public class Services {

	/*
	 * @GET
	 * 
	 * @Path("/signup")
	 * 
	 * @Produces(MediaType.TEXT_HTML) public Response signUp(){ return
	 * Response.ok(new Viewable("/Signup.jsp")).build(); }
	 */

	@POST
	@Path("/signup")
	@Produces(MediaType.TEXT_PLAIN)
	public String signUp(@FormParam("name") String name,
			@FormParam("email") String email, @FormParam("pass") String pass) {
		UserModel user = UserModel.addNewUser(name, email, pass);
		JSONObject json = new JSONObject();
		json.put("id", user.getId());
		json.put("name", user.getName());
		json.put("email", user.getEmail());
		json.put("pass", user.getPass());
		json.put("lat", user.getLat());
		json.put("long", user.getLon());
		return json.toJSONString();
	}

	@POST
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public String login(@FormParam("email") String email,
			@FormParam("pass") String pass) {
		UserModel user = UserModel.login(email, pass);
		JSONObject json = new JSONObject();
		json.put("id", user.getId());
		json.put("name", user.getName());
		json.put("email", user.getEmail());
		json.put("pass", user.getPass());
		json.put("lat", user.getLat());
		json.put("long", user.getLon());
		return json.toJSONString();
	}
	
	@POST
	@Path("/updatePosition")
	@Produces(MediaType.TEXT_PLAIN)
	public String updatePosition(@FormParam("id") String id,@FormParam("lat") String lat, @FormParam("long") String lon)
	{
		Boolean status = UserModel.updateUserPosition(Integer.parseInt(id), Double.parseDouble(lat), Double.parseDouble(lon));
		JSONObject json = new JSONObject();
		json.put("status", status ? 1 : 0);
		return json.toJSONString();
	}
	
	@POST
	@Path("/followUser")
	@Produces(MediaType.TEXT_PLAIN)
	public String followUser(@FormParam("id") String id,@FormParam("email") String email)
	{
		Boolean status = UserModel.follow(Integer.parseInt(id), email);
		JSONObject json = new JSONObject();
		json.put("status", status ? 1 : 0);
		return json.toJSONString();
	}
	
	@POST
	@Path("/unfollowUser")
	@Produces(MediaType.TEXT_PLAIN)
	public String unFollow(@FormParam("id") String id, @FormParam("email") String email) throws SQLException
	{
		boolean status = UserModel.unfollowUser(Integer.parseInt(id),email);
		JSONObject json = new JSONObject();
		json.put("Status", status ? 1 : 0);
		return json.toJSONString();
	}

	@POST
	@Path("/getLastLocation")
	@Produces(MediaType.TEXT_PLAIN)
	public String getLastLocation(@FormParam("id") String id)
	{
		String lastLocation = UserModel.getLastLocation(Integer.parseInt(id));
		JSONObject json = new JSONObject();
		json.put("lastLocation" , lastLocation) ;
		return json.toJSONString();
	}
	
	@POST 
	@Path("/getAllFollowers")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAllFollowers(@FormParam("id") String id)
	{
		ArrayList <UserModel> allFollowers = UserModel.getAllFollowers(Integer.parseInt(id));
		JSONObject json = new JSONObject();
		json.put("allFollowers" , allFollowers) ;
		return json.toJSONString();
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String getJson() {
		return "Hello after editing";
		// Connection URL:
		// mysql://$OPENSHIFT_MYSQL_DB_HOST:$OPENSHIFT_MYSQL_DB_PORT/
	}
}
