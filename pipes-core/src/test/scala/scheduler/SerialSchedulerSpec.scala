package pipes.scheduler

class SerialSchedulerSpec extends SchedulerTests {
  def name = "Serial Scheduler"
  def scheduler = new SerialScheduler
}
