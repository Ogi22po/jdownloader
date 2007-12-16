package jd.gui.skins.simple.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import jd.captcha.JAntiCaptcha;
import jd.config.Configuration;
import jd.gui.UIInterface;
import jd.utils.JDLocale;
import jd.utils.JDUtilities;

public class ConfigPanelCaptcha extends ConfigPanel implements MouseListener{






    /**
     * 
     */
  



    private JTable                   table;

    private Configuration configuration;

    private File[] methods;



    public ConfigPanelCaptcha(Configuration configuration, UIInterface uiinterface) {
        super(uiinterface);
        this.configuration=configuration;
       methods = JAntiCaptcha.getMethods("jd/captcha/methods/");
       logger.info(methods.length+"");
  
 
       
        initPanel(); 

        load();

    }

    /**
     * Lädt alle Informationen
     */
    public void load() {

    }

    /**
     * Speichert alle Änderungen auf der Maske
     */
    public void save() {
    // Interaction[] tmp= new Interaction[interactions.size()];
//        PluginForSearch plg;
//        for (int i = 0; i < pluginsForSearch.size(); i++) {
//            plg = pluginsForSearch.elementAt(i);
//            if (plg.getProperties() != null) configuration.setProperty("PluginConfig_" + plg.getPluginName(), plg.getProperties());
//        }

    }
    public void mouseClicked(MouseEvent e) {
        
            
        
        
        configuration.setProperty(Configuration.PARAM_JAC_METHODS+"_"+methods[table.getSelectedRow()].getName(),!configuration.getBooleanProperty(Configuration.PARAM_JAC_METHODS+"_"+methods[table.getSelectedRow()].getName(),true));
        table.tableChanged(new TableModelEvent(table.getModel()));
    }
    @Override
    public void initPanel() {
        setLayout(new BorderLayout());
        table = new JTable();
        InternalTableModel internalTableModel = new InternalTableModel();
        table.setModel(new InternalTableModel());
      table.setEditingRow(0);
table.addMouseListener(this);
        this.setPreferredSize(new Dimension(700, 350));

        TableColumn column = null;
        for (int c = 0; c < internalTableModel.getColumnCount(); c++) {
            column = table.getColumnModel().getColumn(c);
            switch (c) {

                case 0:
                    column.setPreferredWidth(50);
                    break;
                case 1:
                    column.setPreferredWidth(600);
                    break;
         

            }
        }
        
        // add(scrollPane);
        // list = new JList();
    
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(400, 200));

      

    
        JDUtilities.addToGridBag(panel, scrollpane, 0, 0, 3, 1, 1, 1, insets, GridBagConstraints.BOTH, GridBagConstraints.CENTER);

        
        // JDUtilities.addToGridBag(this, panel,0, 0, 1, 1, 1, 1, insets,
        // GridBagConstraints.BOTH, GridBagConstraints.WEST);
        add(panel, BorderLayout.CENTER);

    }

    private int getSelectedIndex() {
        return table.getSelectedRow();
    }

    @Override
    public String getName() {

        return JDLocale.L("gui.config.jac.name","jAntiCaptcha");
    }


    private File getSelectedMethod() {
        int index = getSelectedIndex();
        if (index < 0) return null;
        return this.methods[index];
    }





    private class InternalTableModel extends AbstractTableModel {

        /**
         * 
         */
        private static final long serialVersionUID = 1155282457354673850L;

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
           

            }
            return String.class;
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return methods.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {

            switch (columnIndex) {
                case 0:                  
                    return configuration.getBooleanProperty(Configuration.PARAM_JAC_METHODS+"_"+methods[rowIndex].getName(),true);
                case 1:
                    return methods[rowIndex].getName()+" : "+(configuration.getBooleanProperty(Configuration.PARAM_JAC_METHODS+"_"+methods[rowIndex].getName(),true)?JDLocale.L("gui.config.jac.status.auto","Automatische Erkennung"):JDLocale.L("gui.config.jac.status.noauto","Manuelle Eingabe"));
            

            }
            return null;
        }

        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return JDLocale.L("gui.config.jac.column.use","Verwenden");
                case 1:
                    return JDLocale.L("gui.config.jac.column.method","Methode");
           

            }
            return super.getColumnName(column);
        }
    }





    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    

    
    

    

}
