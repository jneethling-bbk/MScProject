package csmscproject.riskmodeller;

public class App {
    
	public static void main(String[] args) {
        BuildView view = new BuildView();
        BuildModel model = new BuildModel();
        BuildController controller = new BuildController(view, model);
        controller.configureGUI();
    }
}
