package com.xq.pgyer


import org.gradle.api.Plugin
import org.gradle.api.Project

class PgyerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("pgyerExtension", PgyerExtension)
        project.task('uploadPgyer', type: PgyerTask)
    }
}