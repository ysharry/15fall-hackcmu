package org.hackcmu.helloworld;

/**
 * Created by Billy on 9/26/2015.
 */
public class CityPlans {
    private static int[] dis = {0,10000,15000,19500,24000};
    private static int[] bgs = {R.drawable.arctic_ocean, R.drawable.home_norway, R.drawable.home_london};
    private static int[] cities = {R.string.places_arctic_ocean, R.string.places_norway, R.string.places_london};
    private static int[] prim_colors = {R.color.text_arctic_primary, R.color.text_norway_primary, R.color.text_london_primary};
    private static int[] sec_colors = {R.color.text_arcitc_2nd, R.color.text_norway_2nd, R.color.text_london_2nd};
    private int currentSteps;
    private int currentLevel;

    public CityPlans(int currentSteps) {
        this.currentSteps = currentSteps;

        int i = 0;
        while(i < dis.length && currentSteps >= dis[i]){
            i++;
        }
        currentLevel = --i;
    }

    public int getCurrentBgImage() {
        return bgs[currentLevel];
    }

    public int getCurrentCityName() {
        return cities[currentLevel];
    }

    public int getCurrentPrimColor() {
        return prim_colors[currentLevel];
    }

    public int getCurrentSecColor() {
        return sec_colors[currentLevel];
    }

    public int getStepsTilNextCity() {
        return dis[currentLevel+1] - currentSteps;
    }

    public int getCurrentSteps() {
        return currentSteps;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
