package host.kuro.kurobase.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.SkinData;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class SkinTask extends BukkitRunnable {

    private final KuroBase plugin;
    private final Player player;
    private final String path;
    private final String makepath;

    public SkinTask(KuroBase plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.path = plugin.getConfig().getString("Web.skinpath");
        this.makepath = plugin.getConfig().getString("Web.skinmake");
    }

    @Override
    public void run() {
        try {
            if (player == null) return;

            // get profile skin data
            SkinData skin = PlayerUtils.getPlayerSkin(player);
            if (skin == null) return;

            String base64data = skin.getBase64();
            if (base64data.length() <= 0) return;

            byte[] bytes = Base64.decodeBase64(base64data);
            String jsontext = new String(bytes, "UTF8");
            if (jsontext.length() <= 0) return;

            // json parse
            JsonObject jsonObj = (JsonObject) new Gson().fromJson(jsontext, JsonObject.class);
            if (jsonObj == null) return;

            JsonObject texobj = jsonObj.getAsJsonObject("textures");
            if (texobj == null) return;

            JsonObject skinobj = texobj.getAsJsonObject("SKIN");
            if (skinobj == null) return;

            JsonElement element_url = skinobj.get("url");
            if (element_url == null) return;

            // url get
            String url_text = element_url.getAsString();

            BufferedImage bi =null;
            URLConnection urlcon;
            try {
                URI uri = new URI(url_text);
                URL url = uri.toURL();
                urlcon = url.openConnection();
                bi = ImageIO.read(urlcon.getInputStream());

                File saveFile;
                if (plugin.IsLinux()) {
                    new File(this.path).mkdirs();
                    saveFile = new File( this.path + "/" + player.getName().toLowerCase() + ".png");
                    ImageIO.write(bi, "png", saveFile);

                    // 2D skin make
                    if (makepath.length() > 0) {
                        HttpURLConnection con = null;
                        StringBuffer result = new StringBuffer();
                        URL makeurl = new URL(makepath + "?show=body&side=front&cloak=hd&file_name="+player.getName().toLowerCase());
                        con = (HttpURLConnection) makeurl.openConnection();
                        con.setRequestMethod("GET");
                        con.connect();
                        if (con != null) {
                            final int status = con.getResponseCode();
                            if (status == HttpURLConnection.HTTP_OK) {
                                final InputStream in = con.getInputStream();
                                String encoding = con.getContentEncoding();
                                if(null == encoding){
                                    encoding = "UTF-8";
                                }
                                final InputStreamReader inReader = new InputStreamReader(in, encoding);
                                final BufferedReader bufReader = new BufferedReader(inReader);
                                String line = null;
                                while((line = bufReader.readLine()) != null) {
                                    result.append(line);
                                }
                                bufReader.close();
                                inReader.close();
                                in.close();
                            }
                            con.disconnect();
                        }
                    }
                } else {
                    new File(plugin.getDataFolder() + "/skin/data/").mkdirs();
                    saveFile = new File( plugin.getDataFolder() + "/skin/data/" + player.getName() + ".png");
                    ImageIO.write(bi, "png", saveFile);
                }

                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.SKIN.UPDATE"), args);
                args.clear();
                args = null;

            } catch (URISyntaxException ex) {
                plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
            } catch (MalformedURLException ex) {
                plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
            } catch (IOException ex) {
                plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
            }

        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        }
    }
}
