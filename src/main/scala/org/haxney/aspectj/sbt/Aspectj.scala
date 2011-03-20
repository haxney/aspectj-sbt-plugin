package org.haxney.aspectj.sbt

import _root_.sbt._
import _root_.xsbt._
import org.aspectj.tools.ajc.Main
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler

trait AspectJ extends BasicScalaProject with FileTasks with MavenStyleScalaPaths {
  lazy val aspectjTools = "org.aspectj" % "aspectjtools" % "1.6.11"
  lazy val aspectjRt = "org.aspectj"    % "aspectjrt"    % "1.6.11"
  lazy val aspectjConf = config("aspectj")

  implicit def sting2CompileOption(opts: Iterable[String]) = opts.map(CompileOption.apply)
  implicit def sting2JavaCompileOption(opts: Iterable[String]) = opts.map(JavaCompileOption.apply)

  def aspectjPaths: PathFinder = configurationClasspath(aspectjConf)
  def aspectjArgs = List("-1.5")

  /* use default compile options */
  def aspectjCompileOptions: Seq[JavaCompileOption] = javaCompileOptions ++ aspectjArgs
  def aspectjLabel = "aspectj"
  def aspectjSourcePath = mainJavaSourcePath
  def aspectjSourceRoots = (aspectjSourcePath ##)
  def aspectjSources = sources(aspectjSourceRoots)
  def aspectjCompilePath = mainCompilePath
  def aspectjAnalysisPath = mainAnalysisPath
  def aspectjClasspath = compileClasspath +++ aspectjPaths
  def aspectjCompileConfiguration = new AspectjCompileConfig
  def aspectjScalaInstance = new ScalaInstance(buildScalaInstance.version,
                                               buildScalaInstance.loader,
                                               buildScalaInstance.libraryJar,
                                               aspectjCompilePath.asFile,
                                               buildScalaInstance.extraJars)
  def aspectjBuildCompiler = new AnalyzingCompiler(aspectjScalaInstance, componentManager, log)
  def aspectjCompileConditional = new CompileConditional(aspectjCompileConfiguration, aspectjBuildCompiler)
  def aspectjCompileDescription = "Compiles Java sources with AspectJ"

  class AspectjCompileConfig extends BaseCompileConfig {
    def baseCompileOptions = compileOptions
    def label = aspectjLabel
    def sourceRoots = aspectjSourceRoots
    def sources = aspectjSources
    def outputDirectory = aspectjCompilePath
    def classpath = aspectjClasspath
    def analysisPath = aspectjAnalysisPath
    def fingerprints = Fingerprints(Nil, Nil)
    def javaOptions = ScalaProject.javaOptionsAsString(aspectjCompileOptions)
  }

  protected def aspectjAction = task {
    aspectjCompileConditional.run
    None
  } describedAs aspectjCompileDescription

  lazy val aspectj = aspectjAction
  override def compileOrder = CompileOrder.JavaThenScala
}
