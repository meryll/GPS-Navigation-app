package tech.hypermiles.hypermiles.Analysis;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import tech.hypermiles.hypermiles.Analysis.Utiilities.PointerToLocation;
import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Model.AnalysedRoad;
import tech.hypermiles.hypermiles.Model.AnalysedStep;
import tech.hypermiles.hypermiles.Model.Road;
import tech.hypermiles.hypermiles.Model.Step;

/**
 * Created by Asia on 2016-11-23.
 */

public class RoadAnalysis {

    private NavigationSingleton mNavigationSingleton;
    private Road mRoadToAnalyse;

    public RoadAnalysis()
    {
        mNavigationSingleton = NavigationSingleton.getInstance();
    }

    public AnalysedRoad analyzeNew(Road road)
    {
        Logger.i("TAG", "Zaczynamy analizowanie");
        mRoadToAnalyse = road;
        if(road.isNullOrEmpty()) {
            return null;
        }

        AnalysedRoad analysedRoad = getCurrentAnalysedRoad(road);

        Logger.wtf("ANALIZOWANIE", "Current step index "+mNavigationSingleton.getCurrentStepIndex());
        Logger.wtf("POBRANA", "Zwracane z funkcji "+road.getStartNodeOfTheLastLeg()+" ");
        Logger.wtf("POBRANA", "Rozmiar stepów w nowym analysed road "+analysedRoad.getStepsSize());

        int startNode = road.getStartNodeOfTheLastLeg();

//        for(int i=startNode; i<road.getStepsSize(); i++)
        {

            Step currentStep = road.getStep(startNode);
            AnalysedStep analysedStep = new AnalysedStep(currentStep);
            analysedStep.analyze();
            analysedRoad.addStep(analysedStep);
        }

        analysedRoad.createListOfAllThePointers();

        //todo to w jakimś innym miejscu
        mNavigationSingleton.setCurrentAnalysedRoad(analysedRoad);
        return analysedRoad;
    }

    public AnalysedStep analyzeSingle()
    {
        if(mRoadToAnalyse.isNullOrEmpty()) {
            return null;
        }

        AnalysedRoad analysedRoad = mNavigationSingleton.getCurrentAnalysedRoad();
        if(mRoadToAnalyse.getStepsSize()==analysedRoad.getStepsSize()) {
            return null;
        }

        int startNode = analysedRoad.getStepsSize()-1;
        Logger.i("analizka", "Analizujemy teraz "+startNode);

        Step currentStep = mRoadToAnalyse.getStep(startNode);
        AnalysedStep analysedStep = new AnalysedStep(currentStep);
        analysedStep.analyze();
        analysedRoad.addStep(analysedStep);

        analysedRoad.addToListOfAllThePointers(analysedStep);

        mNavigationSingleton.setCurrentAnalysedRoad(analysedRoad);
        return analysedStep;
    }

    private AnalysedRoad getCurrentAnalysedRoad(Road road)
    {
        AnalysedRoad analysedRoad = mNavigationSingleton.getCurrentAnalysedRoad();
        if(analysedRoad==null) {
            analysedRoad = new AnalysedRoad(road);
        } else {
            analysedRoad.update(road);
        }

        return analysedRoad;
    }

}
