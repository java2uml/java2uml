/**
 * @author Alexey Balyschev - alexbalu-alpha7@mail.ru
 * @class PackageSelectorDialog
 * Класс дерева для выбора пакетов
 * Использует:	Options.path - путь до источника данных  
 * Изменяет:	Options.packages - пакеты для генерации
 */

package com.github.java2uml.gui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.java2uml.core.Options;

public class PackageDialog {
	
private static Logger LOG = LoggerFactory.getLogger("LOG: ");

private static JDialog dialog;
//private static final ImageIcon treeBtnImg = new ImageIcon("res/tree-view-light.png");
//private JButton openTreeBtn;
private JFrame context;
private String path = Options.getPath();
private PackageListPnl packageListPnl;
private static volatile boolean isLoaded = false;

public PackageDialog(final JFrame context) {
	this.context = context;
    //setBounds(500, 200, 500, 700);
    //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //setVisible(true);
    //setLayout(new FlowLayout());
//    openTreeBtn = new JButton(new ImageIcon(treeBtnImg.getImage().getScaledInstance(20, 16, Image.SCALE_SMOOTH)));
//    openTreeBtn.setSize(20,  20);
//    openTreeBtn.setFocusPainted(false);
//    openTreeBtn.setContentAreaFilled(false);
//    openTreeBtn.addActionListener(new ActionListener() 
//    {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            showDialog();
//        }
//    });
    //add(openTreeBtn);
}

//public JButton getOpenTreeButton() {
//	return this.openTreeBtn;
//}
//
//public void setEnabled(final boolean val) {
//	this.openTreeBtn.setEnabled(val);
//}

public void updatePath() {
	this.path = Options.getPath();
}

public static boolean isLoaded() {
	return isLoaded;
}

public void initDialog() {
    dialog = new JDialog(context, Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setTitle("Выбор пакетов для генерации");
    
    packageListPnl = new PackageListPnl();
    dialog.add(packageListPnl);
    dialog.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
            dialog.setVisible(false);
        }
    });
    
    dialog.setBounds(550, 250, 400, 600);
    dialog.setVisible(false);
}

public void showDialog() {
	if (isLoaded && dialog != null) {
		dialog.setVisible(true);
	}
}

//-------------------------------------------------------------------------------------

	private static class PackageListPnl extends JPanel {
		private JButton okBtn;
		private JButton cnclBtn;
		private JPanel rootPnl;
		private JPanel controlPnl;
		private DefaultListModel listModel;
		private JList packageList;
		private JCheckBox allCbx;
		private JLabel tipMultLbl;
		private JLabel tipOnceLbl;
		
		
		public static final String ROOT_PACKAGE = ">";
	
		public PackageListPnl() {
			super(new GridLayout(1, 0));
	
			LOG.info("Create list model...");
			
			// модель для списка создаем в отдельном потоке
		    listModel = new DefaultListModel();
		    Thread thread = new Thread(new Runnable() {
		    	@Override
		    	public void run() {
		    		createPackageListModel();
		    	}
		    });
		    thread.start();
		    
		    // создаем список пакетов
		    packageList = new JList(listModel);    
		    packageList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	
		    
			// панель для скролла
			JScrollPane treeView = new JScrollPane(packageList);
			
			// элементы управления
			allCbx = new JCheckBox("Все пакеты");
			allCbx.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					ListSelectionModel model = PackageListPnl.this.packageList.getSelectionModel();
					if (allCbx.isSelected()) {
						model.setSelectionInterval(0, PackageListPnl.this.listModel.size()-1); 
					} else {
						model.clearSelection();
					}
				}
			});
			tipMultLbl = new JLabel("<html><p><b>Shift + ЛКМ</b></p></html>", SwingConstants.CENTER);
			tipMultLbl.setToolTipText("Выбор нескольких пакетов");
			tipOnceLbl = new JLabel("<html><p><b>Ctrl + ЛКМ</b></p></html>", SwingConstants.CENTER);
			tipOnceLbl.setToolTipText("Выбор отдельных пакетов");
			
			// размеры дерева
			Dimension preferedSize = new Dimension(300, 600);
			this.setPreferredSize(preferedSize);
	
			// кнопки выбора
			cnclBtn = new JButton("Cancel");
			cnclBtn.addActionListener(new ActionListener() 
		    {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		        	if (PackageDialog.dialog != null) {
		        		PackageDialog.dialog.setVisible(false);
		        	}     
		        }
		    });
			okBtn = new JButton("  OK  ");
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					Set<String> packs = new HashSet<>();
					List<Object> packsList = getSelectedPackages();
					
					for (Object obj : packsList) {
						if (obj instanceof String) {
							String val = (String)obj; 
							if (val.equals(ROOT_PACKAGE)) {
								val = "";
							}
							packs.add(val);
						}
					}
					
					Options.setPackages(packs);
					if (PackageDialog.dialog != null) {
		        		PackageDialog.dialog.setVisible(false);
		        	}
				}
			});
			
			// панель управления
			controlPnl = new JPanel();
			controlPnl.setLayout(new GridLayout(1, 3));
			controlPnl.setMaximumSize(new Dimension(400, 50));
			controlPnl.add(allCbx);
			controlPnl.add(tipMultLbl);
			controlPnl.add(tipOnceLbl);
			
			JPanel submitPnl = new JPanel();
			submitPnl.setMaximumSize(new Dimension(400, 50));
			submitPnl.add(okBtn);
			submitPnl.add(Box.createHorizontalStrut(30));
			submitPnl.add(cnclBtn);
			submitPnl.setLayout(new GridLayout(1, 3));
			
			// корневая панель
			rootPnl = new JPanel();
			rootPnl.setLayout(new BoxLayout(rootPnl, BoxLayout.Y_AXIS));
			rootPnl.add(treeView);
			rootPnl.add(controlPnl);
			rootPnl.add(submitPnl);
			
			// добавление элементов в окно просмотра
			add(rootPnl);
		}
		
		/**
		 * Получение списка выбранных пакетов
		 * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
		 * @return
		 */
		public List<Object> getSelectedPackages() {
			return packageList.getSelectedValuesList();
		}
	
		/**
		 * Добавление пакета в список
		 * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
		 * @param model - модель для списка пакетов
		 * @param pack - файл пакета
		 * @param parent - строка с именем корневых пакетов
		 */
		private void addPackage(DefaultListModel model, File pack, String parent) {
			if (pack == null || pack.isFile()) {
				return;
			}
			String packName = (parent.isEmpty() ? "" : parent + ".") + pack.getName();
			FileFilter filter = new FileFilter() {
				// фильтр классов
				public boolean accept(File file) {
					return file.getName().matches(".+\\.class$");
				}
			};
			if (pack.listFiles(filter).length > 0 && !model.contains(packName)) {
				// пакет содержит классы - добавляем в список
				model.addElement(packName);
			}
			for (File subPack : pack.listFiles()) {
				if (!subPack.exists()) {
					continue;
				}
				if (subPack.isDirectory()) {
					addPackage(model, subPack, packName);
				}
			}
		}
	
		/**
		 * Создание модели списка
		 * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
		 */
		private void createPackageListModel() {
			String path = Options.getPath();
			if (path == null || path.isEmpty()) {
				isLoaded = false;
				return;
			}
			synchronized(listModel) {
				try {
					if (path.matches(".+\\.jar$")) {
						JarFile jarfile = new JarFile(new File(path));
						Enumeration<JarEntry> enu = jarfile.entries();
						while (enu.hasMoreElements()) {
							JarEntry je = enu.nextElement();
							File fl = new File(path, je.getName());
							if (je.isDirectory()) {
								continue;
							}
							if (je.getName().matches(".+\\.class$")) {
								// заносим пакет класа в модель
								String packName = extractPackName(je.getName());
								if (packName.trim().equals("")) {
									packName = ROOT_PACKAGE;
								}
								if (!listModel.contains(packName)) {
									listModel.addElement(packName);
								}
							}                    
						}
						if (listModel.contains(ROOT_PACKAGE)) {
							listModel.set(0, ROOT_PACKAGE);
						}
						isLoaded = true;
						return;
					}
					
					// строим модель из содержимого директории
					File src = new File(path);
					if (src.isDirectory()) {
						// фильтр: допускается только байт код
						FileFilter filter = new FileFilter() {
							public boolean accept(File file) {
								return file.getName().matches(".+\\.class$");
							}
						};
						for (File subPack : src.listFiles()) {
							if (!subPack.exists()) {
								continue;
							}
							if (subPack.isFile() && subPack.getName().matches(".+\\.class$")) {
								// извлекаем корневой пакет из класса
								if (!listModel.contains(ROOT_PACKAGE)) {
									listModel.addElement(ROOT_PACKAGE);
								}
							}
							if (subPack.isDirectory()) {
								// уходим вглубь пакета
								addPackage(listModel, subPack, "");
							}
						}
					}
					if (listModel.contains(ROOT_PACKAGE)) {
						listModel.set(0, ROOT_PACKAGE);
					}
					isLoaded = true;
				} catch(IOException e) {
					isLoaded = false;
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Пакет класса
		 * @param path
		 * @return
		 */
		private String extractPackName(String path) {
			int ndx = path.lastIndexOf("/");
			if (ndx == -1) {
				return "";
			}
			String pack = path.substring(0, ndx);
			return pack.replaceAll("/", ".");
		}
		
		/**
		 * Имя класса без пакета
		 * @param path
		 * @return
		 */
		private String extractClassName(String path) {
			int ndx = path.lastIndexOf("/");
			if (ndx == -1) {
				return path;
			}
			return path.substring(ndx+1, path.length());
		}
	}
}