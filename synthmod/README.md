Setup: for drawing you will need to link the Java cross-platform OpenGL media libraries into your java extensions. [This blog post](https://mostlymaths.net/2018/03/processing-and-scala.html/) provides some details -- in summary, find the relevant PATH by running `scala` to open a scala shell and doing:

```
System.getProperty("java.library.path")
```

then, downoad the jogamp library for all platforms from [here](https://jogamp.org/wiki/index.php/Downloading_and_installing_JOGL#Using_the_7z_jogamp-all-platforms_archive), and move the `.jnilib` (Mac) or `.dll` (Windows) files to the relevant folder.

Processing's core JAR is included in the `lib` folder of this project and shouldn't require any additional configuration.

---

After that system specific setup is complete, to start: make sure you have Scala/SBT installed, then run `sbt` in this directory. In the `sbt>` shell prompt, run `run` and the project should start - starting SBT and the fresh compile may take a while, but should be much faster on subsequent runs. VSCode provides a fantastic quick edit/compile loop with the `Metals` scala extension, which will pre-emptively compile your project on file save, and uses indexed builds to make everything quite zippy.
