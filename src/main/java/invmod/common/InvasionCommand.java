package invmod.common;

import invmod.client.render.RenderIMZombie;
import invmod.common.entity.EntityIMCreeper;
import invmod.common.entity.EntityIMLiving;
import invmod.common.entity.EntityIMMob;
import invmod.common.entity.EntityIMPigEngy;
import invmod.common.entity.EntityIMPrimedTNT;
import invmod.common.entity.EntityIMZombie;
import invmod.common.entity.ai.EntityAIAttackNexus;
import invmod.common.entity.ai.EntityAIGoToNexus;
import invmod.common.entity.ai.EntityAIKillEntity;
import invmod.common.entity.ai.EntityAISimpleTarget;
import invmod.common.entity.ai.EntityAITargetOnNoNexusPath;
import invmod.common.entity.ai.EntityAITargetRetaliate;
import invmod.common.entity.ai.EntityAIWaitForEngy;
import invmod.common.entity.ai.EntityAIWanderIM;

import java.util.HashMap;
import java.util.Iterator;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;



public class InvasionCommand extends CommandBase {
	
	public void processCommand(ICommandSender sender, String[] args) {
		String username = sender.getCommandSenderName();
		if ((args.length > 0) && (args.length <= 7)) {
			if (args[0].equals("help")) {
				sender.addChatMessage(new ChatComponentText("--- Showing Invasion help page 1 of 1 ---").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
				sender.addChatMessage(new ChatComponentText("/begin x to start a wave"));
				sender.addChatMessage(new ChatComponentText("/end to end the invasion"));
				sender.addChatMessage(new ChatComponentText("/range x to set the spawn range"));
			} else if (args[0].equals("begin")) {
				if (args.length == 2) {
					int startWave = Integer.parseInt(args[1]);
					if (mod_Invasion.getFocusNexus() != null) {
						mod_Invasion.getFocusNexus().debugStartInvaion(startWave);
					}
				}
			} else if (args[0].equals("end")) {
				if (mod_Invasion.getActiveNexus() != null) {
					mod_Invasion.getActiveNexus().emergencyStop();
					mod_Invasion.broadcastToAll(username + " ended invasion");
				} else {
					sender.addChatMessage(new ChatComponentText(username + ": No invasion to end"));
				}
			} else if (args[0].equals("range")) {
				if (args.length == 2) {
					int radius = Integer.parseInt(args[1]);
					if (mod_Invasion.getFocusNexus() != null) {
						if ((radius >= 32) && (radius <= 128)) {
							if (mod_Invasion.getFocusNexus().setSpawnRadius(radius)) {
								sender.addChatMessage(new ChatComponentText("Set nexus range to " + radius));
							} else {
								sender.addChatMessage(new ChatComponentText(username + ": Can't change range while nexus is active"));
							}
						} else {
							sender.addChatMessage(new ChatComponentText(username + ": Range must be between 32 and 128"));
						}
					} else {
						sender.addChatMessage(new ChatComponentText(username + ": Right-click the nexus first to set target for command"));
					}
				}
			} else if (args[0].equals("spawnertest")) {
				int startWave = 1;
				int endWave = 11;

				if (args.length >= 4)
					return;
				if (args.length >= 3)
					endWave = Integer.parseInt(args[2]);
				if (args.length >= 2) {
					startWave = Integer.parseInt(args[1]);
				}
				Tester tester = new Tester();
				tester.doWaveSpawnerTest(startWave, endWave);
			} else if (args[0].equals("pointcontainertest")) {
				Tester tester = new Tester();
				tester.doSpawnPointSelectionTest();
			} else if (args[0].equals("wavebuildertest")) {
				float difficulty = 1.0F;
				float tierLevel = 1.0F;
				int lengthSeconds = 160;

				if (args.length >= 5)
					return;
				if (args.length >= 4)
					lengthSeconds = Integer.parseInt(args[3]);
				if (args.length >= 3)
					tierLevel = Float.parseFloat(args[2]);
				if (args.length >= 2) {
					difficulty = Float.parseFloat(args[1]);
				}
				Tester tester = new Tester();
				tester.doWaveBuilderTest(difficulty, tierLevel, lengthSeconds);
			} else if (args[0].equals("nexusstatus")) {
				if (mod_Invasion.getFocusNexus() != null)
					mod_Invasion.getFocusNexus().debugStatus();
			} else if (args[0].equals("bolt")) {
				if (mod_Invasion.getFocusNexus() != null) {
					int x = mod_Invasion.getFocusNexus().getXCoord();
					int y = mod_Invasion.getFocusNexus().getYCoord();
					int z = mod_Invasion.getFocusNexus().getZCoord();
					int time = 40;
					if (args.length >= 6)
						return;
					if (args.length >= 5)
						time = Integer.parseInt(args[4]);
					if (args.length >= 4)
						z += Integer.parseInt(args[3]);
					if (args.length >= 3)
						y += Integer.parseInt(args[2]);
					if (args.length >= 2) {
						x += Integer.parseInt(args[1]);
					}
					mod_Invasion.getFocusNexus().createBolt(x, y, z, time);
				}
			} else if (args[0].equals("tnt")) {
				 World world = sender.getEntityWorld();
				 ChunkCoordinates coordinates =sender.getPlayerCoordinates();
				 
				 EntityIMPrimedTNT tnt = new EntityIMPrimedTNT(world);
				 tnt.setLocationAndAngles(coordinates.posX, coordinates.posY, coordinates.posZ, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);	  
				 tnt.fuse=100;
				 world.spawnEntityInWorld(tnt);
			} else if (args[0].equals("testing")) {
//				 HashMap<Class<Entity>, String> classToStringMapping = (HashMap<Class<Entity>, String>) EntityList.classToStringMapping;
//				 Iterator entitylist = classToStringMapping.values().iterator();
//				 World world = sender.getEntityWorld();
//				 ChunkCoordinates coordinates =sender.getPlayerCoordinates();
//				  
//				 while(entitylist.hasNext())
//				 {
//					 String naam = (String) entitylist.next();
//					
//					 
//					 
//					 
//					  if(naam.contains("Blaze"))
//					  {
//						  System.out.println(naam+" detected");
//						  sender.addChatMessage(new ChatComponentText(naam+" detected"));
//						  
//						  Entity entity = EntityList.createEntityByName(naam, world);
//						  entity.setLocationAndAngles(coordinates.posX, coordinates.posY, coordinates.posZ, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);	  
//						  System.out.println(entity.getClass().toString());
//						  EntityLiving mob = (EntityLiving)entity;
//						  mob.tasks.taskEntries.clear();
//						//added entityaiswimming and increased all other tasksordernumers with 1
////						//  mob.tasks = new EntityAITasks(mob.worldObj.theProfiler);
////						  mob.tasks.addTask(0, new EntityAISwimming(mob));
////						  mob.tasks.addTask(1, new EntityAIKillEntity((EntityIMLiving)mob, EntityPlayer.class, 40));
////						  mob.tasks.addTask(2, new EntityAIAttackNexus((EntityIMLiving)mob));
////						  mob.tasks.addTask(3, new EntityAIWaitForEngy((EntityIMLiving)mob, 4.0F, true));
////						  mob.tasks.addTask(4, new EntityAIKillEntity((EntityIMLiving)mob, EntityLiving.class, 40));
////						  mob.tasks.addTask(5, new EntityAIGoToNexus((EntityIMMob)mob));
////						  mob.tasks.addTask(6, new EntityAIWanderIM((EntityIMLiving)mob));
////						  mob.tasks.addTask(7, new EntityAIWatchClosest(mob, EntityPlayer.class, 8.0F));
////						  mob.tasks.addTask(8, new EntityAIWatchClosest(mob, EntityIMCreeper.class, 12.0F));
////						  mob.tasks.addTask(8, new EntityAILookIdle(mob));
////							
////						  mob.targetTasks.taskEntries.clear();
////						 // mob.targetTasks = new EntityAITasks(mob.worldObj.theProfiler);
////						  mob.targetTasks.addTask(0, new EntityAITargetRetaliate((EntityIMLiving)mob, EntityLiving.class, 12.0F));
////						  mob.targetTasks.addTask(2, new EntityAISimpleTarget((EntityIMLiving)mob, EntityPlayer.class, 12.0F, true));
////						  mob.targetTasks.addTask(5, new EntityAIHurtByTarget((EntityIMLiving)mob, false));
//						  mob.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
//						  world.spawnEntityInWorld(mob);
//					  }else{
//						  System.out.println(naam);
//						  sender.addChatMessage(new ChatComponentText(naam));
//					  }
//				 }
			}else{
				sender.addChatMessage(new ChatComponentText("Command not recognised, use /invasion help for a list of all the available commands").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
			
			}
				
		}
	}

	public String getCommandName() {
		return "invasion";
	}

	public String getCommandUsage(ICommandSender icommandsender) {
		return "/invasion help for invasion commands";
	}
}