/*
 * This file contains software that has been made available under The Mozilla
 * Public License 1.1. Use and distribution hereof are subject to the
 * restrictions set forth therein.
 * 
 * Copyright (c) 2003 TADA AB - Taby Sweden All Rights Reserved
 */
package org.postgresql.pljava.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;

import org.postgresql.pljava.internal.ExecutionPlan;
import org.postgresql.pljava.internal.Portal;
import org.postgresql.pljava.internal.SPI;
import org.postgresql.pljava.internal.SPIException;

/**
 *
 * @author Thomas Hallgren
 */
public class SPIStatement implements Statement
	{
	private final SPIConnection m_connection;
	
	// Default settings.
	//
	private String    m_cursorName     = null;
	private int       m_fetchSize      = 100;
	private int       m_maxRows        = 0;
	private ResultSet m_resultSet      = null;
	private int       m_updateCount    = 0;
	private ArrayList m_batch          = null;

	public SPIStatement(SPIConnection conn)
	{
		m_connection = conn;
	}

	public void addBatch(String statement)
	throws SQLException
	{
		// Statements are converted to native SQL once they
		// are executed.
		//
		this.internalAddBatch(statement);
	}

	public void cancel()
	throws SQLException
	{
	}

	public void clearBatch()
	throws SQLException
	{
		m_batch = null;
	}

	public void clearWarnings()
	throws SQLException
	{
	}

	public void close()
	throws SQLException
	{
		if(m_resultSet != null)
		{	
			m_resultSet.close();
			m_resultSet = null;
		}
		m_updateCount = -1;
		m_cursorName = null;
		m_batch = null;
	}

	public boolean execute(String statement)
	throws SQLException
	{
		ExecutionPlan plan = ExecutionPlan.prepare(
			m_connection.nativeSQL(statement), null);

		int result = SPI.getResult();
		if(plan == null)
			throw new SPIException(result);
		
		boolean isResultSet = this.executePlan(plan);
		plan.invalidate();
		return isResultSet;
	}

	protected Object[] getParameterValues()
	{
		return null;
	}

	protected boolean executePlan(ExecutionPlan plan)
	throws SQLException
	{
		m_updateCount = -1;
		m_resultSet   = null;

		Object[] params = this.getParameterValues();
		boolean isResultSet = plan.isCursorPlan();
		if(isResultSet)
		{
			Portal portal = plan.cursorOpen(m_cursorName, params);
			m_resultSet = new SPIResultSet(this, portal, m_maxRows);
		}
		else
		{	
			plan.execp(params, m_maxRows);
			m_updateCount = SPI.getProcessed();
		}
		return isResultSet;
	}

	/**
	 * Return of auto generated keys is not yet supported.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public boolean execute(String statement, int autoGeneratedKeys)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Statement.execute(String,int)");
	}

	/**
	 * Return of auto generated keys is not yet supported.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public boolean execute(String statement, int[] columnIndexes)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Statement.execute(String,int[])");
	}

	/**
	 * Return of auto generated keys is not yet supported.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public boolean execute(String statement, String[] columnNames)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Statement.execute(String,String[])");
	}

	public int[] executeBatch()
	throws SQLException
	{
		int numBatches = (m_batch == null) ? 0 : m_batch.size();
		int[] result = new int[numBatches];
		for(int idx = 0; idx < numBatches; ++idx)
			result[idx] = this.executeBatchEntry(m_batch.get(idx));
		return result;
	}

	public ResultSet executeQuery(String statement)
	throws SQLException
	{
		this.execute(statement);
		return this.getResultSet();
	}

	public int executeUpdate(String statement)
	throws SQLException
	{
		this.execute(statement);
		return this.getUpdateCount();
	}

	/**
	 * Return of auto generated keys is not yet supported.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public int executeUpdate(String statement, int autoGeneratedKeys)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Auto generated key support not yet implemented");
	}


	/**
	 * Return of auto generated keys is not yet supported.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public int executeUpdate(String statement, int[] columnIndexes)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Auto generated key support not yet implemented");
	}


	/**
	 * Return of auto generated keys is not yet supported.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public int executeUpdate(String statement, String[] columnNames)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Auto generated key support not yet implemented");
	}

	/**
	 * Returns the Connection from that created this statement.
	 * @throws SQLException if the statement is closed.
	 */
	public Connection getConnection()
	throws SQLException
	{
		if(m_connection == null)
			throw new StatementClosedException();
		return m_connection;
	}

	public int getFetchDirection()
	throws SQLException
	{
		return ResultSet.FETCH_FORWARD;
	}
	
	public int getFetchSize()
	throws SQLException
	{
		return m_fetchSize;
	}
	
	public ResultSet getGeneratedKeys()
	throws SQLException
	{
		throw new SQLException("JDK 1.4 functionality not yet implemented");
	}
	
	public int getMaxFieldSize()
	throws SQLException
	{
		return Integer.MAX_VALUE;
	}
	
	public int getMaxRows()
	throws SQLException
	{
		return m_maxRows;
	}
	
	public boolean getMoreResults()
	throws SQLException
	{
		return false;
	}
	
	public boolean getMoreResults(int current)
	throws SQLException
	{
		return false;
	}
	
	public int getQueryTimeout()
	throws SQLException
	{
		return 0;
	}
	
	public ResultSet getResultSet()
	throws SQLException
	{
		return m_resultSet;
	}

	public int getResultSetConcurrency()
	{
		return ResultSet.CONCUR_READ_ONLY;
	}

	public int getResultSetHoldability()
	throws SQLException
	{
		throw new SQLException("JDK 1.4 functionality not yet implemented");
	}

	public int getResultSetType()
	{
		return ResultSet.TYPE_FORWARD_ONLY;
	}

	public int getUpdateCount()
	throws SQLException
	{
		return m_updateCount;
	}

	public SQLWarning getWarnings()
	throws SQLException
	{
		return null;
	}

	public void setCursorName(String cursorName)
	throws SQLException
	{
		m_cursorName = cursorName;
	}

	public void setEscapeProcessing(boolean enable)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Statement.setEscapeProcessing");
	}


	/**
	 * Only {@link ResultSet#FETCH_FORWARD is supported.
	 * @throws SQLException indicating that this feature is not supported
	 * for other values on <code>direction</code>.
	 */
	public void setFetchDirection(int direction)
	throws SQLException
	{
		if(direction != ResultSet.FETCH_FORWARD)
			throw new UnsupportedFeatureException("Non forward fetch direction");
	}

	public void setFetchSize(int size)
	throws SQLException
	{
		m_fetchSize = size;
	}

	public void setMaxFieldSize(int size)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Statement.setMaxFieldSize");
	}

	public void setMaxRows(int rows)
	throws SQLException
	{
		m_maxRows = rows;
	}

	public void setQueryTimeout(int seconds)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Statement.setQueryTimeout");
	}

	protected void internalAddBatch(Object batch)
	throws SQLException
	{
		if(m_batch == null)
			m_batch = new ArrayList();
		m_batch.add(batch);
	}

	protected int executeBatchEntry(Object batchEntry)
	throws SQLException
	{
		return (!this.execute(m_connection.nativeSQL((String)batchEntry)) && m_updateCount >= 0)
			? m_updateCount
			: SUCCESS_NO_INFO;
	}
}