package dst.ass3.elastic.impl;

import dst.ass3.elastic.ContainerException;
import dst.ass3.elastic.IContainerService;
import dst.ass3.elastic.IElasticityController;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;

import java.util.Map;
import java.util.stream.Collectors;

public class ElasticityController implements IElasticityController {

    private final IContainerService containerService;
    private final IWorkloadMonitor monitor;

    public ElasticityController(IContainerService containerService, IWorkloadMonitor monitor) {
        this.containerService = containerService;
        this.monitor = monitor;
    }

    static private class RegionInfo {
        public Long workerCount;
        public Long requestCount;
        public Double avgTime;
        public Long maxWaittime;

        public RegionInfo(Long workerCount, Long requestCount, Double avgTime, Long maxWaittime) {
            this.workerCount = workerCount;
            this.requestCount = requestCount;
            this.avgTime = avgTime;
            this.maxWaittime = maxWaittime;
        }
    }

    private Long calculateOptimalWorkercount(RegionInfo info) {
        double scaleOutThreshold = 0.1;
        double scaleDownThreshold = 0.05;


        var optimalWorkers = info.workerCount;
        double linearWaitingTime = info.requestCount * info.avgTime;
        double exp = linearWaitingTime / optimalWorkers;

        // FIXME: If that is correct than we only need to return an enum
        if (exp > info.maxWaittime * (1 + scaleOutThreshold)) {
            while ((linearWaitingTime / optimalWorkers) > (info.maxWaittime * (1 + scaleOutThreshold)))
                optimalWorkers++;
        } else if (exp < info.maxWaittime * (1 - scaleDownThreshold)) {
            while ((linearWaitingTime / optimalWorkers) < (info.maxWaittime * (1 - scaleDownThreshold)))
                optimalWorkers--;
        }

        return optimalWorkers;
    }

    @Override
    public void adjustWorkers() throws ContainerException {
        var workerCounts = monitor.getWorkerCount();
        var requestCounts = monitor.getRequestCount();
        var avgTimes = monitor.getAverageProcessingTime();
        var maxWaitTimesMS = Map.of(
                Region.AT_VIENNA, 30L * 1000,
                Region.AT_LINZ, 30L * 1000,
                Region.DE_BERLIN, 120L * 1000
        );

        for (var r : Region.values()) {
            var info = new RegionInfo(
                    workerCounts.getOrDefault(r, 0L),
                    requestCounts.getOrDefault(r, 0L),
                    avgTimes.getOrDefault(r, 0.0),
                    maxWaitTimesMS.get(r)
            );

            var optimalWorkerCount = calculateOptimalWorkercount(info);
            var delta = optimalWorkerCount - info.workerCount;
            if (delta > 0) {
                // Spawn workers
                for (int i = 0; i < delta; i++) {
                    containerService.startWorker(r);
                }

            } else if (delta < 0) {
                // Remove workers
                var regionContainers = containerService
                        .listContainers()
                        .stream()
                        .filter(c -> c.getWorkerRegion() == r)
                        .collect(Collectors.toList());

                for (int i = 0; i < -delta; i++) {
                    containerService.stopContainer(regionContainers.get(i).getContainerId());
                }
            }
        }
    }
}
