package com.york.doghealthtracker.service;

import com.york.doghealthtracker.model.DentalRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service responsible for calculating the health index for given topic.
 */
@Service
@Log4j2
public class HealthIndexService {

    //Dental request
    /**
     * Calculates the dental index of the dog based on the provided data.
     *
     * @param request
     * @return
     */
    private int calculateSeverityScore(DentalRequest request) {

        int initialSeverityScore = 10;

        // TODO: calculate the severity score -- the teeth INDEX
        switch (request.getPlaqueStatus()) {
            case HI -> initialSeverityScore = initialSeverityScore - 5;
            case NORM -> initialSeverityScore = initialSeverityScore - 1;
        }

        if (initialSeverityScore <= 0) {
            return 1;
        }
        /*
         request.getPlaqueStatus() == PlaqueStatus.HI
         request.getPlaqueStatus() == PlaqueStatus.NORM
         request.getPlaqueStatus() == PlaqueStatus.LOW

         request.getToothLoss() == true
         request.getToothLoss() == false

         request.getGingivitisStatus() == GingivitisStatus.SEVERE
         request.getGingivitisStatus() == GingivitisStatus.MILD
         request.getGingivitisStatus() == GingivitisStatus.NONE
         */

        return 0;
    }

}
