import java.util.List;

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

	public void notifyMsg(String operationName,Object...notification){

		Object message;

		switch(operationName)
		{
      <#list serv.getOpes() as operation>
      case "${operation.getOpName()}":
      {
        message = new Actor1.${operation.getOpName()}_Msg(<#list operation.getOpparams() as property><#if property.getType()== "string">(String)<#else>(${property.getType()})</#if> notification[${property_index}]<#if property_has_next>,</#if></#list>);
        break;
      }
      </#list>
			default:
			{
				message = new String("no match");
				break;
			}
		}

		if(a1==null){
			a1 = system.actorOf(Props.create(Actor1.class),"getnum2");
		}
		a1.tell(message, a1);
	}
}
