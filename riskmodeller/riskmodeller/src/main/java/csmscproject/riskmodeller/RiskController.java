package csmscproject.riskmodeller;

public class RiskController {

	private RiskView view;
	
	public RiskController(RiskView view) {
		this.view = view;
	}
	
	public void configureGUI() {
		view.displayGUI();
	}
}
