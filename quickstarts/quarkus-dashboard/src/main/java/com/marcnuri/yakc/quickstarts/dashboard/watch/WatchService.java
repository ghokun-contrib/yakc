/*
 * Copyright 2020 Marc Nuri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.marcnuri.yakc.quickstarts.dashboard.watch;

import com.marcnuri.yakc.api.WatchEvent;
import com.marcnuri.yakc.model.Model;
import com.marcnuri.yakc.quickstarts.dashboard.configmaps.ConfigMapService;
import com.marcnuri.yakc.quickstarts.dashboard.deployment.DeploymentService;
import com.marcnuri.yakc.quickstarts.dashboard.events.EventService;
import com.marcnuri.yakc.quickstarts.dashboard.node.NodeService;
import com.marcnuri.yakc.quickstarts.dashboard.pod.PodService;
import com.marcnuri.yakc.quickstarts.dashboard.replicaset.ReplicaSetService;
import com.marcnuri.yakc.quickstarts.dashboard.statefulsets.StatefulSetService;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

@Singleton
public class WatchService {

  private static final Logger LOG = LoggerFactory.getLogger(WatchService.class);

  private final ConfigMapService configMapService;
  private final DeploymentService deploymentService;
  private final EventService eventService;
  private final NodeService nodeService;
  private final PodService podService;
  private final ReplicaSetService replicaSetService;
  private final StatefulSetService statefulSetService;

  @Inject
  public WatchService(
    ConfigMapService configMapService,
    DeploymentService deploymentService,
    EventService eventService, NodeService nodeService,
    PodService podService,
    ReplicaSetService replicaSetService,
    StatefulSetService statefulSetService) {
    this.configMapService = configMapService;
    this.deploymentService = deploymentService;
    this.eventService = eventService;
    this.nodeService = nodeService;
    this.podService = podService;
    this.replicaSetService = replicaSetService;
    this.statefulSetService = statefulSetService;
  }

  public Observable<WatchEvent<? extends Model>> getWatch() throws IOException {
    return Observable.merge(Arrays.asList(
      eventService.getEvents().subscribeOn(Schedulers.newThread()),
      configMapService.watch().subscribeOn(Schedulers.newThread()),
      deploymentService.getDeployments().subscribeOn(Schedulers.newThread()),
      nodeService.getNodes().subscribeOn(Schedulers.newThread()),
      podService.getPods().subscribeOn(Schedulers.newThread()),
      replicaSetService.getReplicaSets().subscribeOn(Schedulers.newThread()),
      statefulSetService.getStatefulSets().subscribeOn(Schedulers.newThread())
    ))
      .doOnError(e -> LOG.error("Exception on Watcher", e))
      .onErrorReturn(DashboardError::watchEvent);
  }
}
