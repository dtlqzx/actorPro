package com.bupt.sunnyd.service.wsn;

import wsn.wsnclient.command.SendWSNCommand;
import wsn.wsnclient.command.SendWSNCommandWSSyn;

/**
 * Created by SunnyD on 2016/11/20.
 */
public class PubSubNode {
    private String preWebAdd="http://10.108.164.213:8080/axis2/services/addServices";
    private String WSNAdd="http://10.108.164.213:9001/wsn-core";
    private static PubSubNode instance=null;

    public static PubSubNode getInstance(){
        if(instance==null){
            instance=new PubSubNode();
        }
        return instance;
    }

    public String subcribe(String addr,String topic){
        SendWSNCommand send=new SendWSNCommand(addr,WSNAdd);
        String res="";
        try {
            res=send.subscribe(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public SendWSNCommandWSSyn getSend(String addr){
    	SendWSNCommandWSSyn send=new SendWSNCommandWSSyn(addr,WSNAdd);
        return send;
    }

    public String getpreWebAdd() {
        return preWebAdd;
    }

    public String getWSNAdd() {
        return WSNAdd;
    }

}
