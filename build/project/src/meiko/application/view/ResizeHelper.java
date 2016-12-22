package meiko.application.view;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ResizeHelper {

	public static void addResizeListener(Stage stage) {
		ResizeListener resizeListener = new ResizeListener(stage);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_RELEASED, resizeListener);
		ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
		for (Node child : children) {
			addListenerDeeply(child, resizeListener);
		}
	}

	public static void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {
		node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
		node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
		node.addEventHandler(MouseEvent.MOUSE_EXITED, listener);
		node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener);
		node.addEventHandler(MouseEvent.MOUSE_RELEASED, listener);
		if (node instanceof Parent) {
			Parent parent = (Parent) node;
			ObservableList<Node> children = parent.getChildrenUnmodifiable();
			for (Node child : children) {
				addListenerDeeply(child, listener);
			}
		}
	}

	static class ResizeListener implements EventHandler<MouseEvent> {
		private Stage stage;
		private Cursor cursorEvent = Cursor.DEFAULT;
		private int border = 6;
		private int minSize = 116;
		private double startX = 0;
		private double startY = 0;

		private double xOffset = 0;
		private double yOffset = 0;

		private boolean isDragging = true;

		public ResizeListener(Stage stage) {
			this.stage = stage;
		}

		@Override
		public void handle(MouseEvent mouseEvent) {
			EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
			Scene scene = stage.getScene();

			double mouseEventX = mouseEvent.getSceneX(), mouseEventY = mouseEvent.getSceneY(),
					sceneWidth = scene.getWidth(), sceneHeight = scene.getHeight();

			if (MouseEvent.MOUSE_MOVED.equals(mouseEventType) == true) {
				if (mouseEventX < border && mouseEventY < border) {
					cursorEvent = Cursor.NW_RESIZE;
				} else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
					cursorEvent = Cursor.SW_RESIZE;
				} else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
					cursorEvent = Cursor.NE_RESIZE;
				} else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
					cursorEvent = Cursor.SE_RESIZE;
				} else if (mouseEventX < border) {
					cursorEvent = Cursor.W_RESIZE;
				} else if (mouseEventX > sceneWidth - border) {
					cursorEvent = Cursor.E_RESIZE;
				} else if (mouseEventY < border) {
					cursorEvent = Cursor.N_RESIZE;
				} else if (mouseEventY > sceneHeight - border) {
					cursorEvent = Cursor.S_RESIZE;
				} else {
					cursorEvent = Cursor.DEFAULT;
				}
				scene.setCursor(cursorEvent);
			} else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType)
					|| MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
				scene.setCursor(Cursor.DEFAULT);
			} else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType) == true) {
				// resize window setting
				startX = stage.getWidth() - mouseEventX;
				startY = stage.getHeight() - mouseEventY;
				// drag window setting
				xOffset = stage.getX() - mouseEvent.getScreenX();
				yOffset = stage.getY() - mouseEvent.getScreenY();
			} else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) == true) {
				if ((stage.getHeight() - startY) < 50
						&& (6 < (stage.getWidth() - startX) && (stage.getWidth() - startX) < (stage.getWidth() - 6))) {
					stage.setX(mouseEvent.getScreenX() + xOffset);
					stage.setY(mouseEvent.getScreenY() + yOffset);
				} else if (Cursor.DEFAULT.equals(cursorEvent) == false) {
					if (Cursor.W_RESIZE.equals(cursorEvent) == false && Cursor.E_RESIZE.equals(cursorEvent) == false) {
						double minHeight = stage.getMinHeight() > (minSize) ? stage.getMinHeight() : (minSize);
						if (Cursor.NW_RESIZE.equals(cursorEvent) == true || Cursor.N_RESIZE.equals(cursorEvent) == true
								|| Cursor.NE_RESIZE.equals(cursorEvent) == true) {
							if (stage.getHeight() > minHeight || mouseEventY < 0) {
								stage.setHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight());
								stage.setY(mouseEvent.getScreenY());
							}
						} else {
							if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
								stage.setHeight(mouseEventY + startY);
							}
						}
					}

					if (Cursor.N_RESIZE.equals(cursorEvent) == false && Cursor.S_RESIZE.equals(cursorEvent) == false) {
						double minWidth = stage.getMinWidth() > (minSize * 2) ? stage.getMinWidth() : (minSize * 2);
						if (Cursor.NW_RESIZE.equals(cursorEvent) == true || Cursor.W_RESIZE.equals(cursorEvent) == true
								|| Cursor.SW_RESIZE.equals(cursorEvent) == true) {
							if (stage.getWidth() > minWidth || mouseEventX < 0) {
								stage.setWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
								stage.setX(mouseEvent.getScreenX());
							}
						} else {
							if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
								stage.setWidth(mouseEventX + startX);
							}
						}
					}
				}
				if (isDragging) {
					Thread thread = new Thread(runnable);
					thread.start();
				}
				isDragging = false;
			} else if (MouseEvent.MOUSE_RELEASED.equals(mouseEventType)) {
				scene.setCursor(Cursor.DEFAULT);
				if (stage.getY() < 0) {
					stage.setY(0);
				}
			}
		}

		final long timeInterval = 30000;
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					Thread.sleep(timeInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				isDragging = true;
				// ------- code for task to run
				MainController mainController = new MainController();
				mainController.recodeSettings(stage.getX(),stage.getY(),stage.getWidth(),stage.getHeight());
				// ------- ends here
			}
		};
	}
}
