package dst.ass3.elastic.impl;

import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import dst.ass3.elastic.ContainerException;
import dst.ass3.elastic.ContainerInfo;
import dst.ass3.elastic.ContainerNotFoundException;
import dst.ass3.elastic.IContainerService;
import dst.ass3.messaging.Region;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerService implements IContainerService {

    private DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .build();

    @Override
    public List<ContainerInfo> listContainers() throws ContainerException {
        try (var docker = DockerClientBuilder.getInstance(config).build()) {

            return docker.listContainersCmd().exec()
                    .stream()
                    .filter(c -> c.getImage().equals("dst/ass3-worker"))
                    .map(c -> {
                        var info = new ContainerInfo();
                        info.setContainerId(c.getId());
                        info.setImage(c.getImage());
                        info.setRunning(true);
                        System.out.println(c.getCommand());
                        info.setWorkerRegion(Region.valueOf(c.getCommand().split(" ")[2].toUpperCase()));
                        return info;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ContainerException("Unable to connect to the docker instance");
        }
    }

    @Override
    public void stopContainer(String containerId) throws ContainerException {
        try (var docker = DockerClientBuilder.getInstance(config).build()) {
            docker.stopContainerCmd(containerId).exec();
        } catch (IOException e) {
            throw new ContainerException("Unable to connect to the docker instance");
        } catch (NotFoundException e) {
            throw new ContainerNotFoundException(e.getMessage());
        }
    }

    @Override
    public ContainerInfo startWorker(Region region) throws ContainerException {
        try (var docker = DockerClientBuilder.getInstance(config).build()) {
            var container = docker
                    .createContainerCmd("dst/ass3-worker")
                    .withHostConfig(HostConfig
                            .newHostConfig()
                            .withAutoRemove(true)
                            .withNetworkMode("dst"))
                    .withCmd(region.toString().toLowerCase())
                    .exec();
            docker.startContainerCmd(container.getId()).exec();

            var info = new ContainerInfo();
            info.setWorkerRegion(region);
            info.setImage("dst/ass3-worker");
            info.setContainerId(container.getId());

            var inspect = docker.inspectContainerCmd(container.getId()).exec();

            try {
                info.setRunning(inspect.getState().getRunning());
            } catch (NullPointerException e) {
                info.setRunning(false);
            }
            return info;

        } catch (IOException e) {
            throw new ContainerException("Unable to connect to the docker instance");
        } catch (NotFoundException e) {
            throw new ContainerNotFoundException(e.getMessage());
        }
    }
}
