package meiko.application.view;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import meiko.application.MainLable;

public class SettingController {

	private MainLable mainLable;
	private Stage dialogStage;

	@FXML
	private CheckBox isOnTop;

	@FXML
	private CheckBox isAutoRun;

	private boolean tempIsOnTop = MainLable.isAlwaysTop();
	private boolean tempisStartAtLogon = MainLable.isStartAtLogon();

	@FXML
	private void initialize(){
		isOnTop.setSelected(tempIsOnTop);
		isAutoRun.setSelected(tempisStartAtLogon);
	}

	public void setMainController(MainLable mainLable) {
		// TODO Auto-generated constructor stub
		this.mainLable = mainLable;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	@FXML
	private void handleClose() {
		tempIsOnTop = isOnTop.isSelected();
		MainLable.setAlwaysTop(tempIsOnTop);

		tempisStartAtLogon = isAutoRun.isSelected();
		MainLable.setStartAtLogon(tempisStartAtLogon);

		dialogStage.close();
	}

}
