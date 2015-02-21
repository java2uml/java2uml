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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.java2uml.core.Options;

public class PackageDialog {
	
private static Logger LOG = LoggerFactory.getLogger("LOG - ");

private static JDialog dialog;
private static final ImageIcon treeBtnImg = new ImageIcon("res/tree-view-light.png");
private JButton openTreeBtn;
private JFrame context;
private String path = Options.getPath();

public PackageDialog(final JFrame context) {
	this.context = context;
    //setBounds(500, 200, 500, 700);
    //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //setVisible(true);
    //setLayout(new FlowLayout());
    openTreeBtn = new JButton(new ImageIcon(treeBtnImg.getImage().getScaledInstance(20, 16, Image.SCALE_SMOOTH)));
    openTreeBtn.setSize(20,  20);
    openTreeBtn.setFocusPainted(false);
    openTreeBtn.setContentAreaFilled(false);
    openTreeBtn.addActionListener(new ActionListener() 
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            showDialog();
        }
    });
    //add(openTreeBtn);
}

public JButton getOpenTreeButton() {
	return this.openTreeBtn;
}

public void setEnabled(final boolean val) {
	this.openTreeBtn.setEnabled(val);
}

public void updatePath() {
	this.path = Options.getPath();
}

public void showDialog() {
    dialog = new JDialog(context, Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setTitle("Выбор пакетов для генерации");
    dialog.add(new CheckTreeMain());
    dialog.setBounds(550, 250, 400, 600);
    dialog.setVisible(true);
}

//-------------------------------------------------------------------------------------

private static class CheckTreeMain extends JPanel implements TreeSelectionListener {
	private JTree tree;
	private DefaultMutableTreeNode root;
	private JButton okBtn;
	private JButton cnclBtn;
	private JPanel rootPnl;
	

	// Стиль дерева. Возможные значения:
	// "Angled" (the default), "Horizontal", and "None".
	private static boolean playWithLineStyle = false;
	private static String lineStyle = "Horizontal";


	// стиль дерева
	private static boolean useSystemLookAndFeel = false;

	public CheckTreeMain() {
		super(new GridLayout(1, 0));

		// корневой узел с названием проекта
		root = new DefaultMutableTreeNode(new PackageNode("root:", false, true));
		LOG.info("Create nodes...");
		createNodes(root);

		// дерево пакетов
		tree = new JTree(root);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setFocusable(false);

		// обработка выбора узла дерева
		tree.addTreeSelectionListener(this);

		// стиль дерева
		if (playWithLineStyle) {
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		// панель для скролла
		JScrollPane treeView = new JScrollPane(tree);
		
		// размеры дерева
		Dimension preferedSize = new Dimension(300, 600);
		this.setPreferredSize(preferedSize);

		// панель управления
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
				if (!getNodeChecked(root)) {
					// пакеты выбираются изберательно
					packs = getCheckedPackages(root);
					packs.remove("root");
				}
				
				Set<String> optPacks = new HashSet<>();
				for (String pack : packs) {
					if (!pack.equals("root")) {
						pack = pack.replaceFirst("root.", "");
						optPacks.add(pack);
					}
				}
				
				LOG.info("Packages .................... ");
				for (String pack : optPacks) {
					LOG.info("Package - " + pack);
				}
				
				Options.setPackages(optPacks);
				if (PackageDialog.dialog != null) {
	        		PackageDialog.dialog.setVisible(false);
	        	}
			}
		});
		
		JPanel toolPnl = new JPanel();
		toolPnl.setMaximumSize(new Dimension(200, 50));
		toolPnl.add(okBtn);
		toolPnl.add(Box.createHorizontalStrut(30));
		toolPnl.add(cnclBtn);
		toolPnl.setLayout(new BoxLayout(toolPnl, BoxLayout.X_AXIS));
		
		// корневая панель
		rootPnl = new JPanel();
		rootPnl.setLayout(new BoxLayout(rootPnl, BoxLayout.Y_AXIS));
		rootPnl.add(treeView);
		rootPnl.add(toolPnl);
		
		// добавление элементов в окно просмотра
		add(rootPnl);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null)
			return;
		
		// получаем содержимое узла дерева
        Object nodeInfo = node.getUserObject();
        
		// отмечает текущий узел и все дочерние
		boolean checked = !getNodeChecked(node);
		setNodeChecked(node, checked);
		for (int ndx : tree.getSelectionRows()) {
			tree.removeSelectionRow(ndx);
		}
	}
	
	private boolean isRootNode(DefaultMutableTreeNode node) {
		PackageNode packNode = (PackageNode) node.getUserObject();
		return packNode.isRoot();
	}
	
	private String getNodeValue(DefaultMutableTreeNode node) {
		PackageNode packNode = (PackageNode) node.getUserObject();
		return packNode.getValue();
	}
	
	private boolean getNodeChecked(DefaultMutableTreeNode node) {
		PackageNode packNode = (PackageNode) node.getUserObject();
		return packNode.getChecked();
	}
	
	/**
	 * Получение имени пакета для узла
	 */
	private String getPackageName(DefaultMutableTreeNode node) {
		if (node.isRoot()) {
			return "root";
		} 
		return getPackageName((DefaultMutableTreeNode)node.getParent()) + "." + getNodeValue(node);
	}

	/**
	 * Помечаем узел и дочерние узлы
	 * @author Balyschev Alexey - alexbalu-alpha7@mail.ru 
	 * @param node - текущий узел
	 * @param checked - значение отметки
	 */
	private void setNodeChecked(DefaultMutableTreeNode node, boolean checked) {
		if (node == null) {
			return;
		}
		PackageNode packNode = (PackageNode) node.getUserObject();
		packNode.setChecked(checked);
		node.setUserObject(packNode);
		
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		model.nodeChanged(node);
		for (int ndx=0; ndx < node.getChildCount(); ++ndx) {
			setNodeChecked((DefaultMutableTreeNode)node.getChildAt(ndx), checked);
		}
	}
	
	/**
	 * Получение множества пакетов для дочерних узлов
	 */
	private Set<String> getCheckedPackages(DefaultMutableTreeNode node) {
		Set<String> checkedPacks = new HashSet<String>();
		if (node == null || node.isLeaf()) {
			if (getNodeChecked(node)) {
				checkedPacks.add(getPackageName(node));
			}
			return checkedPacks;
		}
		if (getNodeChecked(node) || isRootNode(node)) {
			// отмечен - берем его значение и значения дочерних узлов
			checkedPacks.add(getPackageName(node));
		}
		if (node.getChildCount() > 0) {
			// уход вглубь для проверки дечерних узлов
			for (int ndx=0; ndx < node.getChildCount(); ++ndx) {
				checkedPacks.addAll(getCheckedPackages((DefaultMutableTreeNode)node.getChildAt(ndx)));
			}
		}
		return checkedPacks;
	}
	
	
	/**
	 * Построение узла дерева
	 * @param parent - родительский узел
	 * @param pack - ссылка на файл узла дерева
	 * @return - узел дерева со всеми подузлами
	 */
	private DefaultMutableTreeNode initTreeNode(DefaultMutableTreeNode parent, File pack) {
		DefaultMutableTreeNode current = new DefaultMutableTreeNode(new PackageNode(pack.getName(), false));
		for (File nd : pack.listFiles()) {
			if (!nd.exists()) {
				continue;
			}
			if (nd.isFile()) {
				// добавляем файл в узел
				// current.add(new DefaultMutableTreeNode(new PackageNode(nd.getName(), false)));
			}
			if (nd.isDirectory()) {
				// уходим вглубь пакета
				current.add(initTreeNode(root, nd));
			} 
		}
		return current;
	}
	
	/**
	 * Построение узла дерева
	 * @param parent - родительский узел
	 * @param pack - имя узла дерева
	 * @return - узел дерева
	 */
	private DefaultMutableTreeNode initTreeNode(DefaultMutableTreeNode parent, String pack) {
		DefaultMutableTreeNode current = new DefaultMutableTreeNode(new PackageNode(pack, false));
		parent.add(current);
		return current;
	}
	
	/**
	 * Создание узлов дерева
	 * @author Balyschev Alexey - alexbalu-alpha7@mail.ru 
	 * @param root - корневой узел
	 */
	private void createNodes(DefaultMutableTreeNode root) {
		String path = Options.getPath();
		
		if (path == null || path.isEmpty()) {
			return;
		}
		boolean isJar = false;
		try {
			if (path.matches(".+\\.jar$")) {
			    // распакуем файлы из jar в директорию
				JarFile jarfile = new JarFile(new File(path));
				path = path.substring(0, path.lastIndexOf(".jar"));
				isJar = true;
				Enumeration<JarEntry> enu = jarfile.entries();
				while (enu.hasMoreElements()) {
					JarEntry je = enu.nextElement();
					File fl = new File(path, je.getName());
					if (je.isDirectory()) {
						continue;
					}
					if (je.getName().matches(".+\\.class$")) {
						// сохраняем файл класса
                    	fl.getParentFile().mkdirs();
                      	fl.getParentFile().createNewFile();
                      	fl = new File(path, je.getName());
                      	try (InputStream is = jarfile.getInputStream(je);
                      		FileOutputStream fo = new FileOutputStream(fl);) {
                          	// сохраняем файл
                          	while (is.available() > 0) {
                          		fo.write(is.read());
                          	}
                      	}
					}                    
				}
			}
			
			// строим дерево из содержимого директории
			File src = new File(path);
			for (File nd : src.listFiles()) {
				if (!nd.exists()) {
					continue;
				}
				if (nd.isFile()) {
					// добавляем файл в узел
					// root.add(new DefaultMutableTreeNode(new PackageNode(nd.getName(), false)));
				}
				if (nd.isDirectory()) {
					// уходим вглубь пакета
					root.add(initTreeNode(root, nd));
				}
			}
//			if (path.matches(".+\\.jar$")) {
//				// строим дерево из содержимого jar
//				Stack<Map<String, Object>> nodeStack = new Stack<Map<String, Object>>();
//				Map<String, Object> map = new HashMap<>();
//				map.put("pack", "root");
//				map.put("node", root);
//				nodeStack.push(map);
//	            try {
//	                // прислали jar - итерируем
//	                JarInputStream jar = new JarInputStream(new FileInputStream(path));
//					JarEntry je;
//					while((je = jar.getNextJarEntry()) != null) {
//						LOG.info("Class:: " +je.getName());
//						if (je.isDirectory()) {
//							// пытаемся извлечь пакет...
//	                    	String pack = extractPackName(je.getName());
//	                    	String fullPackage = je.getName().substring(0, je.getName().lastIndexOf("/"));
//	                    	if (pack != null && !pack.isEmpty()) {
//	                    		// имя получено - добавим пакет в стек и узел в дерево
//	                    		Map<String, Object> child = new HashMap<>();
//                    			child.put("pack", fullPackage);
//	                    		if (nodeStack.size() <= 1) {
//		                    		// если в корне - добавляем
//	                    			child.put("node", initTreeNode(root, pack));
//		                    		nodeStack.push(child);
//		                    	} else {
//		                    		// есть другие пакеты - проверим вложенность и повторы
//		                    		if (!fullPackage.equals((String)nodeStack.peek().get("pack"))) {
//		                    			// повторов нет, имеем дело с разными пакетами
//		                    			if (isSubDir(fullPackage, (String)nodeStack.peek().get("pack"))) {
//			                    			child.put("node", initTreeNode((DefaultMutableTreeNode)nodeStack.peek().get("node"), pack));
//			                    			nodeStack.push(child);
//			                    		} else {
//			                    			// не вложен - уберем пред. пакет
//			                    			String msg = "Size before: " + nodeStack.size();
//			                    			while (nodeStack.size() > 1) {
//			                    				if (isSubDir(fullPackage, (String)nodeStack.peek().get("pack"))) {		
//			                    					break;
//			                    				}
//			                    				nodeStack.pop();
//			                    			}
//			                    			child.put("node", initTreeNode((DefaultMutableTreeNode)nodeStack.peek().get("node"), pack));
//			                    			nodeStack.push(child);
//			                    		}
//		                    		}
//		                    	}
//	                    	}
//	                	}
//	                	
//	                	if (je.getName().matches(".+\\.class$")) {
//	                    	String clsName = extractClassName(je.getName());
//	                    	if (clsName != null && !clsName.isEmpty()) {
//	                    		// добавляем узел класса в текущий узел пакета-родителя
//		                    	initTreeNode((DefaultMutableTreeNode)nodeStack.peek().get("node"), clsName);
//	                    	}
//	                    }
//					}	                
//	            } catch (IOException e) {
//	                throw e;
//	            }
//	        }
			
			if (isJar) {
				// даляем распакованные директории
				if (src.isDirectory() && src.exists()) {
					src.delete();
				}
			}		
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Определение вложенности одной директории к другой
	 * @param sub - потенциальная поддиректория
	 * @param dir - директория
	 * @return
	 */
	private boolean isSubDir(final String sub, final String dir) {
		return sub.contains(dir); 
	}
	
	/**
	 * Верхнее имя пакета
	 * @param path
	 * @return
	 */
	private String extractPackName(String path) {
		path = path.substring(0, path.lastIndexOf("/"));
		String[] split = path.split("/");
		return split[split.length-1];
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
	
	/**
	 * Класс узла
	 * @author Alexey Balyschev - alexbalu-alpha7@mail.ru
	 */
	private class PackageNode {
		private String caption;
		private String value;
		private boolean checked;
		private boolean isRoot;
		
		public PackageNode(String value, boolean checked, boolean isRoot) {
			this(value, checked);
			this.isRoot = isRoot;			
		}
		
		public PackageNode(String value, boolean checked) {
			this.caption = value;
			this.value = value;
			this.checked = checked;
		}
		@Override
		public String toString() {
			return caption;
		}
		
		public String getValue() {
			return this.value;
		}
		
		public boolean getChecked() {
			return this.checked;
		}
		
		public void setChecked(boolean checked) {
			this.checked = checked;
			if (checked) {
				caption = value + " - ok";
			} else {
				caption = value;
			}
		} 
		public boolean isRoot() {
			return this.isRoot;
		}
	}
}

// -------------------------------------------------------------------------------------
public static void main(String[] args) {
    new PackageDialog(null);
}
}