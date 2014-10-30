=== ECO2Clouds Accounting Service Monitoring Collector ===
=== 2013/08/23 ===

=== Overview ===

***This section to be moved into main implementation doc***

The MonitoringCollector object contains a MMQMonitor and a MetricsCollector. Each runs in their own independent thread with no direct interaction.

MonitoringCollector:
  * Loads program configuration from MonitoringCollector.properties.
  * Performs an initial population of the database with infrastructure (site and host) records.
  * Creates a MMQMonitor and a MetricsCollector and starts them in their own independent threads.

MMQMonitor:
  * Contains a DatabaseConnector object representing its own connection to the MySQL database.
  * Subscribes to certain topics on the Management Message Queue.
  * Waits for a message to appear on the MMQ.
  * Filters message by relevance.
  * Takes action if appropriate. Such as adds a new ECO2Clouds experiment record to the database.
  * Waits for a message to appear on the MMQ.
  * Loops until signaled to stop.

MetricsCollector:
  * Contains a DatabaseConnector object representing its own connection to the MySQL database.
  * Checks for any infrastructure changes (new host added, host down, etc.) and adds to the database.
  * Gathers metrics for all sites and updates database.
  * Gathers metrics for all hosts and updates database. ***Currently uses dummy generated values as BonFIRE metrics API does not support getting these metrics.***
  * Gathers metrics for all experiments (Application layer) and updates database. ***Currently uses dummy generated values as BonFIRE metrics API does not support getting these metrics.***
  * Gathers metrics for all virtual machines and updates database.
  * Sleeps (default polling rate is 10s).
  * Loops until signaled to stop.

Uses metrics API http://wiki.bonfire-project.eu/index.php/Monitoring_Ecometrics_specs.
Current issues:
  * No provision for physical machine/host metrics.
  * No provision for application layer metrics.
  * Gathering requires one network transaction per metric per resource leading to an excessive number of API calls and serious quality of service issues.

=== Requirements ===

Developed using:

 * Java v1.7
 * MySQL Server v5.6 (with schema from included src/main/schema_create_script.sql)

Built using:

 * apache-maven-3.1.0 for windows

=== Usage ===

Use command:

  mvn install

to produce a JAR file containing all dependencies in the /target/ directory.

Place this JAR and the accompanying MonitoringCollector.properties configuration file in the same directory.

Run using:

  java -jar <name-of-jar-file>

Application entry point is the MainLauncher class. It creates a MonitoringCollector object and starts it asynchronously. The main thread then waits for a user to signal shutdown by pressing Enter ***Subject to change following integration***.

=== Configuration ===

User configurable values are stored in MonitoringCollector.properties. Detailed information on each parameter can be found in /doc/eu/eco2clouds/accounting/monitoringcollector/ConfigurationValues.html

=== Testing ===

Command:

  mvn test

runs all unit tests and with expected output:

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running eu.eco2clouds.accounting.monitoringcollector.tests.TestSuite
Tests run: 41, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.013 sec

Results :

Tests run: 41, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.078s
[INFO] Finished at: Fri Aug 23 14:04:49 BST 2013
[INFO] Final Memory: 6M/15M
[INFO] ------------------------------------------------------------------------