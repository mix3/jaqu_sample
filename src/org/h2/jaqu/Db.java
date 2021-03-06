/*
 * Copyright 2004-2009 H2 Group. Multiple-Licensed under the H2 License,
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.jaqu;

//## Java 1.5 begin ##
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.h2.jaqu.util.Utils;
import org.h2.jaqu.util.WeakIdentityHashMap;
import org.h2.util.JdbcUtils;

/**
 * This class represents a connection to a database.
 */
//## Java 1.5 begin ##
public class Db {

    private static final WeakIdentityHashMap<Object, Token> TOKENS =
        Utils.newWeakIdentityHashMap();

    private final Connection conn;
    private final Map<Class, TableDefinition> classMap = Utils.newHashMap();

    Db(Connection conn) {
        this.conn = conn;
    }

    static <X> X registerToken(X x, Token token) {
        TOKENS.put(x, token);
        return x;
    }

    static Token getToken(Object x) {
        return TOKENS.get(x);
    }

    private static <T> T instance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static Db open(String url, String user, String password) {
        try {
            Connection conn = JdbcUtils.getConnection(null, url, user, password);
            return new Db(conn);
        } catch (SQLException e) {
            throw convert(e);
        }
    }

    public static Db open(String url, String user, char[] password) {
        try {
            Properties prop = new Properties();
            prop.setProperty("user", user);
            prop.put("password", password);
            Connection conn = JdbcUtils.getConnection(null, url, prop);
            return new Db(conn);
        } catch (SQLException e) {
            throw convert(e);
        }
    }

    private static Error convert(Exception e) {
        return new Error(e);
    }

    public <T> void insert(T t) {
        Class< ? > clazz = t.getClass();
        //define(clazz).createTableIfRequired(this).insert(this, t);
        define(clazz).insert(this, t);
    }

    public <T extends Object> Query<T> from(T alias) {
        Class< ? > clazz = alias.getClass();
        define(clazz).createTableIfRequired(this);
        return Query.from(this, alias);
    }

    public void migrate(Class<? extends Table>... clazz){
    	for(Class<? extends Table> clz : clazz){
    		createTable(clz);
    	}
    }
    
    <T> void createTable(Class<T> clazz) {
        define(clazz).createTableIfRequired(this);
    }

    <T> TableDefinition<T> define(Class<T> clazz) {
        TableDefinition def = classMap.get(clazz);
        if (def == null) {
            def = new TableDefinition(clazz);
            def.mapFields();
            classMap.put(clazz, def);
            if (Table.class.isAssignableFrom(clazz)) {
                T t = instance(clazz);
                Table table = (Table) t;
                Define.define(def, table);
            }
        }
        return def;
    }

    public void close() {
        try {
            conn.close();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public <A> TestCondition<A> test(A x) {
        return new TestCondition<A>(x);
    }

    public <T> void insertAll(List<T> list) {
        for (T t : list) {
            insert(t);
        }
    }

    PreparedStatement prepare(String sql) {
        try {
            return conn.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    TableDefinition getTableDefinition(Class< ? > clazz) {
        return classMap.get(clazz);
    }

    ResultSet executeQuery(String sql) {
        try {
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    int executeUpdate(String sql) {
        try {
            return conn.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setAutoCommit(boolean autoCommit){
    	try {
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }
    
    public void commit(){
    	try {
			conn.commit();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }
    
    public void rollback(){
    	try {
			conn.rollback();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }
    
    public boolean getAutoCommit(){
    	try {
			return conn.getAutoCommit();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }
//    <X> FieldDefinition<X> getFieldDefinition(X x) {
//        return aliasMap.get(x).getFieldDefinition();
//    }
//
//    <X> SelectColumn<X> getSelectColumn(X x) {
//        return aliasMap.get(x);
//    }

}
//## Java 1.5 end ##
