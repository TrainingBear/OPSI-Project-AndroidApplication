package com.tbear9.openfarm.activities;

import com.trbear9.plants.PlantClient;
import com.trbear9.plants.api.Plant;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PostActivityTest extends TestCase {
    private static final Log log = LogFactory.getLog(PostActivityTest.class);

    public void testGetGistUrl(){
        PlantClient.addProvider("TrainingBear", "84d0e105aaabce26c8dfbaff74b2280e");
        PlantClient.addProvider("Github", "https://api.github.com");
        PlantClient.addProvider("Github", "https://api.github.com");
        PlantClient.addProvider("Github", "https://api.github.com");
        String url = PlantClient.url;
        log.info(url);
    }

    public void testGetResponse() {
    }
}