package uq.deco2800.coaster.game.modes;

import uq.deco2800.coaster.game.entities.buildings.Building;
import uq.deco2800.coaster.game.entities.buildings.traps.AcidTrap;
import uq.deco2800.coaster.game.entities.buildings.turrets.Cannon;
import uq.deco2800.coaster.game.entities.buildings.turrets.MachineGun;
import uq.deco2800.coaster.game.world.World;

public class BuildMode {
	private static BuildMode instance = new BuildMode();
	private BuildMode() {
		
	}
	
	public static BuildMode getInstance() {
		return instance;
	}
	
	public void build(BuildingList buildingID, float x, float y) {
		System.out.println(buildingID + ": " + x + ", " + y);
		Building building = getBuilding(buildingID);
		building.setPosition(x, y);
		World.getInstance().addEntity(building);
	}
	
	public Building getBuilding(BuildingList buildingID) {
		Building building;
		switch (buildingID) {
			case MACHINE_GUN:
				building = new MachineGun();
				break;
			case CANNON:
				building = new Cannon();
				break;
			case ACID_TRAP:
				building = new AcidTrap();
				break;
			default:
				building = new MachineGun();
				break;
		}
		return building;
	}
	
	public String getBuildingName(BuildingList buildingID) {
		return getBuilding(buildingID).getName();
	}

	public void build(BuildingList buildingID, double x, double y) {
		build(buildingID, (float) x, (float) y);
	}

}
