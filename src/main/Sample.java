package main;

import java.sql.SQLException;
import java.util.List;

import org.h2.jaqu.Db;

import static org.h2.jaqu.Function.*;

public class Sample {
	public static class Count{
		public String str;
		public Long count;
		
		public String toString(){
			return "[str:\""+str+"\" count:"+count+"]";
		}
	}
	public static void main(String[] args) {
		Db db = Db.open("jdbc:h2:mem:inmemory;DB_CLOSE_DELAY=-1", "sa", "");
//		db.from(new Test()).select();
		db.migrate(Test.class);
		new Transaction<Object>(db){
			@Override
			protected Object run() throws SQLException {
				Test t = new Test(1, "壱");
				db.insert(t);
				
				t = new Test(2, "弐");
				db.insert(t);

				t = new Test(3, "参");
				db.insert(t);

				if(false){ throw new SQLException(); } // 意図的なエラー

				t = new Test(4, "参");
				db.insert(t);

				t = new Test(5, "参");
				db.insert(t);
				return null;
			}
		}.execute();
		
		for(Test result : db.from(new Test()).select()){
			System.out.println(result.toString());
		}
		
		/* select(new ClassName(){{
		 * 	～
		 * }})
		 * この書き方をすると、SELECT で取得するカラムを明示することが出来る。
		 */
		final Test t = new Test();
		List<Count> cList = db.from(t).groupBy(t.str).select(new Count(){{
			str = t.str;
			count = count(t.str);
		}});
		
		for(Count c : cList){
			System.out.println(c.toString());
		}
	}
}
