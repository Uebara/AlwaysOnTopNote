package meiko.application.view;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import meiko.application.MainLable;

public class ScreenShotController {

	private MainLable mainLable;
	private Stage dialogStage;
	private int startX, startY, endX, endY;
	private boolean isCut = false;

	private double xOffset = 0;
	private double yOffset = 0;

	private final ContextMenu contextMenu = new ContextMenu();

	@FXML
	private ImageView showImage;

	public void setMainController(MainLable mainLable) {
		// TODO Auto-generated constructor stub
		this.mainLable = mainLable;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	@FXML
	private void initialize() {
		File file = new File("screencapture.png");
		display(file);
		handleClose();
	}

	private void display(File file) {
		// TODO Auto-generated method stub
		try {
			Image putimage = new Image(file.toURI().toURL().toString());
			showImage.setImage(putimage);

			imageViewListeners();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void imageViewListeners() {
		// TODO Auto-generated method stub
		showImage.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					contextMenu.show(showImage, event.getScreenX(), event.getScreenY());
				} else {
					contextMenu.hide();
				}
				if (!isCut) {
					startX = (int) event.getScreenX();
					startY = (int) event.getScreenY();
				} else {
					xOffset = dialogStage.getX() - event.getScreenX();
					yOffset = dialogStage.getY() - event.getScreenY();
				}
			}
		});

		showImage.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!isCut) {
					endX = (int) event.getScreenX();
					endY = (int) event.getScreenY();
					// System.out.println(endX + "--" + endY);
					if (endX - startX > 0) {
						BufferedImage screencapture;
						try {
							screencapture = new Robot()
									.createScreenCapture(new Rectangle(startX, startY, endX - startX, endY - startY));
							File file = new File("screencapture.png");
							ImageIO.write(screencapture, "png", file);
							isCut = true;
							display(file);
						} catch (IOException | AWTException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		showImage.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (isCut) {
					dialogStage.setX(event.getScreenX() + xOffset);
					dialogStage.setY(event.getScreenY() + yOffset);
				}
			}
		});
	}

	private void handleClose() {
		// TODO Auto-generated method stub
		MenuItem cut = new MenuItem("¹Ø±Õ");
		contextMenu.getItems().add(cut);
		cut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialogStage.close();
			}
		});
	}

	// @FXML
	// private void handleClose() {
	// dialogStage.close();
	// }
}
