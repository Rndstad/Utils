package net.amoebaman.amoebautils.nms;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.tools.JavaFileManager.Location;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.junit.Ignore;
import org.junit.Test;

import net.amoebaman.amoebautils.nms.ReflectionUtil;

public class ReflectionUtilTest{
	
	@Ignore
	public void testGetVersion(){
		fail("Can't be implemented");
	}
	
	@Ignore
	public void testGetNMSClass(){
		fail("Can't be implemented");
	}
	
	@Ignore
	public void testGetOBCClass(){
		fail("Can't be implemented");
	}
	
	@Ignore
	public void testGetHandle(){
		fail("Can't be implemented");
	}
	
	@Test
	public void testGetField(){
		Field field = ReflectionUtil.getField(MaterialData.class, "type");
		assertNotNull(field);
	}
	
	@Test
	public void testGetMethod(){
		Method method = ReflectionUtil.getMethod(Player.class, "getName");
		assertNotNull(method);
	}
	
	@Test
	public void testClassListEqual(){
		assertTrue(ReflectionUtil.classListEqual(new Class<?>[]{int.class, String.class, Player.class}, new Class<?>[]{int.class, String.class, Player.class}));
		assertFalse(ReflectionUtil.classListEqual(new Class<?>[]{World.class, Location.class, Player.class}, new Class<?>[]{World.class, Player.class, Location.class}));
	}
	
}
