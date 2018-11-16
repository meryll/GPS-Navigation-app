package tech.hypermiles.hypermiles.Model;

import java.util.ArrayList;
import java.util.List;

import tech.hypermiles.hypermiles.Analysis.Utiilities.PointerToLocation;
import tech.hypermiles.hypermiles.Middleware.Logger;

/**
 * Created by Asia on 2017-04-20.
 */

public class AnalysedRoad extends  Road {

    private static final String TAG = "AnalysedRoad";
    private List<AnalysedStep> mSteps;
    private List<PointerToLocation> mListOfAllThePointers;

    public AnalysedRoad(Road road)
    {
        this.mStatus = road.mStatus;
        this.mLength = road.mLength;
        this.mDuration = road.mDuration;
        this.decodedRoute = road.decodedRoute;
        this.mLegs = road.mLegs;
        this.mSteps = new ArrayList<>();
    }

    public void update(Road road)
    {
        this.mStatus = road.mStatus;
        this.mLength = road.mLength;
        this.mDuration = road.mDuration;
        this.decodedRoute = road.decodedRoute;

        //removing analysed steps witch will be differen now
        removeChangedSteps(road);
    }

    private void removeChangedSteps(Road road)
    {
        try {
            if(mSteps==null) {
                mSteps = new ArrayList<>();
                return;
            };
            int index = road.getStartNodeOfTheLastLeg();
            Logger.i(TAG, "Chcemy usunac od indeksu "+index+" a rozmiar stepow wynosi "+mSteps.size());
            mSteps.subList(index, mSteps.size()).clear();
        } catch(Exception e) {
            Logger.wtf(TAG, e.getMessage());
        }
    }

    public AnalysedStep getStep(int i)
    {
        try {
            return this.mSteps.get(i);
        } catch (Exception e) {
            Logger.i(TAG, "Step with index "+i+" does not exist");
            return null;
        }
    }

    public List<PointerToLocation> getListOfAllThePointers()
    {
        return mListOfAllThePointers;
    }


    public void addStep(AnalysedStep step)
    {
        this.mSteps.add(step);
    }

    public int getStepsSize()
    {
        if(mSteps == null) return 0;
        return mSteps.size();
    }

    public void createListOfAllThePointers() {

        mListOfAllThePointers = new ArrayList<>();

        for (int i = 0; i < getStepsSize(); i++) {
            Step step = getStep(i);

            for (int j = 0; j < step.moreDetailedPoly.size(); j++) {
                PointerToLocation pointer = new PointerToLocation(step.moreDetailedPoly.get(j), i, j);
                mListOfAllThePointers.add(pointer);
            }
        }
    }

    //todo brzydko
    public void addToListOfAllThePointers(AnalysedStep step) {

        int stepIndex = getStepsSize()-1;

        for (int j = 0; j < step.moreDetailedPoly.size(); j++) {
            PointerToLocation pointer = new PointerToLocation(step.moreDetailedPoly.get(j), stepIndex, j);
            mListOfAllThePointers.add(pointer);
        }
    }


    public Boolean stepExists()
    {
        return this.mSteps!=null && this.mSteps.size()>0;
    }
}
