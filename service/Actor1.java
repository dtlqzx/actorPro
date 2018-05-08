package com.bupt.sunnyd.service;

import com.bupt.sunnyd.service.wsn.PubSubNode;

import akka.actor.UntypedActor;

/**
 * Created by SunnyD on 2016/11/24.
 */
 // messages
 private static class Func1_Msg {
		 public final String Msg_param;

		 public Greet(String param) {
				 Msg_param = param;
		 }
 }

public class Actor1 extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Throwable {
			if(message instanceof Func1_Msg)
			{
				System.out.println("Actor1 received Func1_Msg message："+message.toString());
				PubSubNode.getInstance().getSend("").reliableNotify("all:G", (String)message, false, "A");
			}
			else if (message instanceof String)
			{
    		System.out.println("getNum2收到消息："+message);

    		PubSubNode.getInstance().getSend("").reliableNotify("all:G", (String)message, false, "A");
    		System.out.println("just");
    	}
    }
}
