package com.bupt.sunnyd.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class GetNum2FuncImpl implements GetNum2Func{

	private ActorSystem system;
	private ActorRef a1=null;

	public GetNum2FuncImpl(){
		system = ActorSystem.create("getnum2Service");
		System.out.println("test");
	}

	public Object notifyMsg(Object notification){
		if(a1==null){
			a1 = system.actorOf(Props.create(Actor1.class),"getnum2");
		}
		a1.tell(notification, a1);

	}

}
