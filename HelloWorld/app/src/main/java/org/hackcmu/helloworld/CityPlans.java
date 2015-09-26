package org.hackcmu.helloworld;

/**
 * Created by Billy on 9/26/2015.
 */
public class CityPlans {
    private static int[] dis = {0,5000,10000,15000,19500,24000};
    private static int[] bgs = {R.drawable.arctic_ocean};
    private static int[] cities = {R.string.places_arctic_ocean};
    private int currentSteps;
    private int currentLevel;

    public CityPlans(int currentSteps) {
        this.currentSteps = currentSteps;

        int i = 0;
        while(i < dis.length && currentSteps >= dis[i]){
            i++;
        }
        currentLevel = i--;
    }

    public int getCurrentBgImage() {
        return bgs[currentLevel];
    }

    public int getCurrentCityName() {
        return cities[currentLevel];
    }
}
