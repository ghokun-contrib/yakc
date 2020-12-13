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
 * Created on 2020-12-12, 9:06
 */
package com.marcnuri.yakc.quickstarts.dashboard.clusterrolebindings;

import com.marcnuri.yakc.KubernetesClient;
import com.marcnuri.yakc.api.WatchEvent;
import com.marcnuri.yakc.api.rbacauthorization.v1.RbacAuthorizationV1Api;
import com.marcnuri.yakc.api.rbacauthorization.v1.RbacAuthorizationV1Api.ListClusterRoleBinding;
import com.marcnuri.yakc.api.rbacauthorization.v1beta1.RbacAuthorizationV1beta1Api;
import com.marcnuri.yakc.model.io.k8s.api.rbac.v1.ClusterRoleBinding;
import com.marcnuri.yakc.model.io.k8s.api.rbac.v1.RoleRef;
import com.marcnuri.yakc.model.io.k8s.api.rbac.v1.Subject;
import com.marcnuri.yakc.model.io.k8s.apimachinery.pkg.apis.meta.v1.Status;
import com.marcnuri.yakc.quickstarts.dashboard.watch.Watchable;
import io.reactivex.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.marcnuri.yakc.quickstarts.dashboard.ClientUtil.ignoreForbidden;
import static com.marcnuri.yakc.quickstarts.dashboard.ClientUtil.tryWithFallback;

@Singleton
public class ClusterRoleBindingService implements Watchable<ClusterRoleBinding> {

  private final KubernetesClient kubernetesClient;

  @Inject
  public ClusterRoleBindingService(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  @Override
  public Optional<Observable<WatchEvent<ClusterRoleBinding>>> watch() throws IOException {
    return ignoreForbidden(
      () -> tryWithFallback(
        () -> {
          kubernetesClient.create(RbacAuthorizationV1Api.class).listClusterRoleBinding(new ListClusterRoleBinding().limit(1)).get();
          return Optional.of(kubernetesClient.create(RbacAuthorizationV1Api.class).listClusterRoleBinding().watch());
        },
        () -> {
          kubernetesClient.create(RbacAuthorizationV1beta1Api.class)
            .listClusterRoleBinding(new RbacAuthorizationV1beta1Api.ListClusterRoleBinding().limit(1)).get();
          return Optional.of(kubernetesClient.create(RbacAuthorizationV1beta1Api.class).listClusterRoleBinding().watch()
            .map(ClusterRoleBindingService::to));
        }
      ),
      Optional.empty()
    );
  }

  public Status delete(String name) throws IOException {
    return kubernetesClient.create(RbacAuthorizationV1Api.class).deleteClusterRoleBinding(name).get();
  }

  public ClusterRoleBinding update(String name, ClusterRoleBinding clusterRoleBinding) throws IOException {
    return kubernetesClient.create(RbacAuthorizationV1Api.class).replaceClusterRoleBinding(name, clusterRoleBinding).get();
  }

  static WatchEvent<ClusterRoleBinding> to(WatchEvent<com.marcnuri.yakc.model.io.k8s.api.rbac.v1beta1.ClusterRoleBinding> from) {
    return new WatchEvent<>(from.getType(), to(from.getObject()));
  }

  static ClusterRoleBinding to(com.marcnuri.yakc.model.io.k8s.api.rbac.v1beta1.ClusterRoleBinding from) {
    return ClusterRoleBinding.builder()
      .apiVersion(from.getApiVersion())
      .kind(from.getKind())
      .metadata(from.getMetadata())
      .roleRef(to(from.getRoleRef()))
      .subjects(from.getSubjects().stream().map(ClusterRoleBindingService::to).collect(Collectors.toList()))
      .build();
  }

  static RoleRef to(com.marcnuri.yakc.model.io.k8s.api.rbac.v1beta1.RoleRef from) {
    return RoleRef.builder()
      .name(from.getName())
      .build();
  }

  public static Subject to(com.marcnuri.yakc.model.io.k8s.api.rbac.v1beta1.Subject from) {
    return Subject.builder()
      .name(from.getName())
      .namespace(from.getNamespace())
      .build();
  }
}
