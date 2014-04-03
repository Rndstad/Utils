package net.amoebaman.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Wraps a {@link Connection} and provides convenient and exception-free methods
 * for accessing MySQL databases.
 * 
 * @author AmoebaMan
 */
public class SQLWrapper{
	
	private boolean suppressErrors, debug;
	private String url, database, username, password;
	private Connection connection = null;
	
	/**
	 * Constructs an SQLHandler for an SQL server with its URL, initial
	 * database, and connection credentials.
	 * 
	 * @param url the URL of the server
	 * @param database the database to use, accepts null or empty for no
	 *            database
	 * @param username the username to connect with
	 * @param password the password for the given username, accepts null, empty,
	 *            or "none" for no password
	 */
	public SQLWrapper(String url, String database, String username, String password){
		this.url = url;
		this.database = database;
		this.username = username;
		this.password = password;
		connect();
	}
	
	private void connect(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			if(password == null || password.isEmpty() || password.equals("none"))
				connection = DriverManager.getConnection("jdbc:mysql://" + url + "/");
			else
				connection = DriverManager.getConnection("jdbc:mysql://" + url + "/", username, password);
			if(database != null && !database.isEmpty()){
				executeCommand("CREATE DATABASE IF NOT EXISTS " + database);
				executeCommand("USE " + database);
			}
		}
		catch(Exception e){
			if(!suppressErrors)
				e.printStackTrace();
		}
	}
	
	private boolean validateConnection(){
		if(!isConnected(1)){
			if(debug)
				Bukkit.getLogger().info("[SQL] Connection lost, attempting to reconnect...");
			connect();
			if(!isConnected(1)){
				if(debug)
					Bukkit.getLogger().info("[SQL] Reconnection failed, aborting action");
				return false;
			}
			else if(debug)
				Bukkit.getLogger().info("[SQL] Successfully reconnected");
		}
		return true;
	}
	
	public void disconnect(){
		try{
			if(connection != null)
				connection.close();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks to see if the wrapped connection is valid and usable.
	 * 
	 * @param timeout a maximum connection timeout in seconds
	 * @return the result
	 */
	public boolean isConnected(int timeout){
		try{
			return connection != null && connection.isValid(timeout);
		}
		catch(Exception e){
			if(!suppressErrors)
				e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Turns on or off error suppression. When error suppression is on, no
	 * exceptions thrown by SQL commands and queries will be logged to console.
	 * 
	 * @param value the value
	 */
	public void setErrorSuppression(boolean value){
		suppressErrors = value;
	}
	
	/**
	 * Turns on or off command and query debugging. When debugging is on, all
	 * queries and commands will be logged to console with their success and/or
	 * failure and execution time.
	 * 
	 * @param value the value
	 */
	public void setDebugging(boolean value){
		debug = value;
	}
	
	/**
	 * Sends a command statement to the SQL server to be executed. Question
	 * marks (?) within the query will be replaced in order of occurrence by the
	 * included arguments in accordance with
	 * {@link PreparedStatement#setObject(int, Object)}.
	 * 
	 * @param command a command
	 * @param args arguments for the command
	 */
	public void executeCommand(String command, Object... args){
		if(command == null || command.isEmpty())
			return;
		long startTime = System.currentTimeMillis();
		if(!validateConnection())
			return;
		try{
			PreparedStatement statement = connection.prepareStatement(command);
			for(int i = 0; i < args.length; i++)
				statement.setObject(i + 1, args[i]);
			int count = statement.executeUpdate();
			if(debug)
				Bukkit.getLogger().info("[SQL] Successfully executed command \"" + statement.toString() + "\", updated " + count + " rows (" + (System.currentTimeMillis() - startTime) + "ms)");
		}
		catch(Exception e){
			if(debug)
				Bukkit.getLogger().info("[SQL] Failed to execute command \"" + command + "\" (" + (System.currentTimeMillis() - startTime) + "ms)");
			if(!suppressErrors)
				e.printStackTrace();
		}
	}
	
	/**
	 * Sends a command statement to the SQL server asynchronously. See
	 * {@link #executeCommand(String, Object...)}.
	 * 
	 * @param plugin a plugin, used to schedule the async task
	 * @param command a command
	 * @param args arguments for the command
	 */
	public void executeCommandAsync(Plugin plugin, final String command, final Object... args){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
			
			public void run(){
				executeCommand(command, args);
			}
		});
	}
	
	/**
	 * Sends a query statement to the SQL server to be executed. Question marks
	 * (?) within the query will be replaced in order of occurrence by the
	 * included arguments in accordance with
	 * {@link PreparedStatement#setObject(int, Object)}.
	 * 
	 * @param query a query
	 * @param args arguments for the query
	 * @return the result, or null if something went wrong
	 */
	public ResultSet executeQuery(String query, Object... args){
		if(query == null || query.isEmpty())
			return null;
		long startTime = System.currentTimeMillis();
		if(!validateConnection())
			return null;
		try{
			PreparedStatement statement = connection.prepareStatement(query);
			for(int i = 0; i < args.length; i++)
				statement.setObject(i + 1, args[i]);
			ResultSet set = statement.executeQuery();
			if(debug)
				Bukkit.getLogger().info("[SQL] Successfully executed query \"" + statement.toString() + "\" (" + (System.currentTimeMillis() - startTime) + "ms)");
			return set;
		}
		catch(Exception e){
			if(debug)
				Bukkit.getLogger().info("[SQL] Failed to execute query \"" + query + "\" (" + (System.currentTimeMillis() - startTime) + "ms)");
			if(!suppressErrors)
				e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Safely transforms a {@link ResultSet} (returned by
	 * {@link #executeQuery(String, Object...)}) into list of mappings, where
	 * each entry in the list represents, in order, the rows in the result set
	 * and the maps relate column name to data content.
	 * 
	 * @param rs a result set
	 * @param columnNames the names of the columns to get
	 * @return the transformed result set
	 */
	public RSListMap transformRS(ResultSet rs, String... columnNames){
		RSListMap transform = new RSListMap();
		try{
			while(rs.next()){
				Map<String, Object> line = new HashMap<String, Object>();
				for(String column : columnNames)
					line.put(column, rs.getObject(column));
				transform.add(line);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return transform;
	}
	
	/**
	 * Convenience class, since "{@code List<Map<String, Object>>}" is a pain in
	 * the @$$
	 * to write.
	 * 
	 * @author AmoebaMan
	 */
	@SuppressWarnings("serial")
	public class RSListMap extends ArrayList<Map<String, Object>>{}
}
