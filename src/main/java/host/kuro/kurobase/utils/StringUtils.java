package host.kuro.kurobase.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.Transliterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class StringUtils {

    public static NumberFormat numFmt = NumberFormat.getNumberInstance();

    public static final boolean isHankakuEisu(String target) {
        target.replace("_", "");
        return Pattern.matches("^[0-9a-zA-Z]+$", target);
    }

    public static Boolean isHankakuEisuKigo(String value) {
        if ( value == null || value.length() == 0 )
            return true;
        int len = value.length();
        byte[] bytes = value.getBytes();
        if ( len != bytes.length )
            return false;
        return true;
    }

    public static final String GetJapanese(String target) {
        if (!(isHankakuEisuKigo(target))) return "";
        String buff = Zen2Han(target);
        buff = Roma2Hira(buff);
        buff = Hira2Kanji(buff);
        return buff;
    }

    public static final String Zen2Han(String target) {
        Transliterator trans = Transliterator.getInstance("Fullwidth-Halfwidth");
        String ret = trans.transliterate(target);
        return ret;
    }

    public static final String Roma2Hira(String target) {
        String ret = YukiKanaConverter.conv(target);
        //Transliterator trans = Transliterator.getInstance("Latin-Hiragana");
        //String ret = trans.transliterate(target);
        return ret;
    }

    private static final String GetBase36(String target) {
        byte[] bytes = target.getBytes();
        StringBuffer sb = new StringBuffer();
        int bitsUsed = 0;
        int temp = 0;
        int tempBits = 0;
        long swap;
        int position = 0;
        while((position < bytes.length) || (bitsUsed != 0)) {
            swap = 0;
            if(tempBits > 0) {
                swap = temp;
                bitsUsed = tempBits;
                tempBits = 0;
            }
            while((position < bytes.length) && (bitsUsed < 36)) {
                swap <<= 8;
                swap |= bytes[position++];
                bitsUsed += 8;
            }
            if(bitsUsed > 36) {
                tempBits = bitsUsed - 36;
                temp = (int)(swap & ((1 << tempBits) - 1));
                swap >>= tempBits;
                bitsUsed = 36;
            }
            sb.append(Long.toString(swap, 36));
            bitsUsed = 0;
        }
        return sb.toString();
    }

    public static final String Hira2Kanji(String target) {
        HttpURLConnection  urlConn = null;
        InputStream in = null;
        BufferedReader reader = null;
        String ret = "";
        try {
            // url
            String base36 = URLEncoder.encode(target, "UTF-8");
            URL url = new URL("http://www.google.com/transliterate?langpair=ja-Hira|ja&text=" + base36);
            // con
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.connect();
            // status
            int status = urlConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // response
                in = urlConn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));

                StringBuilder output = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                if (output.length() > 0) {
                    ret = new String(output);
                }
                ret = parseJson(ret);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (urlConn != null) {
                    urlConn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static String parseJson(String json) {
        StringBuilder result = new StringBuilder();
        for (JsonElement response : new Gson().fromJson(json, JsonArray.class)) {
            result.append(response.getAsJsonArray().get(1).getAsJsonArray().get(0).getAsString());
        }
        return result.toString();
    }
}
