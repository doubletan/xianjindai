package com.xinhe.cashloan.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by tantan on 2017/8/3.
 */

public class GetMyKey {

    private static int yi;
    private static String[] er1;
    private static String[] san1;
    private static String[] si1;
    private static String[] shiyi1;
    private static String[] shier1;
    private static String[] zimu;
    private static String er;
    private static String san;
    private static String si;
    private static int wu;
    private static int ba;
    private static String shiyi;
    private static String shier;
    private static String shisan;
    private static String shisi;
    private static String shiwu;
    private static String key;

    public static String getKey(){
        try {
            String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new
                    Date());
            String year = time.substring(0, 4);
            yi=(Integer.valueOf(year)-2016)%10;

            String month = time.substring(4, 6);
            er1="1,2,3,4,5,6,7,8,9,a,b,c".split(",");
            er=er1[Integer.valueOf(month)-1];

            String minute1 = time.substring(10, 11);
            san1="o,p,q,r,s,t".split(",");
            san=san1[Integer.valueOf(minute1)];

            String minute2 = time.substring(11, 12);
            si1="q,r,s,t,u,v,w,x,y,z".split(",");
            si=si1[Integer.valueOf(minute2)];

            wu=new Random().nextInt(300)+200;
            ba=new Random().nextInt(500)+300;

            String hour = time.substring(8, 10);
            shiyi1="a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x".split(",");
            shiyi=shiyi1[Integer.valueOf(hour)];

            String day = time.substring(6, 8);
            shier1="1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",");
            shier=shier1[Integer.valueOf(day)-1];

            zimu="a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",");

            shisan=zimu[new Random().nextInt(26)];
            shisi=zimu[new Random().nextInt(26)];
            shiwu=zimu[new Random().nextInt(26)];
            key=yi+er+san+si+wu+ba+shiyi+shier+shisan+shisi+shiwu;
            return key;
        } catch (Exception e) {
            return "";
        }
    }
}
