package org.ahope.cumtlib.Utilities;

public class StringReplace {
	public static String replace(String con ,String tag,String rep){
	    int j=0;
	    int i=0;
	    String RETU="";
	    String temp =con;
	    int tagc =tag.length();
	    while(i<con.length()){
	      if(con.substring(i).startsWith(tag)){
	        temp =con.substring(j,i)+rep;
	        RETU+= temp;
	        i+=tagc;
	        j=i;
	      }else{
	        i+=1;
	      }
	    }
	    RETU +=con.substring(j);
	    return RETU;
	  }
}
