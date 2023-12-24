package org.potato.util.db;

public class Test_EntityTableTransformUtils {
	
    public static void main( String[] args ) {
    	
        System.out.println(EntityTableTransformUtils.fromEntityNameToTableName("SysUser"));
        System.out.println(EntityTableTransformUtils.fromEntityPropertyNameToTableColumnName("createTime"));
        System.out.println(EntityTableTransformUtils.fromEntityPropertyNameToGetMethodName("createTime"));
        System.out.println(EntityTableTransformUtils.fromEntityPropertyNameToSetMethodName("createTime"));
    }
}
