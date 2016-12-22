package meiko.application;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import meiko.application.view.MainController;
import meiko.application.view.ResizeHelper;
import meiko.application.view.ScreenShotController;
import meiko.application.view.SettingController;

public class MainLable extends Application {

	private AnchorPane mainLayout;
	private Stage primaryStage;
	private static boolean isAlwaysTop;
	private boolean tempIsOnTop;

	public static boolean isAlwaysTop() {
		return isAlwaysTop;
	}

	public static void setAlwaysTop(boolean isAlwwaysTop) {
		MainLable.isAlwaysTop = isAlwwaysTop;
	}

	private static boolean isStartAtLogon;
	private boolean tempisStartAtLogon;

	public static boolean isStartAtLogon() {
		return isStartAtLogon;
	}

	public static void setStartAtLogon(boolean isStartAtLogon) {
		MainLable.isStartAtLogon = isStartAtLogon;
	}

	@FXML
	private AnchorPane moveLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("±„«©");
		Settinginit();

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainLable.class.getResource("view/MainLayout.fxml"));
			mainLayout = (AnchorPane) loader.load();

			setMainLayout();

			Scene scene = new Scene(mainLayout);
			scene.setFill(Color.TRANSPARENT);

			// The controller
			MainController mainController = loader.getController();
			mainController.setMainController(this);

			this.primaryStage.setScene(scene);
			ResizeHelper.addResizeListener(this.primaryStage);

			this.primaryStage.setAlwaysOnTop(isAlwaysTop);
			this.primaryStage.initStyle(StageStyle.TRANSPARENT);
			this.primaryStage.getIcons().add(new Image(MainLable.class.getResourceAsStream("usagilogo.png")));
			this.primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void Settinginit() {
		String path = new File(".").getAbsolutePath();//"D:\\Program Files\\eclipse_mars\\workspace\\AlwaysOnTopNote\\build\\dist\\±„«©DeftMeiko∞Êv1\\";//
		path = path.substring(0, path.lastIndexOf("\\")) + "\\";
		if (new File(path + "init.properties").exists()) {
			String[] result = {"=","=","=","=","=","=","=","="};
			BufferedReader bReader;
			try {
				InputStreamReader inputstr = new InputStreamReader(
						new FileInputStream(new File(path + "init.properties")), "UTF-8");// Œ™¡À±‹√‚∂¡»°UTF-8≥ˆœ÷¬“¬Î
				bReader = new BufferedReader(inputstr);
				String line;
				int i = 0;
				while ((line = bReader.readLine()) != null) {
					result[i++] = line;
				}
				bReader.close();

				isStartAtLogon = result[0].contains("true");
				isAlwaysTop = result[1].contains("true");

				this.primaryStage.setX(Double.valueOf(result[2].substring(result[2].indexOf("=") + 1)));
				this.primaryStage.setY(Double.valueOf(result[3].substring(result[3].indexOf("=") + 1)));

				this.primaryStage.setWidth(Double.valueOf(result[4].substring(result[4].indexOf("=") + 1)));
				this.primaryStage.setHeight(Double.valueOf(result[5].substring(result[5].indexOf("=") + 1)));

				MainController.fontFamily = result[6].substring(result[6].indexOf("=") + 1);
				MainController.fontSize = Double.valueOf(result[7].substring(result[7].indexOf("=") + 1));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void changeStart(boolean isStartAtLogon) {
		// String regKey =
		// "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
		String regKey = "HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
		String myAppName = "±„«©DeftMeiko∞Êv1";

		try {
			String path = new File(".").getAbsolutePath();
			// path = java.net.URLDecoder.decode(path, "UTF-8");
			path = "\"" + path.substring(0, path.lastIndexOf("\\")) + "\\±„«©DeftMeiko∞Êv1.exe\"";
			System.out.println(path);

			Runtime.getRuntime().exec("reg " + (isStartAtLogon ? "add " : "delete ") + regKey + " /v " + myAppName
					+ (isStartAtLogon ? " /t reg_sz /d " + path : " /f"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setMainLayout() {
		// TODO Auto-generated method stub
		final ContextMenu contextMenu = new ContextMenu();
		MenuItem cut = new MenuItem("…Ë÷√");
		contextMenu.getItems().add(cut);
		cut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				tempIsOnTop = isAlwaysTop;
				tempisStartAtLogon = isStartAtLogon;
				SettingDialog();
				ResponseSettings();
			}
		});

		MenuItem shot = new MenuItem("Ωÿ∆¡");
		contextMenu.getItems().add(shot);
		shot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ScreenShotDialog();
			}
		});

		mainLayout.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					contextMenu.show(mainLayout, event.getScreenX(), event.getScreenY());
				} else {
					contextMenu.hide();
				}
			}
		});
	}

	private void ResponseSettings() {
		if (tempIsOnTop != isAlwaysTop) {
			primaryStage.setAlwaysOnTop(isAlwaysTop);
		}
		if (tempisStartAtLogon != isStartAtLogon) {
			changeStart(isStartAtLogon);
		}
	}

	private double xOffset = 0;
	private double yOffset = 0;

	// Set Time Stage
	private String SettingDialog() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainLable.class.getResource("view/SettingLayout.fxml"));
			AnchorPane settingPage = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("…Ë÷√");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);

			settingPage.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					xOffset = event.getSceneX();
					yOffset = event.getSceneY();
				}
			});
			settingPage.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					dialogStage.setX(event.getScreenX() - xOffset);
					dialogStage.setY(event.getScreenY() - yOffset);
				}
			});

			Scene scene = new Scene(settingPage);

			dialogStage.initStyle(StageStyle.UNDECORATED);
			dialogStage.setScene(scene);
			dialogStage.setX(primaryStage.getX() + 45);
			dialogStage.setY(primaryStage.getY() + 45);

			SettingController controller = loader.getController();
			controller.setMainController(this);
			controller.setDialogStage(dialogStage);

			dialogStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void ScreenShotDialog() {
		try {

			try {
				BufferedImage screencapture = new Robot()
						.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				// Save as PNG
				File file = new File("screencapture.png");
				ImageIO.write(screencapture, "png", file);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainLable.class.getResource("view/ScreenShotLayout.fxml"));
			AnchorPane screenshotPage = (AnchorPane) loader.load();

			Scene scene = new Scene(screenshotPage);
			scene.setFill(Color.TRANSPARENT);

			Stage dialogStage = new Stage();
			dialogStage.setScene(scene);
			dialogStage.initStyle(StageStyle.TRANSPARENT);

			dialogStage.setX(0);
			dialogStage.setY(0);
			dialogStage.setAlwaysOnTop(isAlwaysTop);

			ScreenShotController controller = loader.getController();
			controller.setMainController(this);
			controller.setDialogStage(dialogStage);

			dialogStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
