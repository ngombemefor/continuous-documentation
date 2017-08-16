package by.dev.madhead.doktor.pipeline

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.config.ConfluenceServers
import hudson.Extension
import hudson.FilePath
import hudson.model.TaskListener
import hudson.util.FormValidation
import hudson.util.ListBoxModel
import jenkins.model.GlobalConfiguration
import org.jenkinsci.plugins.workflow.steps.Step
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepDescriptor
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

class DoktorStep
@DataBoundConstructor
constructor(
	val server: String,
	val markdownIncludePattern: String,
	val markdownExcludePattern: String,
	val asciidocIncludePattern: String,
	val asciidocExcludePattern: String
) : Step() {
	override fun start(context: StepContext) = Execution(context)

	@Extension
	class DoktorStepDescriptor : StepDescriptor() {
		override fun getFunctionName() = "doktor"

		override fun getDisplayName() = Messages.doktor_pipeline_DoktorStep_displayName()

		override fun getRequiredContext() = setOf(FilePath::class.java)

		fun doFillServerItems(): ListBoxModel {
			val result = ListBoxModel()

			GlobalConfiguration.all().get(ConfluenceServers::class.java)?.servers?.forEach {
				result.add("${it.name} (${it.url})", it.name)
			}

			return result
		}
	}

	inner class Execution(context: StepContext) : SynchronousStepExecution<Unit?>(context) {
		override fun run(): Unit? {
			return context.get(TaskListener::class.java)?.logger?.println("Server: ${server}, +MD: ${markdownIncludePattern}, -MD: ${markdownExcludePattern}, +AD: ${asciidocIncludePattern}, -AD: ${asciidocExcludePattern}")
		}
	}
}
