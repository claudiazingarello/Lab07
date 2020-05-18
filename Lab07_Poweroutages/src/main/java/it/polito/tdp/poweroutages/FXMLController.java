package it.polito.tdp.poweroutages;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.poweroutages.model.Model;
import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.PowerOutages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<Nerc> choiceBox;

    @FXML
    private TextField txtYears;

    @FXML
    private TextField txtHours;

    @FXML
    private Button btnWorst;

    @FXML
    private TextArea txtResult;

    @FXML
    void doRun(ActionEvent event) {
    	txtResult.clear();
    	try {
    		Nerc selectedNerc = choiceBox.getValue();
    		if (selectedNerc ==  null) {
    			txtResult.setText("Select a NERC (area identifier)!");
    			return;
    		}
    		
    		int maxAnni = Integer.parseInt(txtYears.getText());
    		int yearListSize = model.getYearList().size();
    		
    		if (maxAnni <= 0 || maxAnni > yearListSize) {
    			txtResult.setText("Select a number of years in range [1, "+ yearListSize + "]");
    			return;
    		}
    		
    		int maxOre = Integer.parseInt(txtHours.getText());
    		if(maxOre <= 0) {
    			txtResult.setText("Select a number of hours greater than 0");
    			return;
    		}
    		
    		txtResult.setText(
    				String.format("Computing the worst case analysis... for %d hours and %d years", maxOre, maxAnni));
    		List<PowerOutages> worstCase = model.getWorstCase( maxAnni, maxOre, selectedNerc);
    		
    		txtResult.clear();
    		txtResult.appendText("Tot people affected: " + model.sumAffectedPeople(worstCase)+"\n");
    		txtResult.appendText("Tot hours of outage: "+ model.sumOutageHours(worstCase) + "\n");
    		
    		for(PowerOutages ee : worstCase) {
    			txtResult.appendText(String.format("%d %s %d %d", ee.getYear(), ee.getOutageStart(),
    					ee.getOutageEnd(), ee.getOutageDuration(), ee.getAffectedPeople()));
    			txtResult.appendText("\n");
    		}
    	} catch(NumberFormatException e){
    		txtResult.setText("Insert a valid number of years and of hours");
    	}
    }

    @FXML
    void initialize() {
        assert choiceBox != null : "fx:id=\"choiceBox\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtYears != null : "fx:id=\"txtYears\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtHours != null : "fx:id=\"txtHours\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnWorst != null : "fx:id=\"btnWorst\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		
		choiceBox.getItems().addAll(model.getNercList());
		
	}
}
