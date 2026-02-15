import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import jamiebalfour.zpe.core.ZPEFunction;
import jamiebalfour.zpe.core.ZPERuntimeEnvironment;
import jamiebalfour.zpe.core.ZPEStructure;
import jamiebalfour.zpe.interfaces.ZPECustomFunction;
import jamiebalfour.zpe.interfaces.ZPELibrary;
import jamiebalfour.zpe.interfaces.ZPEType;
import jamiebalfour.zpe.types.ZPEBoolean;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Plugin implements ZPELibrary {

  @Override
  public Map<String, ZPECustomFunction> getFunctions() {
    HashMap<String, ZPECustomFunction> functions = new HashMap<>();
    functions.put("open_access_file", new open_access_file());
    return functions;
  }

  @Override
  public Map<String, Class<? extends ZPEStructure>> getObjects() {
    return Map.of();
  }

  @Override
  public boolean supportsWindows() {
    return true;
  }

  @Override
  public boolean supportsMacOs() {
    return true;
  }

  @Override
  public boolean supportsLinux() {
    return true;
  }

  @Override
  public String getName() {
    return "libMSAccess";
  }

  @Override
  public String getVersionInfo() {
    return "1.0";
  }

  public static void main(String[] args) {
    Database db = null;
    try {
      db = DatabaseBuilder.open(new File("/Users/jamiebalfour/Downloads/Customer.accdb"));
      for (String tableName : db.getTableNames()) {
        Table table = db.getTable(tableName);

        for (Row row : table) {
          System.out.println(row.get("foreName"));
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  class open_access_file implements ZPECustomFunction {

    @Override
    public int getRequiredPermissionLevel() {
      return 3;
    }

    @Override
    public String getManualHeader() {
      return "Opens an Microsoft Access database file and returns an MSAccess object";
    }

    @Override
    public String getManualEntry() {
      return "";
    }

    @Override
    public String[] getParameterNames() {
      return new String[]{"path"};
    }

    @Override
    public ZPEType MainMethod(HashMap<String, Object> parameters, ZPERuntimeEnvironment runtime, ZPEFunction currentFunction) {

      String path = (String) parameters.get("path").toString();

      if(new File(path).exists()){
       try{
         Database db = DatabaseBuilder.open(new File("/Users/jamiebalfour/Downloads/Customer.accdb"));

         return new ZPEMSAccess(runtime, currentFunction, db);

       } catch (IOException e) {
         return ZPEBoolean.FALSE();
       }
      }

      return ZPEBoolean.FALSE();

    }

    @Override
    public byte getReturnType() {
      return 0;
    }

    @Override
    public int getMinimumParameters() {
      return 0;
    }
  }
}