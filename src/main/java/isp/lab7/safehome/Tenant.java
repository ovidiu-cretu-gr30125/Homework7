package isp.lab7.safehome;

public class Tenant {
    private String name;

    public Tenant(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return "Tenant{" +
                "name='" + name + '\'' +
                '}';
    }
}
