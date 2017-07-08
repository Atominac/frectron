package com.fretron.fleet.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.HashMap;


public class UserSessionManager {

    private SharedPreferences pref ;
    private SharedPreferences.Editor editor;
    private Context _context;
    public static final String PREFER_NAME =   "P1" ;
    public static final String IS_USER_LOGIN =   "isUserLoggedIn" ;
    public static final String PHONE =   "email" ;
    public static final String TOKEN =   "token" ;

    public  UserSessionManager(Context context){
        this._context=context;
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void create_login_session(String email, String password){
        editor.putBoolean(IS_USER_LOGIN,true);
        editor.putString(PHONE, email);
        editor.putString(TOKEN,password);
        editor.commit();
    }

    public boolean check_login(){

        if(!this.isUserLoggedIn()){

            Intent i =  new Intent("com.sample.project_frectron.Phone_no");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;
    }

    HashMap<String,String> getUserDetails(){
        HashMap<String,String> user = new HashMap<>();
        user.put(PHONE,pref.getString(PHONE,null));
        user.put(TOKEN,pref.getString(TOKEN,null));
        return user;
    }

    public void logout(){

        editor.clear();
        editor.commit();
        Intent i =  new Intent("com.sample.project_frectron.Phone_no");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

}

