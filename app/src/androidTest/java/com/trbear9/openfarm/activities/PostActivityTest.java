package com.trbear9.ui.activities;

import com.trbear9.plants.PlantClient;
import com.trbear9.plants.parameters.blob.Plant;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PostActivityTest extends TestCase {
    private static final Log log = LogFactory.getLog(PostActivityTest.class);

    public void testGetGistUrl(){
        PlantClient client = new PlantClient(new String[]{"aiusdasndhais"}, 192399);
        client.addProvider("TrainingBear", "84d0e105aaabce26c8dfbaff74b2280e");
        client.addProvider("Github", "https://api.github.com");
        client.addProvider("Github", "https://api.github.com");
        client.addProvider("Github", "https://api.github.com");
//        String url = client.getUrl()
//        log.info(url);
    }

    public void testGetResponse() {
    }
}