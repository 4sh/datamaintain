package datamaintain.samples;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Starter {
    private String name;
    private String type;
    private int hp;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;

    public Starter(ResultSet resultSet) throws SQLException {
        this.name = resultSet.getString("name");
        this.type = resultSet.getString("type");
        this.hp = resultSet.getInt("hp");
        this.attack = resultSet.getInt("attack");
        this.defense = resultSet.getInt("defense");
        this.specialAttack = resultSet.getInt("special");
        this.specialDefense = resultSet.getInt("defense");
        this.speed = resultSet.getInt("speed");
    }

    public String getName() {
        return name;
    }

    public Starter setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public Starter setType(String type) {
        this.type = type;
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
                ", type=" + type +
                ", hp=" + hp +
                ", attack=" + attack +
                ", defense=" + defense +
                ", specialAttack=" + specialAttack +
                ", specialDefense=" + specialDefense +
                ", speed=" + speed +
                '}';
    }
}
