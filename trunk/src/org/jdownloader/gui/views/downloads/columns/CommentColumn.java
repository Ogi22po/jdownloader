package org.jdownloader.gui.views.downloads.columns;

import jd.controlling.linkcrawler.CrawledLink;
import jd.controlling.linkcrawler.CrawledPackage;
import jd.controlling.packagecontroller.AbstractNode;
import jd.plugins.DownloadLink;
import jd.plugins.FilePackage;

import org.appwork.swing.exttable.columns.ExtTextAreaColumn;
import org.jdownloader.gui.translate._GUI;

public class CommentColumn extends ExtTextAreaColumn<AbstractNode> {

    /**
     * 
     */
    private static final long serialVersionUID = 3276217379318150024L;

    public CommentColumn() {
        super(_GUI._.CommentColumn_CommentColumn_());
    }

    @Override
    public boolean isEditable(AbstractNode obj) {
        return true;
    }

    @Override
    public boolean isEnabled(final AbstractNode obj) {
        if (obj instanceof CrawledPackage) { return ((CrawledPackage) obj).getView().isEnabled(); }
        return obj.isEnabled();
    }

    protected boolean isEditable(final AbstractNode obj, final boolean enabled) {

        return isEditable(obj);
    }

    public boolean isPaintWidthLockIcon() {
        return false;
    }

    @Override
    protected void setStringValue(String value, AbstractNode object) {
        DownloadLink dl = null;
        if (object instanceof DownloadLink) {
            dl = (DownloadLink) object;
        } else if (object instanceof CrawledLink) {
            dl = ((CrawledLink) object).getDownloadLink();
        } else if (object instanceof FilePackage) {
            ((FilePackage) object).setComment(value);
            return;
        } else if (object instanceof CrawledPackage) {
            ((CrawledPackage) object).setComment(value);
            return;
        }
        if (dl != null) {
            dl.setComment(value);
            return;
        }

    }

    @Override
    public String getStringValue(AbstractNode object) {
        DownloadLink dl = null;
        if (object instanceof DownloadLink) {
            dl = (DownloadLink) object;
        } else if (object instanceof CrawledLink) {
            dl = ((CrawledLink) object).getDownloadLink();
        } else if (object instanceof FilePackage) {
            return ((FilePackage) object).getComment();
        } else if (object instanceof CrawledPackage) { return ((CrawledPackage) object).getComment(); }
        if (dl != null) { return dl.getComment(); }
        return null;
    }
}
