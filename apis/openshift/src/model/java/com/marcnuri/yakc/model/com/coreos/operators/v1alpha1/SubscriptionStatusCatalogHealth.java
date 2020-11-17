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
 */

package com.marcnuri.yakc.model.com.coreos.operators.v1alpha1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marcnuri.yakc.model.Model;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * SubscriptionCatalogHealth describes the health of a CatalogSource the Subscription knows about.
 */
@SuppressWarnings({"squid:S1192", "WeakerAccess", "unused"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class SubscriptionStatusCatalogHealth implements Model {


  @NonNull
  @JsonProperty("catalogSourceRef")
  private SubscriptionStatusCatalogSourceRef catalogSourceRef;

  /**
   * Healthy is true if the CatalogSource is healthy; false otherwise.
   */
  @NonNull
  @JsonProperty("healthy")
  private Boolean healthy;

  /**
   * LastUpdated represents the last time that the CatalogSourceHealth changed
   */
  @NonNull
  @JsonProperty("lastUpdated")
  private OffsetDateTime lastUpdated;

}

