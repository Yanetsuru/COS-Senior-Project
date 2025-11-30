package com.example.demo.service;
import com.example.demo.model.Character;
import org.springframework.stereotype.Service;

@Service
public class CharacterFactory {

    public static Character createDefaultCharacter() {
        Character character = new Character();
        character.setStrength(10);
        character.setDexterity(10);
        character.setIntelligence(10);
        character.setConstitution(10);
        character.setWisdom(10);
        character.setCharisma(10);
        character.setXp(0);
        character.setAge(20);
        character.setLevel(1);
        character.setHp(character.getLevel() * 10);
        return character;
    }
}
