package net.amoebaman.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Sets;

import net.amoebaman.amoebautils.AmoebaUtils;

import net.minecraft.util.com.google.common.collect.Lists;

@RunWith(PowerMockRunner.class)
public class GenUtilTest{
	
	public static Player mockPlayer(String name){
		Player player = Mockito.mock(Player.class);
		Mockito.when(player.getName()).thenReturn(name);
		return player;
	}
	
	@Ignore
	public void testGetConfigFile(){
		fail("Can't be implemented");
	}
	
	@Test
	public void testGetRandomElement(){
		Set set = Sets.newHashSet("I", "like", "cheese");
		assertTrue("set actually contains the random element selected", set.contains(AmoebaUtils.getRandomElement(set)));
	}
	
	@Test
	public void testObjectsToStrings(){
		List<Object> objects = Lists.newArrayList(
			"potato",
			" = ",
			new Object(){ public String toString(){ return "spud"; } }
		);
		
		assertEquals("potato = spud", AmoebaUtils.concat(objects));
	}
	
	@Test
	public void testPlayersToNames(){
		List<Player> players = Lists.newArrayList(mockPlayer("AmoebaMan"), mockPlayer("Kainzo"), mockPlayer("Dinnerbone"));
		List<String> names = AmoebaUtils.playersToNames(players);
		
		assertEquals("AmoebaMan, Kainzo, Dinnerbone", AmoebaUtils.concat(names, ", "));
	}
	
	@Test
	public void testConcat(){
		List<Object> list = Lists.newArrayList(mockPlayer("Pie"), "is", "mighty", "delicious!");
		String concat = AmoebaUtils.concat(list, "Bob said, \"", " ", "\"");
		
		assertEquals("Bob said, \"Pie is mighty delicious!\"", concat);
	}
	
	@Test
	public void testExpand(){
		Object original = Lists.newArrayList("I ", "am ", new String[]{"a ", "huge "}, Lists.newArrayList("fan ", "of ", new String[]{"both ", "pie "}, "and "), "cheese");
		List<Object> expanded = AmoebaUtils.expand(original, "!");
		
		assertEquals("I am a huge fan of both pie and cheese!", AmoebaUtils.concat(expanded));
	}
	
}
