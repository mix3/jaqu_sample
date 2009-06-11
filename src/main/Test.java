package main;

import org.h2.jaqu.Table;
import static org.h2.jaqu.Define.*;

public class Test implements Table{
	public Integer id;
	public String str;
	
	public Test(){}
	public Test(Integer id, String str){
		this.id = id;
		this.str = str;
	}
	
	public String toString(){
		return "[id:"+this.id+" str:\""+this.str+"\"]";
	}
	
	@Override
	public void define() {
		primaryKey(id);
	}
}
