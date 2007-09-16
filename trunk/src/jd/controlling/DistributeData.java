package jd.controlling;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import jd.JDUtilities;
import jd.event.ControlEvent;
import jd.plugins.DownloadLink;
import jd.plugins.Plugin;
import jd.plugins.PluginForDecrypt;
import jd.plugins.PluginForHost;
import jd.plugins.PluginForSearch;

/**
 * Diese Klasse läuft in einem Thread und verteilt den Inhalt der Zwischenablage an (unter Umständen auch mehrere) Plugins
 * Die gefundenen Treffer werden ausgeschnitten.
 *
 * @author astaldo
 */
public class DistributeData extends ControlMulticaster{
    /**
     * Der Logger
     */
    private static Logger logger = Plugin.getLogger();
    /**
     * Die zu verteilenden Daten
     */
    private String data;
    /**
     * Plugins der Anbieter
     */
    private Vector<PluginForHost> pluginsForHost;
    /**
     * Plugins zum Entschlüsseln
     */
    private Vector<PluginForDecrypt> pluginsForDecrypt;
    private Vector<PluginForSearch> pluginsForSearch;
    /**
     * Erstellt einen neuen Thread mit dem Text, der verteilt werden soll.
     * Die übergebenen Daten werden durch einen URLDecoder geschickt.
     *
     * @param data Daten, die verteilt werden sollen
     */
    public DistributeData (String data){
        super("JD-DistributeData");
        this.data              = data;
        this.pluginsForHost    = JDUtilities.getPluginsForHost();
        this.pluginsForDecrypt = JDUtilities.getPluginsForDecrypt();
        this.pluginsForSearch = JDUtilities.getPluginsForSearch();
        try {
            this.data = URLDecoder.decode(this.data,"US-ASCII");
        }
        catch (Exception e) {
            logger.warning("text not url decoded");
        }
    }
    public void run(){
        Vector<DownloadLink> links    = new Vector<DownloadLink>();
        Vector<String> cryptedLinks   = new Vector<String>();
        Vector<String> decryptedLinks = new Vector<String>();
   
        PluginForDecrypt pDecrypt;
        PluginForHost    pHost;
        PluginForSearch    pSearch;
//Zuerst wird data durch die Such PLugins geschickt.
        
        for(int i=0; i<pluginsForSearch.size();i++){
            pSearch = pluginsForSearch.get(i);
            
            if(pSearch.canHandle(data)){
                fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PLUGIN_SEARCH_ACTIVE, pSearch));
                decryptedLinks.addAll(pSearch.findLinks(data));
                data = pSearch.cutMatches(data);
                fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PLUGIN_SEARCH_INACTIVE, pSearch));
            }
        }
        
        
        // Zuerst wird überprüft, ob ein Decrypt-Plugin einen Teil aus der
        // Zwischenablage entschlüsseln kann. Ist das der Fall, wird die entsprechende Stelle
        // verarbeitet und gelöscht, damit sie keinesfalls nochmal verarbeitet wird.
        for(int i=0; i<pluginsForDecrypt.size();i++){
            pDecrypt = pluginsForDecrypt.get(i);
            if(pDecrypt.isClipboardEnabled() && pDecrypt.canHandle(data)){
                fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PLUGIN_DECRYPT_ACTIVE, pDecrypt));
                cryptedLinks.addAll(pDecrypt.getMatches(data,pDecrypt.getSupportedLinks()));
                data = pDecrypt.cutMatches(data);
                decryptedLinks.addAll(pDecrypt.decryptLinks(cryptedLinks));
                fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PLUGIN_DECRYPT_INACTIVE, pDecrypt));
            }
        }
        // Die entschlüsselten Links werden nochmal durch alle DecryptPlugins geschickt.
        // Könnte sein, daß einige zweifach oder mehr verschlüsselt sind
        boolean moreToDo;
        do{
            moreToDo = false;
            for(int i=0; i<pluginsForDecrypt.size();i++){
                pDecrypt = pluginsForDecrypt.get(i);
                Iterator<String> iterator = decryptedLinks.iterator();
                while (iterator.hasNext()){
                    String data = iterator.next();
                    if(pDecrypt.isClipboardEnabled() && pDecrypt.canHandle(data)){
                        moreToDo = true;
                        fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PLUGIN_DECRYPT_ACTIVE, pDecrypt));
                        logger.info("decryptedLink removed "+data+">>"+pDecrypt.getHost());
                        iterator.remove();
                        decryptedLinks.addAll(pDecrypt.decryptLink(data));
                        iterator = decryptedLinks.iterator();
                        fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PLUGIN_DECRYPT_INACTIVE, pDecrypt));
                    }
                }
            }
        }
        while(moreToDo);

        // Danach wird der (noch verbleibende) Inhalt der Zwischenablage an die Plugins der Hoster geschickt.
        for(int i=0; i<pluginsForHost.size();i++){
            pHost = pluginsForHost.get(i);
            if(pHost.isClipboardEnabled() && pHost.canHandle(data)){
                links.addAll(pHost.getDownloadLinks(data));
                data = pHost.cutMatches(data);
            }
        }

        // Als letztes werden die entschlüsselten Links (soweit überhaupt vorhanden)
        // an die HostPlugins geschickt, damit diese einen Downloadlink erstellen können
        Iterator<String> iterator = decryptedLinks.iterator();
        
        while(iterator.hasNext()){
            String decrypted = iterator.next();
            logger.info("link: "+decrypted);
            for(int i=0; i<pluginsForHost.size();i++){
                pHost = pluginsForHost.get(i);
                if(pHost.isClipboardEnabled() && pHost.canHandle(decrypted)){
                    links.addAll(pHost.getDownloadLinks(decrypted));
                    iterator.remove();
                }
            }
        }
        fireControlEvent(new ControlEvent(this,ControlEvent.CONTROL_DISTRIBUTE_FINISHED, links));
    }
}