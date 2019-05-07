package com.cronoteSys.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.cronoteSys.model.bo.ActivityBO;
import com.cronoteSys.model.bo.ExecutionTimeBO;
import com.cronoteSys.model.vo.ActivityVO;
import com.cronoteSys.model.vo.StatusEnum;
import com.cronoteSys.util.ActivityMonitor;
import com.cronoteSys.util.ActivityMonitor.OnMonitorTick;
import com.cronoteSys.util.SessionUtil;

import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ActivityCardController extends ListCell<ActivityVO> implements ShowEditViewActivityObservableI {
	@FXML
	private AnchorPane cardRoot;
	@FXML
	private Label lblTitle;
	@FXML
	private Label lblStatus;
	@FXML
	private ProgressBar pgbProgress;
	@FXML
	private Label lblProgress;
	@FXML
	private Label lblIndex;
	@FXML
	private Button btnDelete;
	@FXML
	private Button btnPlayPause;
	@FXML
	private Button btnFinalize;
	private ActivityVO activity;


	{
		FXMLLoader loader = SessionUtil.getInjector().getInstance(FXMLLoader.class);
		try {
			loader.setLocation(new File(getClass().getResource("/fxml/Templates/ActivityCard.fxml").getPath())
					.toURI().toURL());
			loader.setController(this);
			loader.load();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void updateSelected(boolean selected) {
		super.updateSelected(selected);
		HashMap<String, Object> hmp = new HashMap<String, Object>();
		if (activity.getId() == getListView().getSelectionModel().getSelectedItem().getId()) {
			System.out.println("aaa");
			cardRoot.getStyleClass().add("activityCardSelected");
			hmp.put("action", "view");

			hmp.put("activity", activity);
			hmp.put("project", activity.getProjectVO());
			System.out.println(true);
		} else {
			cardRoot.getStyleClass().removeAll("activityCardSelected");
			hmp.put("action", "close");
			System.out.println(false);
		}
		notifyAllListeners(hmp);

	}

	@Override
	public void updateItem(ActivityVO item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null || !empty) {
			initEvents();	
			setGraphic(cardRoot);
			activity = item;
			loadActivity();
			loadProgressAndRealtime();
		

		} else {
			setText(null);
			setGraphic(null);
		}
		setStyle("-fx-background-color:transparent;-fx-prefwidth:100%;");
	}

	private void loadActivity() {
		lblTitle.setText(activity.getTitle());

		loadProgressAndRealtime();
		GlyphIcon icon = null;
		String btnText = "";
		if (activity.getStats() != StatusEnum.NORMAL_FINALIZED && activity.getStats() != StatusEnum.BROKEN_FINALIZED) {
			if (activity.getStats() == StatusEnum.NOT_STARTED || activity.getStats() == StatusEnum.NORMAL_PAUSED
					|| activity.getStats() == StatusEnum.BROKEN_PAUSED) {

				icon = new FontAwesomeIconView(FontAwesomeIcon.PLAY_CIRCLE_ALT);
				btnText = "play";
				btnFinalize.getStyleClass().removeAll("show");
				icon.setSize("2em");
			} else if (activity.getStats().equals(StatusEnum.BROKEN_IN_PROGRESS)
					|| activity.getStats().equals(StatusEnum.NORMAL_IN_PROGRESS)) {

				icon = new FontAwesomeIconView(FontAwesomeIcon.PAUSE_CIRCLE_ALT);
				btnText = "pause";
				btnFinalize.getStyleClass().add("show");
				icon.setSize("2em");
			}
			btnPlayPause.setGraphic(icon);
			btnPlayPause.setText(btnText);
			btnPlayPause.getStyleClass().add("show");
		}
		StackPane sp = (StackPane) pgbProgress.lookup(".bar");
		if (sp != null)
			sp.setStyle("-fx-background-color:" + activity.getStats().getHexColor());
	}

	private void loadProgressAndRealtime() {
		lblStatus.setText(activity.getStats().getDescription());
		Node bar = pgbProgress.lookup(".bar");
		if (bar != null)
			bar.setStyle("-fx-background-color:" + activity.getStats().getHexColor());
		double estimatedTime = activity.getEstimatedTime().toMillis();
		double realtime = activity.getRealtime().toMillis();
		double progress = realtime / estimatedTime;
		double difference = progress - 1;
		if (difference != 0) {
			FontAwesomeIconView icon = new FontAwesomeIconView();
			Tooltip tp = new Tooltip();
			if (difference > 0) {
				if (difference <= 0.75) {
					icon = new FontAwesomeIconView(FontAwesomeIcon.ANGLE_DOWN);
				} else {
					icon = new FontAwesomeIconView(FontAwesomeIcon.ANGLE_DOUBLE_DOWN);
					icon.setSize("1.5em");
				}
				icon.setFill(Color.RED);
				tp.setText("Percentual de tempo excedido");
				lblIndex.setStyle("-fx-text-fill:red");
			} else {
				if (difference >= -0.35) {
					icon = new FontAwesomeIconView(FontAwesomeIcon.ANGLE_UP);
				} else {
					icon = new FontAwesomeIconView(FontAwesomeIcon.ANGLE_DOUBLE_UP);
					icon.setSize("1.5em");
				}
				icon.setFill(Color.GREEN);
				tp.setText("Percentual de tempo desnecessário");
				lblIndex.setStyle("-fx-text-fill:green");

			}
			lblIndex.getStyleClass().removeAll("hide");
			lblIndex.setText(String.format("%.2f", Math.abs((difference * 100))) + "%");
			lblIndex.setGraphic(icon);
			Tooltip.install(lblIndex, tp);

		}
		progress = progress > 1 ? 1 : progress;
		if (activity.getStats() == StatusEnum.BROKEN_FINALIZED || activity.getStats() == StatusEnum.NORMAL_FINALIZED)
			pgbProgress.setProgress(1);
		else
			pgbProgress.setProgress(progress);
		String progressStr = String.format("%.2f", Math.abs((pgbProgress.getProgress() * 100)));
		lblProgress.setText(progressStr + "%");
		notifyAllOnProgressChangedListeners(activity);
	}

	private void initEvents() {

		btnDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new ActivityBO().delete(activity);
				getListView().getItems().remove(activity);

			}
		});

		btnPlayPause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ExecutionTimeBO execBo = new ExecutionTimeBO();
				ActivityBO actBo = new ActivityBO();
				if (btnPlayPause.getText().equalsIgnoreCase("play")) {
					if (execBo.startExecution(activity) != null) {
						activity = actBo.switchStatus(activity, StatusEnum.NORMAL_IN_PROGRESS);
						if (btnDelete.isVisible()) {
							btnDelete.getStyleClass().remove("show");
						}
						ActivityMonitor.addActivity(activity);
					}
				} else {
					if (execBo.finishExecution(activity) != null) {
						activity = actBo.switchStatus(activity, StatusEnum.NORMAL_PAUSED);
						ActivityMonitor.removeActivity(activity);
					}
				}
				loadActivity();

			}
		});
		btnFinalize.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ExecutionTimeBO execBo = new ExecutionTimeBO();
				ActivityBO actBo = new ActivityBO();

				if (execBo.finishExecution(activity) != null) {
					activity = actBo.switchStatus(activity, StatusEnum.NORMAL_FINALIZED);
					btnPlayPause.getStyleClass().removeAll("show");
					btnFinalize.getStyleClass().removeAll("show");
					loadActivity();

					ActivityMonitor.removeActivity(activity);
				}

			}
		});
		setOnMouseEntered(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				if (activity.getStats().equals(StatusEnum.NOT_STARTED)) {
					btnDelete.getStyleClass().add("show");
				}

			}
		});
		setOnMouseExited(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				btnDelete.getStyleClass().removeAll("show");

			}
		});

		pgbProgress.skinProperty().addListener(new ChangeListener<Skin>() {

			@Override
			public void changed(ObservableValue<? extends Skin> observable, Skin oldValue, Skin newValue) {
				paintBar(pgbProgress.lookup(".bar"));

			}
		});
		pgbProgress.progressProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				paintBar(pgbProgress.lookup(".bar"));

			}
		});

		ActivityMonitor.addOnMonitorTickListener(new OnMonitorTick() {

			@Override
			public void onMonitorTicked(ActivityVO act) {
				if (act.getId() == activity.getId())
					activity = act;
				loadProgressAndRealtime();

			}
		});
	}

	private void paintBar(Node node) {
		if (node != null)
			node.setStyle("-fx-background-color:" + activity.getStats().getHexColor());
	}

	private static ArrayList<OnProgressChangedI> activityAddedListeners = new ArrayList<OnProgressChangedI>();

	public interface OnProgressChangedI {
		void onProgressChangedI(ActivityVO act);
	}

	public static void addOnActivityAddedIListener(OnProgressChangedI newListener) {
		activityAddedListeners.add(newListener);
	}

	private void notifyAllOnProgressChangedListeners(ActivityVO act) {
		for (OnProgressChangedI l : activityAddedListeners) {
			l.onProgressChangedI(act);
		}
	}

	@Override
	public void notifyAllListeners(HashMap<String, Object> hmp) {
		for (ShowEditViewActivityObserverI l : listeners) {
			l.showEditViewActivity(hmp);
		}

	}

}
