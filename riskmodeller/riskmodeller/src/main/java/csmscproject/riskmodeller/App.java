package csmscproject.riskmodeller;

public class App {
    
	public static void main(String[] args) {
        RiskView view = new RiskView();
        RiskController controller = new RiskController(view);
        controller.configureGUI();
    }
}
