package net.amoebaman.utils.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public class ReflectionUtil {
	
	public static String getVersion(){
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1) + ".";
        return version;
	}

    public static Class<?> getNMSClass(String className) {
        String fullName = "net.minecraft.server." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        }
        catch (Exception e) { e.printStackTrace(); }
        return clazz;
    }
    
    public static Class<?> getOBCClass(String className){
        String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        }
        catch (Exception e) { e.printStackTrace(); }
        return clazz;
    }

    public static Object getHandle(Object obj) {
        try {
            return getMethod(obj.getClass(), "getHandle").invoke(obj);
        }
        catch (Exception e){
            e.printStackTrace(); 
            return null;
        }
    }

    public static Field getField(Class<?> cl, String field_name) {
        try {
        	Field field = cl.getDeclaredField(field_name);
        	field.setAccessible(true);
            return cl.getDeclaredField(field_name);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> cl, String method, Class<?>... args) {
        for (Method m : cl.getMethods()) 
            if (m.getName().equals(method) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))){
            	m.setAccessible(true);
                return m;
            }
        return null;
    }

    public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;
        if (l1.length != l2.length)
            return false;
        for (int i = 0; i < l1.length; i++)
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        return equal;
    }

}
