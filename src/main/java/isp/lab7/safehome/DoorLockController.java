package isp.lab7.safehome;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoorLockController implements ControllerInterface {
        private List<AccessLog> accessLogList = new ArrayList<AccessLog>();

        private List<Tenant> tenantList = new ArrayList<Tenant>();

        private List<AccessKey> accessKeyList = new ArrayList<AccessKey>() ;

        private Map<Tenant,AccessKey> validAccess;

        private Door door;

        public int attemptsNumber;

         public DoorLockController(Door door){
             this.door=door;
             this.validAccess = new HashMap<>();
             this.attemptsNumber=0;
    }
        @Override
        public DoorStatus enterPin(String pin) throws InvalidPinException,TooManyAttemptsException {
             if(attemptsNumber<2) {
                 if (checkPin(pin, validAccess)) {
                     if (door.getStatus().equals(DoorStatus.OPEN)) {
                         door.lockDoor();
                         System.out.println(door.getStatus().toString());
                     } else {
                         door.unlockDoor();
                         System.out.println(door.getStatus().toString());
                     }
                 } else {
                     door.lockDoor();
                     attemptsNumber++;
                     throw new InvalidPinException(pin, "Invalid pin!");
                 }
             }
             else {
                 if (attemptsNumber == 2) {
                     if (!pin.equals(ControllerInterface.MASTER_KEY)) {
                         door.unlockDoor();
                         throw new TooManyAttemptsException(pin, "Too many attempts! Enter master key to unlock the door");
                     }
                     else{
                         if(pin.equals(ControllerInterface.MASTER_KEY)){
                             door.unlockDoor();
                             attemptsNumber=0;
                         }
                     }
                 }
             }
            return door.getStatus();
        }
        public boolean checkPin(String pin,Map<Tenant,AccessKey> validAccess) {
            for (Map.Entry<Tenant, AccessKey> map : validAccess.entrySet()) {
                if (pin.equals(map.getValue().getPin()))
                    return true;
            }
            return false;
        }
    /**
     * this method verify if exists a tenant in the @param tenantList by @param name
     * @param name the name of the tenant that is search
     * @param tenantList the list where we search
     * @return true if there is a tenant whit the @param name or false if there is not
     */
        public boolean existsTenant(String name,List<Tenant> tenantList){
            for(Tenant t: tenantList){
                if(t.getName().equals(name))
                    return true;
            }
            return false;
        }
        @Override
        public void addTenant(String pin, String name) throws TenantAlreadyExistsException {
                if (existsTenant(name,tenantList)) {
                    accessLogList.add(new AccessLog(name, LocalDateTime.now(),"addTenant",door.getStatus(),"Tenant already exists in the map"));
                    throw new TenantAlreadyExistsException(name, "Tenant already exists in the map");
            }
                else {
                    tenantList.add(new Tenant(name));
                    accessKeyList.add(new AccessKey(pin));
                    validAccess.put(new Tenant(name),new AccessKey(pin));
                    ///door.lockDoor();
                    accessLogList.add(new AccessLog(name, LocalDateTime.now(), "AddTenant", door.getStatus(), "No error"));
                }
            }

        @Override
        public void removeTenant(String name) throws TenantNotFoundException {
                if(existsTenant(name,tenantList)){
                    validAccess.entrySet().removeIf(t->t.getKey().getName().equals(name));
                    tenantList.removeIf(t -> t.getName().equals(name));
                    door.unlockDoor();
                    accessLogList.add(new AccessLog(name,LocalDateTime.now(),"removeTenant",door.getStatus(),"no error"));
                }
                else{
                    door.lockDoor();
                    accessLogList.add(new AccessLog(name,LocalDateTime.now(),"removeTenant",door.getStatus(),"Tenant not found"));
                    throw new TenantNotFoundException(name,"Tenant not found");
                }
        }

        public List<AccessLog> getAccessLogs(){
               return accessLogList;
        }
}
