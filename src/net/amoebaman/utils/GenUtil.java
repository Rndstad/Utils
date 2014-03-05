package net.amoebaman.utils;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GenUtil {

    /**
     * Gets the {@link LivingEntity} truly responsible for the damage, if there is one.
     * This will trace back indirect damage to its source, including arrows to their
     * shooters, and wolves to their owners.
     * 
     * @param event the event in question
     * @return the true culprit, or null if the culprit is not a living entity
     */
    public static LivingEntity getTrueCulprit(EntityDamageByEntityEvent event){
    	Entity damager = event.getDamager();
        if(damager instanceof LivingEntity)
            return (LivingEntity) event.getDamager();
        else if(damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof LivingEntity)
            return (LivingEntity) ((Projectile) damager).getShooter();
        else if(damager instanceof Tameable && ((Tameable) damager).getOwner() instanceof LivingEntity)
        	return (LivingEntity) ((Tameable) damager).getOwner();
        else
            return null;
    }
	
	public static <E> E getRandomElement(Collection<E> set){
		E element = null;
		Iterator<E> it = set.iterator();
		for(int i = 0; i < Math.random() * set.size() && it.hasNext(); i++)
			element = it.next();
		return element;
	}
    
}
