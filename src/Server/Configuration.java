package Server;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
    private final String filePath;
    private BufferedReader in;
    private final  ConcurrentHashMap<String,String> config;

    public Configuration(String filePath){
        this.filePath = filePath;
        this.config = new ConcurrentHashMap<>();
    }
    public boolean basicConfigurationTest(){
        String[] mandatory = {"Port", "SmsSenderName", "Path2Logs", "StopServerPort", "Linux", "ACCOUNT_SID", "AUTH_TOKEN"};
        for (String setting : mandatory) {
            if(!config.containsKey(setting)) return false;
        }
        return true;
    }
    public ConcurrentHashMap<String,String> readFile()  {
        File confFile = new File(filePath);
        try {
            in = new BufferedReader(new FileReader(confFile));
            String line;
            while ((line = in.readLine())!= null){
                if(line.equals("") || line.charAt(0) == '#' )
                    continue;
                String[] setting = line.split("=");
                config.put(setting[0],setting[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!this.basicConfigurationTest()) { throw new NullPointerException("Configuration file Error"); }
        return config;
    }
    public int getNumericValue(String s) { return Integer.parseInt(this.config.getOrDefault(s, "0")); }
}
