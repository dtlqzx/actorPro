import java.util.List;

public class Ope 
{
//    // ʵ�����ڵİ���  
//    private String javaPackage;  
    // ʵ������
    private String opName;  
    // ���Լ���  
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
