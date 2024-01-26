package com.team1816.lib.hardware;


import com.team1816.lib.hardware.factory.YamlConfig;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.YAMLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RobotYamlTests {

    @Test
    public void defaultYamlTest() {
        loadConfig("default");
    }

    @Test
    public void zodiacProYamlTest() {
        loadConfig("zodiac_pro");
    }

    @Test
    public void seasonYamlTest() {
        loadConfig("zen"); //TODO update this every season!
    }

    @Test
    public void alphaYamlTest() {
        loadConfig("alpha");
    }
    @Test
    public void braveYamlTest() {
        loadConfig("bravo");
    }

    private void loadConfig(String configName) {
        RobotConfiguration config = null;
        try {
            config =
                YamlConfig.loadFrom(
                    this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("yaml/" + configName + ".config.yml")
                );
        } catch (YAMLException e) {
            e.printStackTrace();
        }
        assertNotNull(config);
        System.out.println(config);
    }
}
