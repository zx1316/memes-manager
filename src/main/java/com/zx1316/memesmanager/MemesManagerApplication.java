package com.zx1316.memesmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageIO;

@SpringBootApplication
public class MemesManagerApplication {

    public static void main(String[] args) {
        ImageIO.scanForPlugins();
        SpringApplication.run(MemesManagerApplication.class, args);
    }

}
