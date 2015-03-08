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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
private static Logger LOG = LoggerFactory.getLogger(PackageDialog.class);

private static JDialog dialog;
private JFrame context;
private String path = Options.getPath();
private PackageListPnl packageListPnl;
private static UI ui = UI.getInstance();
private static volatile boolean isLoaded = false;

public PackageDialog(final JFrame context) {
	this.context = context;
}
	
public void updatePath() {
	this.path = Options.getPath();
}

public static boolean isLoaded() {
	return isLoaded;
}

public void initDialog() {
    dialog = new JDialog(context, Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setTitle(ui.getLocaleLabels().getString("selectPackagesLabel"));
    
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
	LOG.info("isLoaded: " + isLoaded);
	LOG.info("dialog: " + (dialog != null));
	if (/*isLoaded &&*/ dialog != null) {
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
		private static UI ui = UI.getInstance();
			
		public static final String ROOT_PACKAGE = ">";
	
		public PackageListPnl() {
			super(new GridLayout(1, 0));
			// модель для списка создаем в отдельном потоке
		    listModel = new DefaultListModel();
		    
		    // копируем пакеты з настроек в модель
		    for (String pack : Options.getPackages()) {
		    	listModel.addElement(pack);
		    }
		    
		    // создаем список пакетов
		    packageList = new JList(listModel);    
		    packageList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	
		    
			// панель для скролла
			JScrollPane treeView = new JScrollPane(packageList);
			
			// элементы управления
			allCbx = new JCheckBox(ui.getLocaleLabels().getString("allPackagesLabel"));
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
			tipMultLbl = new JLabel("<html><p><b>Shift + " + ui.getLocaleLabels().getString("lmbLabel") + "</b></p></html>", SwingConstants.CENTER);
			tipMultLbl.setToolTipText("Выбор нескольких пакетов");
			tipOnceLbl = new JLabel("<html><p><b>Ctrl + " + ui.getLocaleLabels().getString("lmbLabel") + "</b></p></html>", SwingConstants.CENTER);
			tipOnceLbl.setToolTipText(ui.getLocaleLabels().getString("particularPackagesSelection"));
			
			// размеры дерева
			Dimension preferedSize = new Dimension(300, 600);
			this.setPreferredSize(preferedSize);
	
			// кнопки выбора
			cnclBtn = new JButton(ui.getLocaleLabels().getString("cancelLabel"));
			cnclBtn.addActionListener(new ActionListener() 
		    {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		        	if (PackageDialog.dialog != null) {
		        		PackageDialog.dialog.setVisible(false);
		        	}     
		        }
		    });
			okBtn = new JButton("OK");
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
	}
}