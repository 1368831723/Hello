package com.pwj.utils;

public class FullStopUtil {

    private static String content = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    public static String getFullStop(String remark) {
        if (remark.length()==0){
            return "无。";
        }else {
            String lastStr = remark.substring(remark.length() - 1);
            if (content.contains(lastStr)){
                remark = remark.substring(0,remark.length()-1);
                return getFullStop(remark);
            }else {
                return remark+"。";
            }
        }
    }
}
