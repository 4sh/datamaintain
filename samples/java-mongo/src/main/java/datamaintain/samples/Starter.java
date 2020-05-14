package datamaintain.samples;

import java.util.List;

public class Starter {
    private String name;
    private List<String> types;
    private int hp;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;

    public String getName() {
        return name;
    }

    public Starter setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getTypes() {
        return types;
    }

    public Starter setTypes(List<String> types) {
        this.types = types;
        return this;
    }

    public int getHp() {
        return hp;
    }

    public Starter setHp(int hp) {
        this.hp = hp;
        return this;
    }

    public int getAttack() {
        return attack;
    }

    public Starter setAttack(int attack) {
        this.attack = attack;
        return this;
    }

    public int getDefense() {
        return defense;
    }

    public Starter setDefense(int defense) {
        this.defense = defense;
        return this;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public Starter setSpecialAttack(int specialAttack) {
        this.specialAttack = specialAttack;
        return this;
    }

    public int getSpecialDefense() {
        return specialDefense;
    }

    public Starter setSpecialDefense(int specialDefense) {
        this.specialDefense = specialDefense;
        return this;
    }

    public int getSpeed() {
        return speed;
    }

    public Starter setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    @Override
    public String toString() {
        return "Starter{" +
                "name='" + name + '\'' +
                ", types=" + types +
                ", hp=" + hp +
                ", attack=" + attack +
                ", defense=" + defense +
                ", specialAttack=" + specialAttack +
                ", specialDefense=" + specialDefense +
                ", speed=" + speed +
                '}';
    }
}
