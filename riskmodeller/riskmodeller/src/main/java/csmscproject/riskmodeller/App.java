package csmscproject.riskmodeller;

public class App {
    
	public static void main(String[] args) {
        BuildView view = new BuildView();
        BuildController controller = new BuildController(view);
        controller.configureGUI();
    }
}
