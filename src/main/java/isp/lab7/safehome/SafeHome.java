package isp.lab7.safehome;

public class SafeHome {

    public static void main(String[] args) throws Exception {
        Door door = new Door(DoorStatus.CLOSE);
        DoorLockController dr1 = new DoorLockController(door);
        ControllerInterface ctrl1;
        dr1.addTenant("123","User1");
        dr1.addTenant("124","b");
        dr1.addTenant("125","a");
        dr1.addTenant("123","User1");
        dr1.removeTenant("User1");
        dr1.enterPin("123");
        System.out.println(dr1.getAccessLogs().toString());

    }
}
