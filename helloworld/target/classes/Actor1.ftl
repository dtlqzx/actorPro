package com.bupt.sunnyd.service;

import com.bupt.sunnyd.service.wsn.PubSubNode;

import akka.actor.UntypedActor;
 // messages
 private static class Func1_Msg {
		 public final String Msg_param;

		 public Greet(String param) {
				 Msg_param = param;
		 }
 }
 private static class Func1Return_Msg {
		 public final String Msg_param;

		 public Greet(String param) {
				 Msg_param = param;
		 }
 }

 <#list serv.getOpes() as operation>
   private static class ${operation.getOpName() + "_Msg"}
   {
     <#list operation.getOpparams() as property>
     <#if property.getType()== "string">public final String<#else>public final ${property.getType()}</#if> ${"Msg_"+property.getName()};
     </#list>
     public ${operation.getOpName() + "_Msg"}(<#list operation.getOpparams() as property><#if property.getType()== "string">String<#else>${property.getType()}</#if> ${property.getName() ! "null"}<#if property_has_next>, </#if></#list>)
     {
       <#list operation.getOpparams() as property>
       ${"Msg_"+property.getName()} = ${property.getName()};
       </#list>

     }
   }
   private static class ${operation.getOpName() + "Return_Msg"}
   {
     <#if operation.getOpreturn()?has_content><#list operation.getOpreturn() as return><#if return.getType()== "ArrayOfString">List<String><#else><#if return.getType()== "string">String<#else>${return.getType()}</#if></#if></#list> ${operation.getOpName() + "Msg_result"};</#if>
     public ${operation.getOpName() + "Return_Msg"}(<#if operation.getOpreturn()?has_content><#list operation.getOpreturn() as return><#if return.getType()== "ArrayOfString">List<String><#else><#if return.getType()== "string">String<#else>${return.getType()}</#if></#if></#list> ${operation.getOpName() + "result"}</#if>)
     {
       <#if operation.getOpreturn()?has_content>${operation.getOpName() + "Msg_result"} = ${operation.getOpName() + "result"};</#if>
     }
   }
 </#list>


public class Actor1 extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Throwable
    {
      <#list serv.getOpes() as operation>
      <#if operation?index == 0>
      if(message instanceof ${operation.getOpName() + "_Msg"})
			{
				System.out.println("Actor1 received"+ "${operation.getOpName() + "_Msg"}" +"message:"+message.toString());
				PubSubNode.getInstance().getSend("").reliableNotify("all:G", (String)message, false, "A");
			}
      else if (message instanceof ${operation.getOpName() + "Return_Msg"})
			{
        System.out.println("Actor1 received"+ "${operation.getOpName() + "Return_Msg"}" +"message:"+message.toString());
    		PubSubNode.getInstance().getSend("").reliableNotify("all:G", (String)message, false, "A");
    	}
      <#else>
      else if(message instanceof ${operation.getOpName() + "_Msg"})
			{
				System.out.println("Actor1 received"+ "${operation.getOpName() + "_Msg"}" +"message:"+message.toString());
				PubSubNode.getInstance().getSend("").reliableNotify("all:G", (String)message, false, "A");
			}
      else if (message instanceof ${operation.getOpName() + "Return_Msg"})
			{
        System.out.println("Actor1 received"+ "${operation.getOpName() + "Return_Msg"}" +"message:"+message.toString());
    		PubSubNode.getInstance().getSend("").reliableNotify("all:G", (String)message, false, "A");
    	}
      </#if>
      </#list>
    }
}
