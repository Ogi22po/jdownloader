package org.jdownloader.extensions.jdfeedme.columns;

import jd.gui.swing.components.table.JDTableModel;
import jd.gui.swing.components.table.JDTextEditorTableColumn;

import org.jdownloader.extensions.jdfeedme.JDFeedMeFeed;

public class TimestampColumn extends JDTextEditorTableColumn {

	private static final long serialVersionUID = 4030351626645232509L;

    public TimestampColumn(String timestamp, JDTableModel table) {
        super(timestamp, table);
    }

    @Override
    public boolean isEditable(Object obj) {
        return false;
    }

    @Override
    public boolean isEnabled(Object obj) {
        return ((JDFeedMeFeed) obj).isEnabled();
    }

    @Override
    public boolean isSortable(Object obj) {
        return false;
    }

    @Override
    public void sort(Object obj, boolean sortingToggle) {
    }

    @Override
    protected String getStringValue(Object value) {
        return ((JDFeedMeFeed) value).getTimestamp();
    }

    @Override
    protected void setStringValue(String value, Object object) {
        ((JDFeedMeFeed) object).setTimestamp(value);
    }

    @Override
    public int getMaxWidth() {
        return 190;
    }

}
