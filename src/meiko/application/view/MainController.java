package meiko.application.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import meiko.application.MainLable;

public class MainController {

	private MainLable mainLable;

	@FXML
	private ImageView topimageview;
	@FXML
	private TextArea contentArea;

	private Image normalImage = new Image(MainController.class.getResourceAsStream("meiko001.png"));
	private Image moveImage = new Image(MainController.class.getResourceAsStream("meiko002.png"));

	private boolean isTexting = true;

	public void setMainController(MainLable mainLable) {
		// TODO Auto-generated constructor stub
		this.mainLable = mainLable;
	}

	public static String fontFamily;
	public static double fontSize;

	@FXML
	private void initialize() {
		topimageview.setImage(normalImage);
		setImage();

		contentArea.setFont(Font.font(fontFamily,fontSize));
		String path = new File(".").getAbsolutePath();//"D:\\Program Files\\eclipse_mars\\workspace\\AlwaysOnTopNote\\build\\dist\\±„«©DeftMeiko∞Êv1\\";//
		path = path.substring(0,path.lastIndexOf("\\"))+"\\";
		if (new File(path+"setting.properties").exists()) {
			String result = "";
			BufferedReader bReader;
			try {
				InputStreamReader inputstr = new InputStreamReader(new FileInputStream(new File(path+"setting.properties")),
						"UTF-8");// Œ™¡À±‹√‚∂¡»°UTF-8≥ˆœ÷¬“¬Î
				bReader = new BufferedReader(inputstr);
				String line;
				while ((line = bReader.readLine()) != null) {
					result = result + line + "\n";
				}
				contentArea.setText(result);
				bReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		contentArea.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (isTexting) {
					Thread thread = new Thread(runnable);
					thread.start();
				}
				isTexting = false;
			}
		});
	}

	final long timeInterval = 30000;
	Runnable runnable = new Runnable() {
		public void run() {
			try {
				Thread.sleep(timeInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isTexting = true;
			// ------- code for task to run
			recodeContent();
			// ------- ends here
		}
	};

	private void setImage() {
		topimageview.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				topimageview.setImage(moveImage);
			}
		});
		topimageview.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				topimageview.setImage(normalImage);
			}
		});
	}

	@FXML
	private void handleClose() {
		recodeContent();
		recodeSettings(mainLable.getPrimaryStage().getX(), mainLable.getPrimaryStage().getY(),
				mainLable.getPrimaryStage().getWidth(), mainLable.getPrimaryStage().getHeight());
		mainLable.getPrimaryStage().close();
		System.exit(0);
	}

	public void recodeSettings(double positionX, double positionY, double noteWidth, double noteHeight) {
		try {
			PrintWriter writer2;
			writer2 = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(new File("init.properties")), "UTF-8"));
			if (MainLable.isStartAtLogon())
				writer2.append("isStartAtLogon=true\n");
			else
				writer2.append("isStartAtLogon=false\n");

			if (MainLable.isAlwaysTop())
				writer2.append("isAlwaysTop=true\n");
			else
				writer2.append("isAlwaysTop=false\n");

			writer2.append("positionX=" + positionX + "\n");
			writer2.append("positionY=" + positionY + "\n");

			writer2.append("noteWidth=" + noteWidth + "\n");
			writer2.append("noteHeight=" + noteHeight + "\n");

			writer2.append("fontFamily=" + fontFamily + "\n");
			writer2.append("fontSize=" + fontSize + "\n");

			writer2.flush();
			writer2.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void recodeContent() {
		try {
			PrintWriter writer;
			writer = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(new File("setting.properties")), "UTF-8"));
			writer.append(contentArea.getText());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
