package de.google.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @desc A singleton database access class for MySQL
 * @author
 */
public final class DBConnection
{
	public Connection conn;
	private Statement statement;
	public static DBConnection db;

	private DBConnection()
	{
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "rusl";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "milinda";
		String password = "1234";
		try
		{
			Class.forName( driver ).newInstance();
			this.conn = DriverManager.getConnection( url + dbName, userName, password );
		}
		catch ( Exception sqle )
		{
			sqle.printStackTrace();
		}
	}

	/**
	 * @return DBConnection Database connection object
	 */
	public static synchronized DBConnection getDbCon()
	{
		if ( db == null )
		{
			db = new DBConnection();
		}
		return db;
	}

	/**
	 * @param query
	 *            String The query to be executed
	 * @return a ResultSet object containing the results or null if not available
	 * @throws SQLException
	 */
	public ResultSet query( String query ) throws SQLException
	{
		statement = db.conn.createStatement();
		ResultSet res = statement.executeQuery( query );
		return res;
	}

	/**
	 * @desc Method to insert data to a table
	 * @param insertQuery
	 *            String The Insert query
	 * @return boolean
	 * @throws SQLException
	 */
	public int insert( String insertQuery ) throws SQLException
	{
		statement = db.conn.createStatement();
		int result = statement.executeUpdate( insertQuery );
		return result;
	}

	public void update( String title_orginal, String title, String review, String language, String hotel )
			throws SQLException
	{
		// create our java preparedstatement using a sql update query
		PreparedStatement ps = conn.prepareStatement(
				"UPDATE hotel_review.reviews SET eng_title = ?, eng_review = ?, src_language = ? WHERE title = ? AND hotel_name = ?" );

		// set the preparedstatement parameters
		ps.setString( 1, title );
		ps.setString( 2, review );
		ps.setString( 3, language );
		ps.setString( 4, title_orginal );
		ps.setString( 5, hotel );

		// call executeUpdate to execute our sql update statement
		ps.executeUpdate();
		ps.close();
	}

	public void nlpTestUpdate( String title, String review ) throws SQLException
	{
		// create our java preparedstatement using a sql update query
		PreparedStatement ps = conn.prepareStatement( "UPDATE rusl.hotel_reviews SET eng_review = ? WHERE title = ?" );

		// set the preparedstatement parameters
		ps.setString( 1, review );
		ps.setString( 2, title );

		// call executeUpdate to execute our sql update statement
		ps.executeUpdate();
		ps.close();
	}

	public void saveSentence( int reviewId, String sentence ) throws SQLException
	{
		PreparedStatement ps = conn
				.prepareStatement( "INSERT INTO hotel_review.sentences (reviewsId,sentences) VALUES (?, ?)" );
		ps.setInt( 1, reviewId );
		ps.setString( 2, sentence );
		ps.executeUpdate();
		ps.close();
	}

}