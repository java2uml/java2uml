package com.github.java2uml.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class DiagramViewer extends JFrame {

	private static DiagramViewer instance = null;
	
	private final JFXPanel jfxPanel = new JFXPanel();
	private WebEngine engine;
	private WebView view;
	private Slider slider;
	
	private final JPanel panel		= new JPanel(new BorderLayout());
	private final JLabel lblStatus 	= new JLabel();
	
	private final JButton btnGo 		= new JButton("Go");
	private final JButton btnZoomIn 	= new JButton("+");
	private final JButton btnZoomOut 	= new JButton("-");
	private final JLabel lbZoom 		= new JLabel("100%");
	private final JLabel lbStatus		= new JLabel();
	private final JTextField txtURL 	= new JTextField();
	private final JProgressBar progressBar = new JProgressBar();
	
	private static double zoom = 1.0;
	private static int zoomVal = 100;
	
	private final String tmplHeader	= "<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\"><body>";
	private final String tmplFooter = "</body></html>";
	
	protected DiagramViewer() {
		super();
		initComponents();
	}
	
	/**
	 * Получение экземпляра просмоторщика
	 * @return
	 */
	public static synchronized DiagramViewer getInstance() {
		if (instance == null) {
			instance = new DiagramViewer();
		}
		return instance; 
	}
	
	private void initComponents() {
		createScene();

		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "go":
					loadDiagram(txtURL.getText());
					break;
				case "zoom_in":
					zoom = (zoom >= 4.0) ? 4.0 : zoom + 0.25;
					lbZoom.setText((int)(zoom*100)+"%"); 
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							view.getEngine().executeScript("document.body.style.zoom=" + zoom);
						}
					});
					break;
				case "zoom_out":
					zoom = (zoom <= 0.25) ? 0.25 : zoom - 0.25;
					lbZoom.setText((int)(zoom*100)+"%");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							view.getEngine().executeScript("document.body.style.zoom=" + zoom);
						}
					});
					break;
				}

			}
		};

		Font font = new Font("Arial", Font.BOLD, 14);
		btnGo.setActionCommand("go");
		btnGo.setVisible(false);
		btnZoomIn.setActionCommand("zoom_in");
		btnZoomOut.setActionCommand("zoom_out");
		txtURL.setActionCommand("go");
		txtURL.setEnabled(false);

		
		btnZoomIn.setFont(font);
		btnZoomOut.setFont(font);
		btnGo.setFont(font);

		//btnGo.addActionListener(al);
		//txtURL.addActionListener(al);
		btnZoomIn.addActionListener(al);
		btnZoomOut.addActionListener(al);
		
		progressBar.setPreferredSize(new Dimension(150, 18));
		progressBar.setStringPainted(true);

		JPanel topBar = new JPanel(new BorderLayout(5, 0));
		topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		topBar.add(txtURL, BorderLayout.CENTER);
		JPanel toolPnl = new JPanel();
		toolPnl.add(btnZoomIn);
		toolPnl.add(btnZoomOut);
		toolPnl.add(lbZoom);
		toolPnl.add(btnGo);
		toolPnl.setLayout(new BoxLayout(toolPnl, BoxLayout.X_AXIS));
		topBar.add(toolPnl, BorderLayout.EAST);
		
		JPanel statusBar = new JPanel(new BorderLayout(5, 0));
		statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		statusBar.add(lblStatus, BorderLayout.CENTER);
		statusBar.add(progressBar, BorderLayout.EAST);

		panel.add(topBar, BorderLayout.NORTH);
		panel.add(jfxPanel, BorderLayout.CENTER);
		panel.add(statusBar, BorderLayout.SOUTH);
		
		getContentPane().add(panel);

		setPreferredSize(new Dimension(1024, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}

	private void createScene() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				view	= new WebView();
				view.setContextMenuEnabled(false);
				engine 	= view.getEngine();
				engine.titleProperty().addListener(
						new ChangeListener<String>() {
							@Override
							public void changed(
									ObservableValue<? extends String> observable,
									String oldValue, final String newValue) {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										DiagramViewer.this.setTitle(newValue);
									}
								});
							}
						});

				engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
					@Override
					public void handle(final WebEvent<String> event) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								lblStatus.setText(event.getData());
							}
						});
					}
				});

//				engine.locationProperty().addListener(
//						new ChangeListener<String>() {
//							@Override
//							public void changed(
//									ObservableValue<? extends String> ov,
//									String oldValue, final String newValue) {
//								SwingUtilities.invokeLater(new Runnable() {
//									@Override
//									public void run() {
//										txtURL.setText(newValue);
//									}
//								});
//							}
//						});

				engine.getLoadWorker().workDoneProperty()
						.addListener(new ChangeListener<Number>() {
							@Override
							public void changed(
									ObservableValue<? extends Number> observableValue,
									Number oldValue, final Number newValue) {
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										progressBar.setValue(newValue
												.intValue());
									}
								});
							}
						});

				engine.getLoadWorker().exceptionProperty()
						.addListener(new ChangeListener<Throwable>() {
							public void changed(
									ObservableValue<? extends Throwable> o,
									Throwable old, final Throwable value) {
								if (engine.getLoadWorker().getState() == State.FAILED) {
									SwingUtilities.invokeLater(new Runnable() {
										@Override
										public void run() {
											JOptionPane
													.showMessageDialog(
															panel,
															(value != null) ? engine
																	.getLocation()
																	+ "\n"
																	+ value.getMessage()
																	: engine.getLocation()
																			+ "\nUnexpected error.",
															"Loading error...",
															JOptionPane.ERROR_MESSAGE);
										}
									});
								}
							}
						});
				jfxPanel.setScene(new Scene(view));
			}
		});
	}
	
	/**
	 * Приведение строки пути к URL формату
	 * @param str
	 * @return
	 */
	private static String toURL(String str) {
		try {
			return new URL(str).toExternalForm();
		} catch (MalformedURLException exception) {
			return null;
		}
	}

	/**
	 * Загрузка файла диаграммыв в окно просмоторщика
	 */
	public void loadDiagram(final String uri) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (uri != null) {
					engine.load(uri);
				}
			}
		});
	}
	
	/**
	 * Загрузка содержимого диаграммыв в окно просмоторщика
	 */
	public void loadDiagramContent(final String content) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (content != null) {
					String res = tmplHeader + content + tmplFooter;
					engine.loadContent(res);
				}
			}
		});
	}
	
	/**
	 * Адрес диаграммы в адресной строке
	 */
	public void setURLField(final String url) {
		txtURL.setText(url);
	}
	
	/**
	 * Установка статуса
	 */
	public void setViewerStatus(final String text) {
		lblStatus.setText(text);
	}
	
	private static String getError(final String error) {
		return "<html><font color='red'> " + error + "</font></html>";
	}

	/**
	 * Показ диаграмы в просмотрощике
	 * @param path
	 */
	public static void show(final String path) {
		// проверка наличия файла
		File file = new File(path);
		try {
			if (!Files.exists(Paths.get(path))) {
				instance.setVisible(true);
				instance.setViewerStatus(getError("Error! Diagram doesn't exist!")); return;
			}
		} catch(Exception e) {
			e.printStackTrace();
			instance.setVisible(true);
			instance.setViewerStatus(getError("Error! Invalid diagram path!")); return;
		}
		
		// проверка формата диаграммы
		if (file.getName().matches("[a-zA-Z0-9]+\\.png$")) {
			// загружаем как png
			instance.setVisible(true);
			instance.loadDiagram(file.toURI().toString());
			instance.setURLField(path);
		} else if (file.getName().matches("[a-zA-Z0-9]+\\.svg$")) {
			// загружаем как svg
			try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
				StringBuilder buf = new StringBuilder();
				String line = null;
				while((line = reader.readLine()) != null) {
					buf.append(line);
					buf.append("\n");
				}
				// из строки извлекаем только содержимое тега <svg></svg> (иначе диаграмму не отобразить)
				int tagStart	= buf.toString().indexOf("<svg");
				int tagEnd 		= buf.toString().indexOf("</svg>");
				if (tagStart > -1 && tagEnd > -1) {
					String svg = buf.toString().substring(tagStart, tagEnd + "</svg>".length());
					instance.setVisible(true);
					instance.loadDiagramContent(svg);
					instance.setURLField(path);
				}
			} catch(IOException e) {
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			// неверный формат - на загружаем
			instance.setVisible(true);
			instance.setViewerStatus(getError("Error! Wrong diagram format!"));
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//String tmp = "http://oracle.com";
				//String tmp = "C:/Apache2_2/htdocs/eclipse/java2uml_balyschev/classes.png";
				String tmp = "/Users/mac/Desktop/java2UML/diagram.svg";
				DiagramViewer.getInstance().show(tmp);
			}
		});
	}
}