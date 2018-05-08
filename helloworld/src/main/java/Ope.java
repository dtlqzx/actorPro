import java.util.List;

public class Ope 
{
//    // 实体所在的包名  
//    private String javaPackage;  
    // 实体类名
    private String opName;  
    // 属性集合  
    private List<ParameterInfo> opparams;  
    private List<ParameterInfo> opreturn;
	public String getOpName() {
		return opName;
	}
	public void setOpName(String opName) {
		this.opName = opName;
	}
	public List<ParameterInfo> getOpparams() {
		return opparams;
	}
	public void setOpparams(List<ParameterInfo> opparams) {
		this.opparams = opparams;
	}
	public List<ParameterInfo> getOpreturn() {
		return opreturn;
	}
	public void setOpreturn(List<ParameterInfo> opreturn) {
		this.opreturn = opreturn;
	}

    
}
