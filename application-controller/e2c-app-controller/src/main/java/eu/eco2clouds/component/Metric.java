/* 
 * Copyright 2014 Politecnico di Milano.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 *  @author: Pierluigi Plebani, Politecnico di Milano, Italy
 *  e-mail pierluigi.plebani@polimi.it

 */
package eu.eco2clouds.component;

import eu.eco2clouds.ac.monitor.Item;

/**
 *
 *  
 */
public class Metric {

    //list of metrics at vmlevel 
    public static final String VM_CPU_USAGE = "cpuusage"; 
    public static final String VM_STORAGE_USAGE = "Storage usage";
    public static final String VM_IO_USAGE = "I/O usage";
    public static final String VM_MEMORY_USAGE = "Memory usage";
    public static final String VM_POWER_CONSUMPTION = "Power consumption";
    public static final String VM_DISK_IOPS = "Disk IOPS";

    //list of metrics at hostlevel 
    public static final String PM_POWER_CONSUMPTION = "Power consumption";
    public static final String PM_DISK_IOPS = "Disk IOPS";
    public static final String PM_CPU_UTILIZATION = "CPU utilization";
    public static final String PM_AVAILABILITY = "Availability";
    public static final String PM_NO_VM = "no vm";

    //list of metrics at site level
    public static final String SITE_UTILIZATION = "Site utilization";
    public static final String SITE_STORAGE_UTILIZATION = "Storage utilization";
    public static final String SITE_AVAILABILITY = "Availability";
    public static final String SITE_PUE = "PUE";

    
    
    public static final String CPU_LOAD = "cpuload";
    public static final String CPU_UTIL = "cpuutil";
    public static final String DISK_FREE = "diskfree";
    public static final String DISK_TOTAL = "disktotal";
    public static final String DISK_USAGE = "diskusage";
    public static final String IOPS = "iops";
    public static final String IO_UTIL = "ioutil";
    public static final String MEM_FREE = "memfree";
    public static final String MEM_TOTAL = "memtotal";
    public static final String MEM_USED = "memused";
    public static final String NETIF_IN = "netifin";
    public static final String NETIF_OUT = "netifout";
    public static final String PROC_NUM = "proc_num";
    public static final String POWER = "power";
    public static final String SWAP_FREE = "swapfree";
    public static final String SWAP_TOTAL = "swaptotal";

}
