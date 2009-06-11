package main;

import java.sql.SQLException;

import org.h2.jaqu.Db;

public abstract class Transaction<T> {
	protected Db db = null;
	protected T back = null;
	
	protected Transaction(Db db){
		this.db = db;
	}
	
	public T execute(){
		try{
			db.setAutoCommit(false);
			back = run();
			db.commit();
		}catch(SQLException e){
			db.rollback();
		}finally{
			db.setAutoCommit(true);
		}
		return back;
	}
	
	protected abstract T run() throws SQLException;
}
