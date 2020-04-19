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
 * Created on 2020-04-05, 19:33
 */
package com.marcnuri.yack.schema.api;

import com.marcnuri.yack.schema.GeneratorSettings;
import io.swagger.v3.oas.models.OpenAPI;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.openapitools.codegen.config.CodegenConfigurator;

import java.io.File;

/**
 * Created by Marc Nuri on 2020-04-05.
 */
public class ApiGeneratorTask extends DefaultTask {
  @Input
  public String packageName;
  @Input
  public File schema;
  @Input
  public File templatesDir;
  @Input
  public File outputDirectory;

  @TaskAction
  public void run() {
    final CodegenConfigurator configurator = new CodegenConfigurator();
    configurator.setInputSpec(schema.getAbsolutePath());
    configurator.setGeneratorName("java");
    final OpenAPI openAPI = (OpenAPI)configurator.toContext().getSpecDocument();
    final GeneratorSettings settings = GeneratorSettings.builder()
        .openAPI(openAPI)
        .logger(getLogger())
        .packageName(packageName)
        .schema(schema.toPath())
        .templatesDir(templatesDir.toPath())
        .outputDirectory(outputDirectory.toPath())
        .sourceDirectory(outputDirectory.toPath().resolve("src").resolve("api").resolve("java"))
        .build();
    new ApiGenerator(settings).generate();
    getLogger().lifecycle("Generation completed");
  }

}
