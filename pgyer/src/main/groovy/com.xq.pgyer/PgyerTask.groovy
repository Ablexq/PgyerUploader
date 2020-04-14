package com.xq.pgyer

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


/**
 * windows使用上传pgyer
 */
class PgyerTask extends DefaultTask {

    PgyerTask() {
        mustRunAfter(['clean', 'assemble'])
        dependsOn(['clean', 'assemble'])
        project.tasks.getByName("assemble") {
            mustRunAfter("clean")
            dependsOn("clean")
        }

        group = "publish"
    }


    @TaskAction
    void uploadPgyerByWin() {
        def pgyerExtension = project.extensions.getByName("pgyerExtension")
        println "pgyerExtension================================$pgyerExtension"
        PgyerUtils.uploadPgyer(pgyerExtension, project)
    }


}