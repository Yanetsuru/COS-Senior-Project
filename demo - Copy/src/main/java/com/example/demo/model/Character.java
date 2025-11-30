package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private int level;
    private int hp;
    private int xp;
    private String species;
    private String background;
    private String dndclass;
    private int strength;
    private int dexterity;
    private int intelligence;
    private int constitution;
    private int wisdom;
    private int charisma;

    public Character() {}

    public Character(String name, int age, int level, int hp, int xp, String species, String background, String dndclass,
                     int strength, int dexterity, int intelligence, int constitution, int wisdom, int charisma) {
        this.name = name;
        this.age = age;
        this.level = level;
        this.hp = hp;
        this.xp = xp;
        this.species = species;
        this.background = background;
        this.dndclass = dndclass;
        this.strength = strength;
        this.dexterity = dexterity;
        this.intelligence = intelligence;
        this.constitution = constitution;
        this.wisdom = wisdom;
        this.charisma = charisma;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getDndclass() {
        return dndclass;
    }

    public void setDndclass(String dndclass) {
        this.dndclass = dndclass;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getConstitution() {
        return constitution;
    }

    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }

    public int getWisdom() {
        return wisdom;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
}
