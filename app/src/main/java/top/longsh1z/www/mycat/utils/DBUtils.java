package top.longsh1z.www.mycat.utils;

import android.util.Log;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class DBUtils {

    private static final String TAG = "DBUtils>>>>>";

    private static  Statement getStatement(){
        try {
            Class jdbcDriver = Class.forName("com.mysql.jdbc.Driver");
            DriverManager.registerDriver((Driver) jdbcDriver.newInstance());
            String dbUrl = "jdbc:mysql://120.78.219.119:3306/MyCat?useUnicode=true&characterEncoding=utf8";
            String dbUser = "root";
            String dbPwd = "EDB0bd8cb80d";
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
            Statement stmt = conn.createStatement();
            return stmt;
        }catch (Exception ex) {
            ex.printStackTrace();
            Log.i(TAG, "getConn: 连接失败");
            return null;
        }
    }

    public  void check() {
        try {
            Statement stmt = getStatement();
            MyApp.setCurUserUsername("001");
            ResultSet rs=stmt.executeQuery("select * from User where username = "+MyApp.getCurUserUsername());
            if(rs.next()){
                String Name=rs.getString("phone");
                Log.i(TAG, "check: "+Name);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(TAG, "getConn: 查询失败");
        }
    }
}
