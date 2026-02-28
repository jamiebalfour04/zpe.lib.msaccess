import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import jamiebalfour.generic.JBBinarySearchTree;
import jamiebalfour.zpe.core.ZPEObject;
import jamiebalfour.zpe.core.ZPERuntimeEnvironment;
import jamiebalfour.zpe.core.ZPEStructure;
import jamiebalfour.zpe.exceptions.ExitHalt;
import jamiebalfour.zpe.exceptions.IncorrectDataTypeException;
import jamiebalfour.zpe.exceptions.ZPERuntimeException;
import jamiebalfour.zpe.interfaces.ZPEObjectNativeMethod;
import jamiebalfour.zpe.interfaces.ZPEPropertyWrapper;
import jamiebalfour.zpe.interfaces.ZPEType;
import jamiebalfour.zpe.types.ZPEBoolean;
import jamiebalfour.zpe.types.ZPEList;
import jamiebalfour.zpe.types.ZPEString;

import java.io.IOException;

public class ZPEACCDB extends ZPEStructure {

  private final Database db;
  private final ZPERuntimeEnvironment runtime;

  protected ZPEACCDB(ZPERuntimeEnvironment z, ZPEPropertyWrapper parent, Database db) {
    super(z, parent, "ZPEMSAccess");
    this.db = db;
    this.runtime = z; // <-- IMPORTANT: you were never setting this before

    addNativeMethod("get_tables", new get_tables_Command());
    addNativeMethod("get_table", new get_table_Command());
  }

  public class get_tables_Command implements ZPEObjectNativeMethod {

    @Override
    public String[] getParameterNames() {
      return new String[0];
    }

    @Override
    public String[] getParameterTypes() {
      return new String[0];
    }

    @Override
    public ZPEType MainMethod(JBBinarySearchTree<String, ZPEType> parameters, ZPEObject parent)
            throws ZPERuntimeException, ExitHalt, IncorrectDataTypeException {

      ZPEList output = new ZPEList();

      try {
        for (String tableName : db.getTableNames()) {
          output.add(new ZPEString(tableName));
        }
      } catch (IOException e) {
        // Match your existing behaviour: ignore and return what we have (empty list)
      }

      return output;
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 0;
    }

    @Override
    public String getName() {
      return "get_tables";
    }
  }

  public class get_table_Command implements ZPEObjectNativeMethod {

    @Override
    public String[] getParameterNames() {
      return new String[]{"table_name"};
    }

    @Override
    public String[] getParameterTypes() {
      return new String[]{"string"};
    }

    @Override
    public ZPEType MainMethod(JBBinarySearchTree<String, ZPEType> parameters, ZPEObject zpeObject)
            throws ZPERuntimeException, ExitHalt, IncorrectDataTypeException {

      String tableName = parameters.get("table_name").toString();

      try {
        Table table = db.getTable(tableName);
        return new ZPEMSAccessTable(runtime, zpeObject, table);
      } catch (IOException e) {
        return ZPEBoolean.FALSE();
      }
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 0;
    }

    @Override
    public String getName() {
      return "get_table";
    }
  }

  public class ZPEMSAccessTable extends ZPEStructure {

    private final Table table;

    protected ZPEMSAccessTable(ZPERuntimeEnvironment z, ZPEPropertyWrapper parent, Table table) {
      super(z, parent, "MSAccessTable");
      this.table = table;

      addNativeMethod("get_rows", new get_rows_Command());
      addNativeMethod("get_field_names", new get_field_names_Command());
    }

    public class get_field_names_Command implements ZPEObjectNativeMethod {

      @Override
      public String[] getParameterNames() {
        return new String[0];
      }

      @Override
      public String[] getParameterTypes() {
        return new String[0];
      }

      @Override
      public ZPEType MainMethod(JBBinarySearchTree<String, ZPEType> jbBinarySearchTree, ZPEObject zpeObject)
              throws ZPERuntimeException, ExitHalt, IncorrectDataTypeException {

        ZPEList output = new ZPEList();

        for (Column c : table.getColumns()) {
          output.add(new ZPEString(c.getName()));
        }

        return output;
      }

      @Override
      public int getRequiredPermissionLevel() {
        return 0;
      }

      @Override
      public String getName() {
        return "get_field_names";
      }
    }

    public class get_rows_Command implements ZPEObjectNativeMethod {

      @Override
      public String[] getParameterNames() {
        return new String[0];
      }

      @Override
      public String[] getParameterTypes() {
        return new String[0];
      }

      @Override
      public ZPEType MainMethod(JBBinarySearchTree<String, ZPEType> jbBinarySearchTree, ZPEObject zpeObject)
              throws ZPERuntimeException, ExitHalt, IncorrectDataTypeException {

        ZPEList output = new ZPEList();

        for (Row r : table) {
          output.add(new ZPEMSAccessRow(runtime, zpeObject, r));
        }

        return output;
      }

      @Override
      public int getRequiredPermissionLevel() {
        return 0;
      }

      @Override
      public String getName() {
        return "get_rows";
      }
    }
  }

  public class ZPEMSAccessRow extends ZPEStructure {

    private final Row row;

    protected ZPEMSAccessRow(ZPERuntimeEnvironment z, ZPEPropertyWrapper parent, Row row) {
      super(z, parent, "MSAccessRow");
      this.row = row;

      addNativeMethod("get_column", new get_column_Command());
    }

    public ZPEType get(String columnName) {
      Object v = row.get(columnName);

      // Null-safe: Access fields can genuinely be null
      if (v == null) {
        return new ZPEString("");
      }

      return new ZPEString(v.toString());
    }

    public class get_column_Command implements ZPEObjectNativeMethod {

      @Override
      public String[] getParameterNames() {
        return new String[]{"column_name"};
      }

      @Override
      public String[] getParameterTypes() {
        return new String[]{"string"};
      }

      @Override
      public ZPEType MainMethod(JBBinarySearchTree<String, ZPEType> parameters, ZPEObject zpeObject)
              throws ZPERuntimeException, ExitHalt, IncorrectDataTypeException {
        return get(parameters.get("column_name").toString());
      }

      @Override
      public int getRequiredPermissionLevel() {
        return 0;
      }

      @Override
      public String getName() {
        return "get_column";
      }
    }
  }
}