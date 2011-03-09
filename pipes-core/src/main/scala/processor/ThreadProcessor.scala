package pipes.processor


trait ThreadProcessor[In, Out] extends Processor[In, Out] {

  /*
  def drain: Traversable[Out] = Nil

  def newThread(runnable: Runnable): Thread = {
    val thread = new Thread(runnable)
    thread.setName("[ThreadProcessor]: %s".format(this.toString))
    return thread
  }

  protected def defaultPut(out: Out) = outputQueue.put(Some(out))
  def run[T](func: ⇒ T): Traversable[Out] = runWithPut(defaultPut _)(func)

  def runWithPut[T](put: Out ⇒ Unit)(func: ⇒ T): Traversable[Out] = {
    val thread = newThread(new Runnable {
      def run = {
        Stream.continually(inputQueue.take())
              .takeWhile(_ != None)
              .foreach(v ⇒ process(v.get, put))
        outputQueue.put(None)
      }
    })

    thread.start()
    func
    inputQueue.put(None)

    Stream.continually(outputQueue.take())
          .takeWhile(_ != None)
          .map(_.get) ++ drain ++ {
            thread.join()
            Stream.empty[Out]
          }.toList
  }
  */
}


trait QueuedThreadProcessor[In, Out] extends ThreadProcessor[In, Out]
                                     with QueuedProcessor[In, Out]
