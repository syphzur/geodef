package pl.polsl.bol.krzysztof.backend.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wololo.geojson.FeatureCollection;
import pl.polsl.bol.krzysztof.backend.converters.GpsDataListToFeatureCollectionConverter;
import pl.polsl.bol.krzysztof.backend.converters.SpatialObjectListToFeatureCollectionConverter;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsData;
import pl.polsl.bol.krzysztof.backend.models.entities.SpatialObject;
import pl.polsl.bol.krzysztof.backend.services.GpsDataService;
import pl.polsl.bol.krzysztof.backend.services.SpatialObjectService;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/geojson")
public class GeoJSONDataController {

    private final GpsDataService gpsDataService;

    private final SpatialObjectService spatialObjectService;

    private final GpsDataListToFeatureCollectionConverter gpsDataListToFeatureCollectionConverter;

    private final SpatialObjectListToFeatureCollectionConverter spatialObjectListToFeatureCollectionConverter;

    public GeoJSONDataController(final GpsDataService gpsDataService, final SpatialObjectService spatialObjectService,
                                 final GpsDataListToFeatureCollectionConverter gpsDataListToFeatureCollectionConverter,
                                 final SpatialObjectListToFeatureCollectionConverter spatialObjectListToFeatureCollectionConverter) {
        this.gpsDataService = gpsDataService;
        this.spatialObjectService = spatialObjectService;
        this.gpsDataListToFeatureCollectionConverter = gpsDataListToFeatureCollectionConverter;
        this.spatialObjectListToFeatureCollectionConverter = spatialObjectListToFeatureCollectionConverter;
    }

    @GetMapping("/gps-data/all")
    public ResponseEntity<FeatureCollection> getAllGpsData() {
        final List<GpsData> gpsDataList = this.gpsDataService.getAll();
        final FeatureCollection featureCollection = this.gpsDataListToFeatureCollectionConverter.convert(gpsDataList);
        return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

    @GetMapping(path = "/gps-data/all", params = "userUuid")
    public ResponseEntity<FeatureCollection> getAllByUser(@RequestParam final UUID userUuid,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateFrom,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateTo,
                                                          @RequestParam(required = false) final UUID trackerUuid) {
        final List<GpsData> dataList = this.gpsDataService.getAllByUser(userUuid, dateFrom, dateTo, trackerUuid);
        final FeatureCollection featureCollection = this.gpsDataListToFeatureCollectionConverter.convert(dataList);
        return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

    @GetMapping("/spatial-object/all")
    public ResponseEntity<FeatureCollection> getAllSpatialObjects() {
        final List<SpatialObject> spatialObjectList = this.spatialObjectService.getAll();
        final FeatureCollection featureCollection = this.spatialObjectListToFeatureCollectionConverter.convert(spatialObjectList);
        return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

    @GetMapping(path = "/spatial-object/all", params = "layerUuid")
    public ResponseEntity<FeatureCollection> getAllByLayer(@RequestParam final UUID layerUuid,
                                                           @RequestParam(defaultValue = "false") final boolean withHierarchicalLayers) {
        final List<SpatialObject> spatialObjectList = this.spatialObjectService.getAllByLayer(layerUuid, withHierarchicalLayers);
        final FeatureCollection featureCollection = this.spatialObjectListToFeatureCollectionConverter.convert(spatialObjectList);
        return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

    @GetMapping(path = "/spatial-object/all", params = "typeUuid")
    public ResponseEntity<FeatureCollection> getAllByType(@RequestParam final UUID typeUuid) {
        final List<SpatialObject> spatialObjectList = this.spatialObjectService.getAllByType(typeUuid);
        final FeatureCollection featureCollection = this.spatialObjectListToFeatureCollectionConverter.convert(spatialObjectList);
        return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

    @GetMapping(path = "/spatial-object/all", params = "creatorUuid")
    public ResponseEntity<FeatureCollection> getAllByCreator(@RequestParam final UUID creatorUuid) {
        final List<SpatialObject> spatialObjectList = this.spatialObjectService.getAllByCreator(creatorUuid);
        final FeatureCollection featureCollection = this.spatialObjectListToFeatureCollectionConverter.convert(spatialObjectList);
        return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

}
